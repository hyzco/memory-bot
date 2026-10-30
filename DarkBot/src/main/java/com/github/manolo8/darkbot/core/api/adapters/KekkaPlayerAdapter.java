package com.github.manolo8.darkbot.core.api.adapters;

import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.BotInstaller;
import com.github.manolo8.darkbot.core.api.Capability;
import com.github.manolo8.darkbot.core.api.GameAPIImpl;
import com.github.manolo8.darkbot.core.api.Utils;
import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.objects.slotbars.Item;
import com.github.manolo8.darkbot.core.utils.ByteUtils;
import com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder;
import com.github.manolo8.darkbot.utils.StartupParams;
import eu.darkbot.api.KekkaPlayer;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.game.other.Locatable;
import eu.darkbot.api.managers.ConfigAPI;
import eu.darkbot.api.managers.OreAPI;
import eu.darkbot.api.utils.ItemUseCaller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class KekkaPlayerAdapter extends GameAPIImpl<
        KekkaPlayer,
        KekkaPlayer,
        KekkaPlayer,
        ByteUtils.ExtraMemoryReader,
        KekkaWindowInput,
        KekkaPlayerAdapter.KekkaPlayerDirectInteraction> {

    private final ItemUseCaller itemUseCaller;
    private final Consumer<Map<String, Config.BotSettings.APIConfig.PatternInfo>> listener = this::setBlockingPatterns;

    // The base constructor calls setFlashOcxPath/setMinClientSize, which reach native KekkaPlayer before the window exists
    // and crash the API thread. Those calls are queued here and replayed once the window is valid.
    private volatile boolean nativeReady;
    private String pendingFlashOcxPath;
    private int pendingMinWidth;
    private int pendingMinHeight;
    private boolean hasPendingMinClientSize;
    private Map<String, Config.BotSettings.APIConfig.PatternInfo> pendingBlockingPatterns;

    public KekkaPlayerAdapter(StartupParams params, KekkaPlayerDirectInteraction di, KekkaPlayer kekkaPlayer,
                              BotInstaller botInstaller, ItemUseCaller itemUseCaller, ConfigAPI config) {
        super(params,
                kekkaPlayer,
                kekkaPlayer,
                kekkaPlayer,
                new ByteUtils.ExtraMemoryReader(kekkaPlayer, botInstaller),
                new KekkaWindowInput(kekkaPlayer),
                di,
                Capability.LOGIN,
                Capability.INITIALLY_SHOWN,
                Capability.CREATE_WINDOW_THREAD,
                Capability.DIRECT_LIMIT_FPS,
                Capability.HANDLER_GAME_QUALITY,
                Capability.PROXY,
                Capability.WINDOW_POSITION,
                Capability.HANDLER_CLEAR_CACHE,
                Capability.HANDLER_CLEAR_RAM,
                Capability.HANDLER_GAME_QUALITY,
                Capability.HANDLER_VOLUME,
                Capability.HANDLER_TRANSPARENCY,
                Capability.HANDLER_CLIENT_SIZE,
                Capability.HANDLER_MIN_CLIENT_SIZE,
                Capability.HANDLER_CPU_USAGE,
                Capability.HANDLER_RAM_USAGE,
                Capability.ALL_KEYBINDS_SUPPORT,
                Capability.HANDLER_FLASH_PATH,
                Capability.HANDLER_INTERNET_READ_TIME,
                Capability.DIRECT_ENTITY_SELECT,
                Capability.DIRECT_MOVE_SHIP,
                Capability.DIRECT_COLLECT_BOX,
                Capability.DIRECT_POST_ACTIONS,
                Capability.DIRECT_CALL_METHOD,
                Capability.DIRECT_REFINE,
                Capability.DIRECT_USE_ITEM);
        this.itemUseCaller = itemUseCaller;

            // 10 seconds after each reload
        botInstaller.invalid.add(v -> clearRamTimer.activate(10_000));

        ConfigSetting<Map<String, Config.BotSettings.APIConfig.PatternInfo>> c = config.requireConfig("bot_settings.api_config.block_patterns");
        listener.accept(c.getValue());
        c.addListener(listener);
    }

    @Override
    public void createWindow() {
        super.createWindow();
        if (com.github.manolo8.darkbot.Main.INSTANCE.config.BOT_SETTINGS.OTHER.DOCK_GAME_WINDOW) NativeWindowEmbedder.dockByTitleAsync("KekkaPlayer");
        else NativeWindowEmbedder.embedByTitleAsync("KekkaPlayer");
    }

    @Override
    public void reload(boolean useFakeDailyLogin) {
        // Clear cache before reload so a stale cache doesn't stall map load.
        // This must NOT be called before createWindow() — the native handle doesn't exist yet
        // and calling clearCache() on an uninitialized KekkaPlayer corrupts its state and
        // causes createWindow() to crash (EXCEPTION_ACCESS_VIOLATION) on the API thread.
        try {
            window.clearCache(".*");
        } catch (Throwable t) {
            System.out.println("Cache clear before reload failed: " + t);
        }
        if (!useFakeDailyLogin && window.getVersion() >= 26)
            window.normalReload();
        else handler.reload();
    }

    @Override
    public boolean isValid() {
        boolean valid = super.isValid();
        if (valid && !nativeReady) applyPendingNativeSettings();
        return valid;
    }

    @Override
    public synchronized void setFlashOcxPath(String path) {
        if (nativeReady) super.setFlashOcxPath(path);
        else pendingFlashOcxPath = path;
    }

    @Override
    public synchronized void setMinClientSize(int width, int height) {
        if (nativeReady) {
            super.setMinClientSize(width, height);
            return;
        }
        pendingMinWidth = width;
        pendingMinHeight = height;
        hasPendingMinClientSize = true;
    }

    private synchronized void applyPendingNativeSettings() {
        nativeReady = true;
        if (pendingFlashOcxPath != null) super.setFlashOcxPath(pendingFlashOcxPath);
        if (hasPendingMinClientSize) super.setMinClientSize(pendingMinWidth, pendingMinHeight);
        if (pendingBlockingPatterns != null) setBlockingPatterns(pendingBlockingPatterns);
    }

    @Override
    public boolean useItem(Item item) {
        // The direct "sendRequest" call into KekkaPlayer access-violates here (hard JVM crash, no Java exception).
        // Returning false makes the slot bar press the item's keybind instead, which goes through safe window input.
        return false;
    }

    @Override
    public boolean isUseItemSupported() {
        return false;
    }

    @Override
    public void postActions(long... actions) {
        window.postActions(actions);
    }

    @Override
    public void pasteText(String text, long... actions) {
        window.pasteText(text, actions);
    }

    @Override
    public long getMemoryUsage() {
        return window.getMemoryUsage() / 1024 / 1024;
    }

    @Override
    public String getVersion() {
        return "KekkaPlayer-" + window.getVersion();
    }

    public synchronized void setBlockingPatterns(Map<String, Config.BotSettings.APIConfig.PatternInfo> map) {
        if (!nativeReady) {
            pendingBlockingPatterns = map;
            return;
        }

        List<String> result = new ArrayList<>(map.size());
        map.forEach((key, value) -> {
            if (value.enable && value.regex != null && !value.regex.isEmpty()) {
                result.add(value.regex);
                result.add(value.filePath == null ? "" : value.filePath);
            }
        });

        window.setBlockingPatterns(result.toArray(new String[0]));
    }

    public static class KekkaPlayerDirectInteraction extends NoopAPIAdapter.NoOpDirectInteraction
            implements Utils.SignatureChecker {
        private final KekkaPlayer kekkaPlayer;
        private final BotInstaller botInstaller;

        private final Set<String> methodSignatureCache = new HashSet<>();

        public KekkaPlayerDirectInteraction(KekkaPlayer kekkaPlayer, BotInstaller botInstaller) {
            this.kekkaPlayer = kekkaPlayer;
            this.botInstaller = botInstaller;

            botInstaller.invalid.add(v -> methodSignatureCache.clear());
        }

        @Override
        public Set<String> signatureCache() {
            return methodSignatureCache;
        }

        @Override
        public void setMaxFps(int maxFps) {
            kekkaPlayer.setMaxFps(maxFps);
        }

        @Override
        public boolean callMethodChecked(boolean checkName, String signature, int index, long... arguments) {
            if (checkSignature(checkName, signature, index, arguments[0]))
                return callMethodAsync(index, arguments);

            return false;
        }

        @Override
        public boolean callMethodAsync(int index, long... arguments) {
            return kekkaPlayer.callMethod(botInstaller.screenManagerAddress.get(), index, arguments);
        }

        @Override
        public void selectEntity(Entity entity) {
            // Garbage client size (wrong view layout / mid-reload) turns into garbage native args and a hard JVM crash.
            int cw = com.github.manolo8.darkbot.core.manager.MapManager.clientWidth;
            int ch = com.github.manolo8.darkbot.core.manager.MapManager.clientHeight;
            if (cw < 100 || cw > 16384 || ch < 100 || ch > 16384) return;
            if (entity.clickable.isInvalid()) return;
            if (botInstaller.screenManagerAddress.get() == 0) return;

            // KekkaPlayer.sendNotification access-violates (uncatchable JVM crash) on the select notification, so
            // select the way a player does: a real click on the ship in the docked game window.
            if (Utils.viewRect() == null) return;
            Utils.SelectEntityClick click = Utils.createSelectEntityClick(entity);
            com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder.clickEntity(click.clickX, click.clickY);
        }

        @Override
        public void moveShip(Locatable destination) {
            boolean ok = checkGotoMethod(kekkaPlayer, botInstaller);
            if (!ok) System.out.println("Kekka moveShip skipped: goto method check failed (settled=" + BotInstaller.isSettled() + ")");
            if (ok)
                kekkaPlayer.moveShip(botInstaller.screenManagerAddress.get(), (long) destination.getX(), (long) destination.getY(), 0);
        }

        @Override
        public void collectBox(Box box) {
            if (checkGotoMethod(kekkaPlayer, botInstaller))
                kekkaPlayer.moveShip(botInstaller.screenManagerAddress.get(), (long) box.getX(), (long) box.getY(), box.address);
        }

        @Override
        public void refine(long refineUtilAddress, OreAPI.Ore oreType, int amount) {
            kekkaPlayer.refine(refineUtilAddress, oreType.getId(), amount);
        }

        @Override
        public long callMethod(int index, long... arguments) {
            return kekkaPlayer.callMethodSync(index, arguments);
        }

        @Override
        public int checkMethodSignature(long obj, int methodIdx, boolean includeMethodName, String signature) {
            // KekkaPlayer.dll's native check reads Flash objects from this thread while the Flash thread
            // mutates them, and access-violates (hard JVM crash, uncatchable). Object validity is already
            // verified in Java (isValidPtr / isScriptableObjectValid) before this is reached, so trust the
            // method index instead of risking the crash. 1 == valid.
            return 1;
        }
    }
}
