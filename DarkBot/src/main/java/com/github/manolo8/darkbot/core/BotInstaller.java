package com.github.manolo8.darkbot.core;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.api.Capability;
import com.github.manolo8.darkbot.core.itf.Manager;
import com.github.manolo8.darkbot.core.utils.Lazy;
import eu.darkbot.api.API;
import eu.darkbot.util.Timer;

import static com.github.manolo8.darkbot.Main.API;

public class BotInstaller implements API.Singleton {
    private static final byte[] bytesToMainApplication =
            new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0,
                    0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0};

    private static final byte[] bytesToSettings =
            new byte[]{0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0, 5};

    public static byte[] getMainApplicationPattern() {
        return bytesToMainApplication.clone();
    }

    // Shortened from 180s/300s/2 hits (up to ~6min total) so a frozen initial load self-heals in
    // under a minute instead of requiring a manual relaunch.
    private static final long INVALID_TIMEOUT_WITH_READ = 45_000;
    private static final long INVALID_TIMEOUT_NO_READ = 60_000;
    private static final int MAX_INVALID_TIMEOUT_HITS = 1;

    public static long SCRIPT_OBJECT_VTABLE, STRING_OBJECT_VTABLE;
    public final Lazy<Long> mainApplicationAddress = new Lazy<>();
    public final Lazy<Boolean> invalid = new Lazy<>(true);
    public final Lazy<Long> mainAddress = new Lazy<>();
    public final Lazy<Long> screenManagerAddress = new Lazy<>();
    public final Lazy<Long> guiManagerAddress = new Lazy<>();
    public final Lazy<Long> heroInfoAddress = new Lazy<>();
    public final Lazy<Long> settingsAddress = new Lazy<>();
    public final Lazy<Long> connectionManagerAddress = new Lazy<>();
    public static int SEP;

    private final Timer invalidTimer = Timer.get();

    // Flash-side objects are still settling right after a reload; native signature checks on them
    // are prone to reading freed/half-constructed memory, so gate first-use checks for a bit.
    public static volatile long lastValidAt = 0;
    public static final long SETTLE_MS = 400;

    public static boolean isSettled() {
        return System.currentTimeMillis() - lastValidAt >= SETTLE_MS;
    }

    public BotInstaller() {
        this.invalid.add(value -> {
            if (!value) {
                invalidTimer.disarm();
                lastValidAt = System.currentTimeMillis();
            }
        });
    }

    public void install(Manager... managers) {
        for (Manager manager : managers)
            manager.install(this);
    }

    public boolean isInvalid() {
        if (invalid.get()) {
            checkInvalid();
            invalid.send(tryInstall());
            return true;
        }

        if (API.isValid() && API.readLong(mainApplicationAddress.get() + 1344) == mainAddress.get()) {
            if (heroInfoAddress.get() == 0) checkUserData();

            if (connectionManagerAddress.get() == 0) {
                long connMgr = API.readLong(mainAddress.get() + 560);
                if (connMgr != 0) connectionManagerAddress.send(connMgr);
            }
            return false;
        }

        invalid.send(true);
        // Keep bot in invalid state until core pointers are resolved.
        return true;
    }

    private void checkUserData() {
        int heroId = API.readInt(API.readLong(screenManagerAddress.get() + 240) + 56);
        if (heroId == 0) return;

        long address = API.searchClassClosure(closure -> {
            if (heroId != API.readInt(closure + 0x30)) return false;

            int level = API.readInt(closure + 0x34);
            // speed is updated few seconds later, basically heroId alone is enough
            // int speed = API.readMemoryInt(closure + 56);
            int bool = API.readInt(closure + 0x3c);
            int val = API.readInt(closure + 0x40);
            int cargo = API.readBindableInt(closure, 0x148);
            int maxCargo = API.readBindableInt(closure, 0x150);

            return level >= 0 && level <= 100
                   //&& speed > 50 && speed < 2000
                   && (bool == 1 || bool == 2)
                   && val == 0
                   && cargo >= 0
                   && maxCargo >= 100 && maxCargo < 100_000;
        });
        if (address != 0) heroInfoAddress.send(address);
    }

    /**
     * Attempts to install, returns if the bot is still invalid
     * @return True if invalid and should retry, false otherwise.
     */
    private long lastPatternFailLog = 0;

    private boolean tryInstall() {
        if (!API.isValid()) return true;
        long temp;

        if ((temp = API.searchPattern(bytesToMainApplication)) == 0) {
            if (System.currentTimeMillis() - lastPatternFailLog > 5000) {
                lastPatternFailLog = System.currentTimeMillis();
                System.out.println("BotInstaller: bytesToMainApplication pattern not found in process memory yet " +
                        "(this is the core address resolution everything else depends on)");
            }
            return true;
        }
        System.out.println("BotInstaller: bytesToMainApplication pattern found at " + temp);
        this.mainApplicationAddress.send(temp - 228);
        BotInstaller.SEP = API.readInt(mainApplicationAddress.get() + 4);

        if ((temp = API.searchClassClosure(this::settingsPattern)) == 0) {
            System.out.println("BotInstaller: settingsPattern not found, aborting install");
            return true;
        }
        this.settingsAddress.send(temp);

        if ((temp = API.readLong(mainApplicationAddress.get() + 1344)) == 0) {
            System.out.println("BotInstaller: mainAddress read (mainApplicationAddress+1344) was 0, aborting install");
            return true;
        }
        this.mainAddress.send(temp);

        if ((temp = API.readLong(mainAddress.get() + 504)) == 0) {
            System.out.println("BotInstaller: screenManagerAddress read (mainAddress+504) was 0, aborting install");
            return true;
        }
        this.screenManagerAddress.send(temp);

        if ((temp = API.readLong(mainAddress.get() + 512)) == 0) {
            System.out.println("BotInstaller: guiManagerAddress read (mainAddress+512) was 0, aborting install");
            return true;
        }
        this.guiManagerAddress.send(temp);

        SCRIPT_OBJECT_VTABLE = API.readLong(screenManagerAddress.get());
        // vtable -> traits -> core -> string cache -> cpp_vtable
        STRING_OBJECT_VTABLE = API.readLong(screenManagerAddress.get(), 0x10, 0x28, 0x8, 0x3e8, 0x0);
        System.out.println("BotInstaller: install succeeded. mainAddress=" + mainAddress.get()
                + " screenManagerAddress=" + screenManagerAddress.get()
                + " guiManagerAddress=" + guiManagerAddress.get());

        //reset address
        this.heroInfoAddress.send(0L);
        this.connectionManagerAddress.send(0L);
        return false;
    }

    private boolean settingsPattern(long address) {
        return API.readInt(address + 48) == -1
               && API.readInt(address + 52) == 0
               && API.readInt(address + 56) == 2
               && API.readInt(address + 60) == 1;
        // address + 280 - starts old pattern here
    }

    private long lastInternetRead;
    private int invalidTimeoutHits;
    private void checkInvalid() {
        // Background only api ignores invalid checks
        if (API.hasCapability(Capability.BACKGROUND_ONLY)) return;

        if (API.hasCapability(Capability.HANDLER_INTERNET_READ_TIME)) {
            long lastRead = API.lastInternetReadTime();
            if (lastInternetRead != lastRead) {
                lastInternetRead = lastRead;
                invalidTimeoutHits = 0;
                invalidTimer.activate(INVALID_TIMEOUT_WITH_READ);
            } else if (!invalidTimer.isArmed()) invalidTimer.activate(INVALID_TIMEOUT_WITH_READ);

        } else if (!invalidTimer.isArmed()) invalidTimer.activate(INVALID_TIMEOUT_NO_READ);

        // timer is disarmed, so is potentially stuck on loading
        if (invalidTimer.tryDisarm()) {
            invalidTimeoutHits++;
            if (invalidTimeoutHits < MAX_INVALID_TIMEOUT_HITS) {
                invalidTimer.activate(API.hasCapability(Capability.HANDLER_INTERNET_READ_TIME)
                        ? INVALID_TIMEOUT_WITH_READ : INVALID_TIMEOUT_NO_READ);
                System.out.println("Loading watchdog timeout " + invalidTimeoutHits + "/"
                        + MAX_INVALID_TIMEOUT_HITS + ", waiting before refresh...");
                return;
            }

            invalidTimeoutHits = 0;
            if (API.hasCapability(Capability.HANDLER_CLEAR_CACHE)
                    && Main.INSTANCE != null
                    && Main.INSTANCE.config != null
                    && Main.INSTANCE.config.BOT_SETTINGS.API_CONFIG.CLEAR_CACHE_ON_STUCK)
                API.clearCache(".*");

            API.handleRefresh(true);
            System.out.println("Triggering refresh: stuck at loading screen for too long!");
        }
    }
}
