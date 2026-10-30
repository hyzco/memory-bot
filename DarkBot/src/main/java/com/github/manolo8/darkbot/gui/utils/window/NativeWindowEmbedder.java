package com.github.manolo8.darkbot.gui.utils.window;

import com.github.manolo8.darkbot.gui.astral.AstralControlGui;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;

import java.awt.Canvas;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Reparents a real Win32 window (e.g. KekkaRunner.exe's game window) into an AWT Canvas,
 * so it renders embedded inside our own Swing UI instead of as a separate OS window.
 */
public class NativeWindowEmbedder {

    private static volatile WinDef.HWND embeddedChild;
    private static volatile Canvas embeddedTarget;
    /** True when embeddedChild is tracked by position-sync (dock) rather than true SetParent reparenting. */
    private static volatile boolean docked;
    private static volatile boolean needsRaise = true;
    private static volatile boolean wantVisible = true;
    private static final java.util.concurrent.atomic.AtomicBoolean WATCHDOG_STARTED = new java.util.concurrent.atomic.AtomicBoolean();
    private static final java.util.concurrent.atomic.AtomicBoolean EMBED_WATCHDOG_STARTED = new java.util.concurrent.atomic.AtomicBoolean();
    private static volatile javax.swing.Timer dockResyncTimer;

    /**
     * Waits for {@link AstralControlGui} to exist (it may not be constructed yet when the hosted
     * process is first launched), then polls for a visible top-level window owned by {@code pid}
     * and embeds it into the control GUI's game canvas once found. Runs on a background daemon
     * thread; safe to call repeatedly (e.g. on every relaunch/refresh of the hosted process).
     */
    public static void embedAsync(long pid) {
        Thread t = new Thread(() -> {
            try {
                System.out.println("NativeWindowEmbedder: waiting for AstralControlGui (pid=" + pid + ")");
                AstralControlGui gui = waitForGui(20_000);
                if (gui == null) {
                    System.out.println("NativeWindowEmbedder: AstralControlGui never became available, aborting");
                    return;
                }

                Canvas canvas = gui.getGameCanvas();
                if (!waitForDisplayable(canvas, 20_000)) {
                    System.out.println("NativeWindowEmbedder: game canvas never became displayable, aborting");
                    return;
                }

                System.out.println("NativeWindowEmbedder: searching for a visible top-level window owned by pid " + pid);
                WinDef.HWND hwnd = findWindowByPid(pid, 20_000);
                if (hwnd == null) {
                    System.out.println("NativeWindowEmbedder: no window found for pid " + pid + " within timeout");
                    return;
                }

                char[] titleBuf = new char[512];
                User32.INSTANCE.GetWindowText(hwnd, titleBuf, titleBuf.length);
                String title = Native.toString(titleBuf);
                System.out.println("NativeWindowEmbedder: found hwnd=" + hwnd + " title=\"" + title + "\", embedding now");

                embed(hwnd, canvas);
                System.out.println("NativeWindowEmbedder: embed() call completed, canvas size=" + canvas.getWidth() + "x" + canvas.getHeight());
            } catch (Throwable e) {
                System.out.println("NativeWindowEmbedder: unexpected failure");
                e.printStackTrace();
            }
        }, "Window embedder");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Same as {@link #embedAsync(long)} but for in-process native windows (e.g. KekkaPlayer, which
     * is loaded via JNI into our own JVM rather than a separate process) - PID matching would catch
     * every one of our own Swing windows too, so this matches on a distinctive window title substring
     * instead.
     */
    public static void embedByTitleAsync(String titleSubstring) {
        startEmbedWatchdog(titleSubstring);
        Thread t = new Thread(() -> {
            try {
                System.out.println("NativeWindowEmbedder: waiting for AstralControlGui (title match \"" + titleSubstring + "\")");
                AstralControlGui gui = waitForGui(20_000);
                if (gui == null) {
                    System.out.println("NativeWindowEmbedder: AstralControlGui never became available, aborting");
                    return;
                }

                Canvas canvas = gui.getGameCanvas();
                if (!waitForDisplayable(canvas, 20_000)) {
                    System.out.println("NativeWindowEmbedder: game canvas never became displayable, aborting");
                    return;
                }

                System.out.println("NativeWindowEmbedder: searching for a visible window titled like \"" + titleSubstring + "\"");
                WinDef.HWND hwnd = findWindowByTitle(titleSubstring, 30_000);
                if (hwnd == null) {
                    System.out.println("NativeWindowEmbedder: no window titled like \"" + titleSubstring + "\" found within timeout");
                    return;
                }

                char[] titleBuf = new char[512];
                User32.INSTANCE.GetWindowText(hwnd, titleBuf, titleBuf.length);
                String title = Native.toString(titleBuf);
                System.out.println("NativeWindowEmbedder: found hwnd=" + hwnd + " title=\"" + title + "\", embedding now");

                embed(hwnd, canvas);
                System.out.println("NativeWindowEmbedder: embed() call completed, canvas size=" + canvas.getWidth() + "x" + canvas.getHeight());
            } catch (Throwable e) {
                System.out.println("NativeWindowEmbedder: unexpected failure");
                e.printStackTrace();
            }
        }, "Window embedder (title)");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Watches for a new KekkaPlayer window appearing after a game restart and re-embeds it automatically.
     * Runs once per session; safe to call multiple times (no-op after first call).
     */
    private static void startEmbedWatchdog(String titleSubstring) {
        if (!EMBED_WATCHDOG_STARTED.compareAndSet(false, true)) return;
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Canvas canvas = embeddedTarget;
                    if (canvas == null) {
                        AstralControlGui gui = waitForGui(1);
                        if (gui == null) continue;
                        canvas = gui.getGameCanvas();
                        if (canvas == null || !canvas.isDisplayable() || canvas.getWidth() <= 0) continue;
                    }
                    WinDef.HWND current = embeddedChild;
                    // If currently embedded window is still valid, nothing to do
                    if (current != null && User32.INSTANCE.IsWindow(current)) continue;
                    // Look for a new window with the title
                    WinDef.HWND found = findWindowOnce(titleSubstring, null);
                    if (found == null) continue;
                    System.out.println("NativeWindowEmbedder: new game window " + found + " detected, re-embedding");
                    embed(found, canvas);
                } catch (InterruptedException e) {
                    return;
                } catch (Throwable e) {
                    System.out.println("NativeWindowEmbedder: embed watchdog error: " + e);
                }
            }
        }, "Window embed watchdog");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Like {@link #embedByTitleAsync(String)}, but instead of true SetParent reparenting, keeps the
     * window top-level and continuously repositions/resizes it to sit exactly over the canvas
     * (borderless, no title bar). Reparenting a DirectX-rendering window (e.g. KekkaPlayer, which
     * uses D3D9) can break its swap chain and leave it black; this avoids touching its window
     * hierarchy at all, which is the safer option for D3D content.
     */
    public static void dockByTitleAsync(String titleSubstring) {
        startWatchdog(titleSubstring);
        Thread t = new Thread(() -> {
            try {
                System.out.println("NativeWindowEmbedder: waiting for AstralControlGui (dock, title match \"" + titleSubstring + "\")");
                AstralControlGui gui = waitForGui(20_000);
                if (gui == null) {
                    System.out.println("NativeWindowEmbedder: AstralControlGui never became available, aborting");
                    return;
                }

                Canvas canvas = gui.getGameCanvas();
                if (!waitForDisplayable(canvas, 20_000)) {
                    System.out.println("NativeWindowEmbedder: game canvas never became displayable, aborting");
                    return;
                }

                System.out.println("NativeWindowEmbedder: searching for a visible window titled like \"" + titleSubstring + "\"");
                WinDef.HWND hwnd = findWindowByTitle(titleSubstring, 30_000);
                if (hwnd == null) {
                    System.out.println("NativeWindowEmbedder: no window titled like \"" + titleSubstring + "\" found within timeout");
                    return;
                }

                char[] titleBuf = new char[512];
                User32.INSTANCE.GetWindowText(hwnd, titleBuf, titleBuf.length);
                String title = Native.toString(titleBuf);
                System.out.println("NativeWindowEmbedder: found hwnd=" + hwnd + " title=\"" + title + "\", docking now");

                dock(hwnd, canvas);
                System.out.println("NativeWindowEmbedder: dock() call completed");
            } catch (Throwable e) {
                System.out.println("NativeWindowEmbedder: unexpected failure");
                e.printStackTrace();
            }
        }, "Window docker (title)");
        t.setDaemon(true);
        t.start();
    }

    /**
     * The game window is recreated whenever the game reloads (refresh, relog, captcha/popup recovery); the new
     * window comes up as a loose top-level window with its own title bar. Watch for any game window that isn't the
     * docked one and dock it too, so the game never ends up floating outside the bot window.
     */
    private static void startWatchdog(String titleSubstring) {
        if (!WATCHDOG_STARTED.compareAndSet(false, true)) return;
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Canvas canvas = embeddedTarget;
                    if (canvas == null) {
                        AstralControlGui gui = waitForGui(1);
                        if (gui == null) continue;
                        canvas = gui.getGameCanvas();
                        if (canvas == null || !canvas.isDisplayable() || canvas.getWidth() <= 0) continue;
                    }
                    WinDef.HWND current = embeddedChild;
                    WinDef.HWND found = findWindowOnce(titleSubstring, current);
                    if (found == null) continue;
                    System.out.println("NativeWindowEmbedder: game window " + found + " is not docked, docking it");
                    dock(found, canvas);
                    User32.INSTANCE.ShowWindow(found, wantVisible ? WinUser.SW_SHOW : WinUser.SW_HIDE);
                } catch (InterruptedException e) {
                    return;
                } catch (Throwable e) {
                    System.out.println("NativeWindowEmbedder: watchdog error: " + e);
                }
            }
        }, "Window dock watchdog");
        t.setDaemon(true);
        t.start();
    }

    /** First visible window titled like {@code substring} that isn't {@code except} (or null). */
    private static WinDef.HWND findWindowOnce(String substring, WinDef.HWND except) {
        String needle = substring.toLowerCase();
        WinDef.HWND[] found = new WinDef.HWND[1];
        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            if (except != null && hWnd.equals(except) && User32.INSTANCE.IsWindow(except)) return true;
            if (!User32.INSTANCE.IsWindowVisible(hWnd) || !isOwnWindow(hWnd)) return true;
            char[] titleBuf = new char[256];
            User32.INSTANCE.GetWindowText(hWnd, titleBuf, titleBuf.length);
            String title = Native.toString(titleBuf);
            if (title != null && title.toLowerCase().contains(needle)) {
                found[0] = hWnd;
                return false;
            }
            return true;
        }, null);
        return found[0];
    }

    private static final long OWN_PID = ProcessHandle.current().pid();

    /**
     * The game window is created inside this JVM (KekkaPlayer is loaded in-process), so it always belongs to our own
     * process. With two bots running, matching by title alone made each one grab the other's game window and the two
     * kept stealing it back (screens switching and flickering).
     */
    private static boolean isOwnWindow(WinDef.HWND hWnd) {
        IntByReference pid = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hWnd, pid);
        return pid.getValue() == OWN_PID;
    }

    private static WinDef.HWND findWindowByTitle(String substring, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        String needle = substring.toLowerCase();
        WinDef.HWND[] found = new WinDef.HWND[1];

        while (System.currentTimeMillis() < deadline) {
            found[0] = null;
            User32.INSTANCE.EnumWindows((hWnd, data) -> {
                if (!User32.INSTANCE.IsWindowVisible(hWnd) || !isOwnWindow(hWnd)) return true;
                char[] titleBuf = new char[512];
                User32.INSTANCE.GetWindowText(hWnd, titleBuf, titleBuf.length);
                String title = Native.toString(titleBuf);
                if (title != null && title.toLowerCase().contains(needle)) {
                    found[0] = hWnd;
                    return false;
                }
                return true;
            }, null);

            if (found[0] != null) return found[0];

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    private static boolean waitForDisplayable(Canvas canvas, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (canvas.isDisplayable() && canvas.getWidth() > 0 && canvas.getHeight() > 0) return true;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return canvas.isDisplayable() && canvas.getWidth() > 0 && canvas.getHeight() > 0;
    }

    private static AstralControlGui waitForGui(long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            AstralControlGui gui = AstralControlGui.getInstance();
            if (gui != null) return gui;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return AstralControlGui.getInstance();
    }

    private static WinDef.HWND findWindowByPid(long pid, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        WinDef.HWND[] found = new WinDef.HWND[1];

        while (System.currentTimeMillis() < deadline) {
            found[0] = null;
            User32.INSTANCE.EnumWindows((hWnd, data) -> {
                IntByReference pidRef = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(hWnd, pidRef);
                if (pidRef.getValue() == pid && User32.INSTANCE.IsWindowVisible(hWnd)) {
                    found[0] = hWnd;
                    return false;
                }
                return true;
            }, null);

            if (found[0] != null) return found[0];

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    private static synchronized void embed(WinDef.HWND child, Canvas target) {
        com.sun.jna.Pointer parentPointer = Native.getComponentPointer(target);
        System.out.println("NativeWindowEmbedder: canvas native pointer=" + parentPointer);
        if (parentPointer == null) {
            System.out.println("NativeWindowEmbedder: canvas has no native peer, cannot embed");
            return;
        }
        WinDef.HWND parent = new WinDef.HWND(parentPointer);

        int style = User32.INSTANCE.GetWindowLong(child, WinUser.GWL_STYLE);
        style &= ~(WinUser.WS_CAPTION | WinUser.WS_THICKFRAME | WinUser.WS_SYSMENU
                | WinUser.WS_MINIMIZEBOX | WinUser.WS_MAXIMIZEBOX | WinUser.WS_POPUP);
        style |= WinUser.WS_CHILD | WinUser.WS_VISIBLE;
        User32.INSTANCE.SetWindowLong(child, WinUser.GWL_STYLE, style);

        Extra.INSTANCE.SetWindowTextW(child, "");

        WinDef.HWND prevParent = User32.INSTANCE.SetParent(child, parent);
        if (prevParent == null) {
            System.out.println("NativeWindowEmbedder: SetParent FAILED, GetLastError=" + Native.getLastError());
        } else {
            System.out.println("NativeWindowEmbedder: SetParent succeeded, previous parent=" + prevParent);
        }

        embeddedChild = child;
        embeddedTarget = target;
        docked = false;
        resize();

        target.removeComponentListener(RESIZE_LISTENER);
        target.addComponentListener(RESIZE_LISTENER);
    }

    private static synchronized void dock(WinDef.HWND child, Canvas target) {
        int style = User32.INSTANCE.GetWindowLong(child, WinUser.GWL_STYLE);
        style &= ~(WinUser.WS_CAPTION | WinUser.WS_THICKFRAME | WinUser.WS_SYSMENU
                | WinUser.WS_MINIMIZEBOX | WinUser.WS_MAXIMIZEBOX);
        style |= WinUser.WS_POPUP | WinUser.WS_VISIBLE;
        User32.INSTANCE.SetWindowLong(child, WinUser.GWL_STYLE, style);

        // Make the game window OWNED by our frame: Windows keeps owned windows above their owner, so the
        // (black) canvas can't cover it, and it follows the frame on minimize/restore. No reparenting,
        // so the game's D3D surface is untouched.
        try {
            Window frame = javax.swing.SwingUtilities.getWindowAncestor(target);
            com.sun.jna.Pointer framePtr = frame == null ? null : Native.getComponentPointer(frame);
            if (framePtr != null) {
                User32.INSTANCE.SetWindowLongPtr(child, -8 /* GWLP_HWNDPARENT */, framePtr);
                System.out.println("NativeWindowEmbedder: game window now owned by frame " + framePtr);
            }
        } catch (Throwable t) {
            System.out.println("NativeWindowEmbedder: could not set owner: " + t);
        }

        embeddedChild = child;
        embeddedTarget = target;
        docked = true;
        syncDockPosition();

        target.removeComponentListener(RESIZE_LISTENER);
        target.addComponentListener(RESIZE_LISTENER);

        Window topWindow = javax.swing.SwingUtilities.getWindowAncestor(target);
        if (topWindow != null) {
            topWindow.removeComponentListener(MOVE_LISTENER);
            topWindow.addComponentListener(MOVE_LISTENER);
        }

        if (dockResyncTimer != null) dockResyncTimer.stop();
        dockResyncTimer = new javax.swing.Timer(500, e -> syncDockPosition());
        dockResyncTimer.start();
    }

    /** Client-area size of the embedded game window (what the game stage is rendered at), or null if not embedded. */
    public static int[] getClientSize() {
        WinDef.HWND child = embeddedChild;
        if (child == null) return null;
        WinDef.RECT r = new WinDef.RECT();
        if (!User32.INSTANCE.GetClientRect(child, r)) return null;
        int w = r.right - r.left, h = r.bottom - r.top;
        return w > 0 && h > 0 ? new int[]{w, h} : null;
    }

    /** Last time the player themselves pressed a key / mouse button in the bot or game window (real input only). */
    public static volatile long lastUserInputAt;
    private static final long USER_INPUT_YIELD_MS = 350;

    public static void markUserInput() {
        lastUserInputAt = System.currentTimeMillis();
    }

    private static volatile long lastEntityClick;
    /** A second click on a ship soon after the first is a double-click: the game flies to it and attacks. */
    private static final long ENTITY_CLICK_GAP_MS = 800;

    public static boolean entityClickCooling() {
        return System.currentTimeMillis() - lastEntityClick < ENTITY_CLICK_GAP_MS;
    }

    /**
     * Click meant to SELECT a ship. All select clicks (the bot's own select path and Tutucu's fallback) go through
     * here so two of them can never land close together and register as a double-click.
     */
    public static boolean clickEntity(int x, int y) {
        long now = System.currentTimeMillis();
        if (now - lastEntityClick < ENTITY_CLICK_GAP_MS) return false;
        // The player is clicking right now: never fight their hand for the mouse.
        if (now - lastUserInputAt < USER_INPUT_YIELD_MS) return false;
        if (!clickGame(x, y)) return false;
        lastEntityClick = now;
        return true;
    }

    /**
     * Left-clicks the game window at client coordinates via window messages (what a mouse does), without going
     * through KekkaPlayer's native input, which can hard-crash the JVM. Returns false if no game window is docked.
     */
    public static boolean clickGame(int x, int y) {
        if (embeddedChild == null) return false;
        WinDef.RECT bounds = new WinDef.RECT();
        if (!User32.INSTANCE.GetClientRect(embeddedChild, bounds)) return false;
        if (x < 0 || y < 0 || x >= bounds.right - bounds.left || y >= bounds.bottom - bounds.top) return false;
        // The press/release pauses are sleeps; keep them off the bot's tick thread.
        CLICKER.execute(() -> clickGameNow(x, y));
        return true;
    }

    private static final java.util.concurrent.ExecutorService CLICKER = java.util.concurrent.Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "game-clicker");
        t.setDaemon(true);
        return t;
    });

    private static boolean clickGameNow(int x, int y) {
        WinDef.HWND root = embeddedChild;
        if (root == null) return false;
        WinDef.RECT r = new WinDef.RECT();
        if (!User32.INSTANCE.GetClientRect(root, r)) return false;
        if (x < 0 || y < 0 || x >= r.right - r.left || y >= r.bottom - r.top) return false;

        WinDef.HWND target = root;
        try {
            WinDef.HWND child = Extra.INSTANCE.ChildWindowFromPoint(root, ((long) y << 32) | (x & 0xFFFFFFFFL));
            if (child != null && !child.equals(root)) target = child;
        } catch (RuntimeException ignored) {
            // root is fine
        }

        // Child windows want coordinates relative to themselves.
        WinDef.POINT pt = new WinDef.POINT(x, y);
        pt.write();
        if (!target.equals(root)) Extra.INSTANCE.MapWindowPoints(root, target, pt.getPointer(), 1);
        pt.read();

        WinDef.LPARAM pos = new WinDef.LPARAM(((long) pt.y << 16) | (pt.x & 0xFFFF));
        System.out.println("clickGame: " + x + "," + y + " client=" + (r.right - r.left) + "x" + (r.bottom - r.top)
                + " visible=" + User32.INSTANCE.IsWindowVisible(root) + " hwnd=" + target
                + (target.equals(root) ? " (root)" : " (child " + pt.x + "," + pt.y + ")"));
        User32.INSTANCE.PostMessage(target, 0x0007 /* WM_SETFOCUS */, new WinDef.WPARAM(0), new WinDef.LPARAM(0));
        User32.INSTANCE.PostMessage(target, 0x0200 /* WM_MOUSEMOVE */, new WinDef.WPARAM(0), pos);
        sleepQuietly(6);
        User32.INSTANCE.PostMessage(target, 0x0201 /* WM_LBUTTONDOWN */, new WinDef.WPARAM(1), pos);
        sleepQuietly(10);
        User32.INSTANCE.PostMessage(target, 0x0202 /* WM_LBUTTONUP */, new WinDef.WPARAM(0), pos);
        return true;
    }

    /** True when the game window is docked and can take window-message input. */
    public static boolean canPostInput() {
        return embeddedChild != null;
    }

    private static WinDef.HWND inputTarget() {
        WinDef.HWND root = embeddedChild;
        if (root == null) return null;
        WinDef.RECT r = new WinDef.RECT();
        if (!User32.INSTANCE.GetClientRect(root, r)) return root;
        try {
            WinDef.HWND child = Extra.INSTANCE.ChildWindowFromPoint(root,
                    (((long) ((r.bottom - r.top) / 2)) << 32) | ((r.right - r.left) / 2 & 0xFFFFFFFFL));
            if (child != null && !child.equals(root)) return child;
        } catch (RuntimeException ignored) {
            // root is fine
        }
        return root;
    }

    /** Presses and releases a key in the game window via window messages (KekkaPlayer's native keyClick can crash). */
    public static void pressKey(char ch) {
        if (embeddedChild == null) return;
        CLICKER.execute(() -> {
            WinDef.HWND target = inputTarget();
            if (target == null) return;
            int vk = Extra.INSTANCE.VkKeyScanW(ch) & 0xFF;
            int scan = Extra.INSTANCE.MapVirtualKeyW(vk, 0);
            long down = 1L | ((long) scan << 16);
            long up = down | (1L << 30) | (1L << 31);
            User32.INSTANCE.PostMessage(target, 0x0100 /* WM_KEYDOWN */, new WinDef.WPARAM(vk), new WinDef.LPARAM(down));
            User32.INSTANCE.PostMessage(target, 0x0102 /* WM_CHAR */, new WinDef.WPARAM(ch), new WinDef.LPARAM(down));
            sleepQuietly(12);
            User32.INSTANCE.PostMessage(target, 0x0101 /* WM_KEYUP */, new WinDef.WPARAM(vk), new WinDef.LPARAM(up));
        });
    }

    public static void typeText(String text) {
        if (embeddedChild == null || text == null) return;
        CLICKER.execute(() -> {
            WinDef.HWND target = inputTarget();
            if (target == null) return;
            for (char c : text.toCharArray()) {
                User32.INSTANCE.PostMessage(target, 0x0102 /* WM_CHAR */, new WinDef.WPARAM(c), new WinDef.LPARAM(1));
                sleepQuietly(4);
            }
        });
    }

    /** Raw mouse message at game-window client coordinates; kind: 0 move, 1 down, 2 up. */
    public static void postMouse(int kind, int x, int y) {
        if (embeddedChild == null) return;
        CLICKER.execute(() -> {
            WinDef.HWND target = inputTarget();
            if (target == null) return;
            WinDef.LPARAM pos = new WinDef.LPARAM(((long) y << 16) | (x & 0xFFFF));
            int msg = kind == 0 ? 0x0200 : kind == 1 ? 0x0201 : 0x0202;
            User32.INSTANCE.PostMessage(target, msg, new WinDef.WPARAM(kind == 1 ? 1 : 0), pos);
        });
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Win32 calls missing from JNA's User32 mapping. POINT is passed by value as one packed 64-bit register. */
    private interface Extra extends com.sun.jna.Library {
        Extra INSTANCE = Native.load("user32", Extra.class);

        WinDef.HWND ChildWindowFromPoint(WinDef.HWND parent, long packedPoint);

        short VkKeyScanW(char ch);

        int MapVirtualKeyW(int code, int mapType);

        int MapWindowPoints(WinDef.HWND from, WinDef.HWND to, com.sun.jna.Pointer points, int count);

        boolean SetWindowTextW(WinDef.HWND hwnd, String text);
    }

    private static void resize() {
        if (docked) {
            syncDockPosition();
            return;
        }
        WinDef.HWND child = embeddedChild;
        Canvas target = embeddedTarget;
        if (child == null || target == null) return;

        User32.INSTANCE.MoveWindow(child, 0, 0,
                Math.max(target.getWidth(), 1), Math.max(target.getHeight(), 1), true);
    }

    private static void syncDockPosition() {
        WinDef.HWND child = embeddedChild;
        Canvas target = embeddedTarget;
        if (child == null || target == null || !target.isShowing()) return;

        try {
            Point screenPos = target.getLocationOnScreen();
            int w = Math.max(target.getWidth(), 1), h = Math.max(target.getHeight(), 1);
            // Only raise on the first sync after docking / re-showing; raising every tick would put the
            // game above dialogs (e.g. Tutucu settings) that belong to the same frame.
            boolean raise = needsRaise;
            needsRaise = false;
            User32.INSTANCE.SetWindowPos(child, raise ? new WinDef.HWND(new com.sun.jna.Pointer(0)) : null,
                    screenPos.x, screenPos.y, w, h,
                    WinUser.SWP_NOACTIVATE | (raise ? WinUser.SWP_SHOWWINDOW : WinUser.SWP_NOZORDER));
        } catch (IllegalComponentStateException ignored) {
            // canvas not showing right now; next resync will catch up
        }
    }

    /**
     * The embedded/docked window is a raw Win32 window, not a real Swing component, so Swing hiding
     * its container (e.g. switching away from its tab) does NOT hide it automatically - it keeps
     * rendering on top of whatever else occupies that screen area. Callers must explicitly show/hide
     * it in sync with whether its container is actually the visible one (e.g. on tab change).
     */
    public static void setEmbeddedVisible(boolean visible) {
        wantVisible = visible;
        WinDef.HWND child = embeddedChild;
        if (child == null) return;
        if (visible) needsRaise = true;
        User32.INSTANCE.ShowWindow(child, visible ? WinUser.SW_SHOW : WinUser.SW_HIDE);
    }

    private static final ComponentAdapter RESIZE_LISTENER = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            resize();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            resize();
        }
    };

    private static final ComponentAdapter MOVE_LISTENER = new ComponentAdapter() {
        @Override
        public void componentMoved(ComponentEvent e) {
            syncDockPosition();
        }

        @Override
        public void componentResized(ComponentEvent e) {
            syncDockPosition();
        }
    };
}
