package eu.darkbot.api;

import com.github.manolo8.darkbot.core.api.GameAPI;
import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.utils.LibUtils;
import com.github.manolo8.darkbot.utils.OSUtil;
import com.github.manolo8.darkbot.utils.Time;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RunnerKekkaPlayer implements GameAPI.Window, GameAPI.Handler, API.Singleton {

    private static Path runnerPath;

    private final DarkMem memory = new DarkMem();
    private final RunnerKekkaInput interaction = new RunnerKekkaInput();

    private Process process;
    private long pid;

    private String url;
    private String sid;
    private String preloader;
    private String vars;
    private String flashPath;

    private int width = 1280;
    private int height = 800;

    public DarkMem getMemory() {
        return memory;
    }

    public RunnerKekkaInput getInteraction() {
        return interaction;
    }

    public String getVersionTag() {
        return "RunnerKekka-" + memory.getVersion() + "m " + interaction.getVersion() + "i";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public synchronized void setData(String url, String sid, String preloader, String vars) {
        this.url = url;
        this.sid = sid != null && sid.startsWith("dosid=") ? sid.substring("dosid=".length()) : sid;
        this.preloader = preloader;
        this.vars = vars;
    }

    @Override
    public synchronized void createWindow() {
        restartProcess();
    }

    @Override
    public synchronized boolean isValid() {
        if (process == null) return false;
        if (process.isAlive()) return pid > 0;

        pid = 0;
        interaction.attach(0);
        process = null;
        return false;
    }

    @Override
    public long getMemoryUsage() {
        return 0;
    }

    @Override
    public synchronized void reload() {
        restartProcess();
    }

    @Override
    public synchronized void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void setVisible(boolean visible) {
    }

    @Override
    public void setMinimized(boolean minimized) {
    }

    @Override
    public synchronized void setFlashOcxPath(String path) {
        if (path != null && !path.isBlank()) {
            this.flashPath = path;
        }
    }

    @Override
    public synchronized void setClientSize(int width, int height) {
        setSize(width, height);
    }

    @Override
    public synchronized void setMinClientSize(int width, int height) {
        if (this.width < width) this.width = width;
        if (this.height < height) this.height = height;
    }

    private synchronized void restartProcess() {
        stopProcess();

        if (!OSUtil.isWindows()) {
            System.err.println("RunnerKekkaPlayer is only supported on Windows");
            return;
        }

        if (url == null || sid == null || preloader == null || vars == null) {
            System.err.println("RunnerKekkaPlayer is missing launch data");
            return;
        }

        try {
            Path runner = getRunnerPath();
            if (!Files.exists(runner)) {
                System.err.println("KekkaRunner.exe was not found at " + runner.toAbsolutePath());
                return;
            }

            if (flashPath == null || flashPath.isBlank()) {
                flashPath = LibUtils.getFlashOcxPath().toString();
            }

            System.out.println("Runner launch url=" + url);
            System.out.println("Runner launch preloader=" + preloader);
            System.out.println("Runner launch vars=" + summarizeVars(vars));

            process = new ProcessBuilder(
                    runner.toAbsolutePath().toString(),
                    "--sid", sid,
                    "--url", url,
                    "--movie", preloader,
                    "--width", String.valueOf(width),
                    "--height", String.valueOf(height),
                    "--name", "Hak Clan SE",
                    "--flash", flashPath,
                    "--vars", vars)
                    .start();

            pid = process.pid();
            memory.openProcess(pid);
            interaction.attach(pid);
            System.out.println("RunnerKekkaPlayer attached to pid " + pid);
        } catch (IOException e) {
            pid = 0;
            interaction.attach(0);
            process = null;
            System.err.println("Failed to launch runner-backed game window");
            e.printStackTrace();
        }
    }

    private synchronized void stopProcess() {
        interaction.attach(0);
        pid = 0;

        if (process == null) return;

        try {
            if (process.isAlive()) {
                process.destroy();
                if (!process.waitFor(2, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    process.waitFor(2, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            process = null;
        }
    }

    private static synchronized Path getRunnerPath() {
        if (runnerPath == null) {
            runnerPath = LibUtils.getSharedLibrary("KekkaRunner.exe");
        }
        return runnerPath;
    }

    private static String summarizeVars(String vars) {
        if (vars == null || vars.isBlank()) return "<empty>";

        String[] keys = Arrays.stream(vars.split("&"))
                .map(part -> {
                    int sep = part.indexOf('=');
                    return sep == -1 ? part : part.substring(0, sep);
                })
                .filter(key -> !key.isBlank())
                .sorted()
                .toArray(String[]::new);

        return "count=" + keys.length + ", keys=" + String.join(",", keys) + ", cdn=" + findVar(vars, "cdn")
                + ", lang=" + findVar(vars, "lang") + ", userID=" + findVar(vars, "userID");
    }

    private static String findVar(String vars, String key) {
        String prefix = key + "=";
        return Arrays.stream(vars.split("&"))
                .filter(part -> part.startsWith(prefix))
                .map(part -> part.substring(prefix.length()))
                .findFirst()
                .orElse("<missing>");
    }

    public static class RunnerKekkaInput implements GameAPI.Interaction {
        private final DarkInput input = new DarkInput();
        private long pid;
        private boolean windowOpen;

        public void attach(long pid) {
            this.pid = pid;
            this.windowOpen = false;
            MapManager.clientWidth = 0;
            MapManager.clientHeight = 0;
        }

        private boolean inputReady() {
            if (windowOpen) return true;
            if (pid == 0 || MapManager.clientWidth == 0 || MapManager.clientHeight == 0) return false;

            return windowOpen = input.openWindow(pid, MapManager.clientWidth, MapManager.clientHeight);
        }

        @Override
        public int getVersion() {
            return input.getVersion();
        }

        @Override
        public void keyClick(int keyCode) {
            if (inputReady()) input.keyClick(keyCode);
        }

        @Override
        public void sendText(String text) {
            if (inputReady()) input.sendText(text);
        }

        @Override
        public void mouseMove(int x, int y) {
            if (inputReady()) input.mouseMove(x, y);
        }

        @Override
        public void mouseDown(int x, int y) {
            if (inputReady()) input.mouseDown(x, y);
        }

        @Override
        public void mouseUp(int x, int y) {
            if (inputReady()) input.mouseUp(x, y);
        }

        @Override
        public void mouseClick(int x, int y) {
            if (inputReady()) {
                input.mouseClick(x, y);
                Time.sleep(75);
            }
        }
    }
}