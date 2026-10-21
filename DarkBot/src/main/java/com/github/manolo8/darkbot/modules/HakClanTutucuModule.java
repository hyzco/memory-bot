package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.entities.Pet;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.itf.Module;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.extensions.features.Feature;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import eu.darkbot.api.game.other.EntityInfo.Diplomacy;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.manolo8.darkbot.core.objects.facades.SettingsProxy.KeyBind.ATTACK_LASER;

@Feature(name = "HAK Clan Tutucu", description = "Locks nearest enemy ship and can auto-attack after lock.")
public class HakClanTutucuModule implements Module {

    private static final Logger LOG = Logger.getLogger(HakClanTutucuModule.class.getName());
    private static final double DEFAULT_MAX_LOCK_DISTANCE = 1100;
    private static final long TARGET_CACHE_UPDATE_MS = 50;
    private static final int SCAN_BATCH_SIZE = 48;

    private Main main;
    private HeroManager hero;
    private Config config;

    private Ship target;
    private Ship cachedNearestEnemy;
    private String pinnedEnemyName;
    private long lastTargetCacheUpdate;
    private transient int scanCursor;
    private transient Ship cycleBestEnemy;
    private transient double cycleBestDistanceSq;
    private transient int selectorKeyCode = -1;
    private volatile boolean toggleRequested;
    private volatile long lastHotkeyToggleMs;
    private transient NativeKeyListener nativeKeyListener;
    private volatile boolean nativeHookRegistered;
    private volatile boolean nativeHookRegistering;

    @Override
    public void install(Main main) {
        this.main = main;
        this.hero = main.hero;
        this.config = main.config;
        refreshSelectorKeyCode();
        installGlobalHotkey();
    }

    @Override
    public void uninstall() {
        uninstallGlobalHotkey();
    }

    public void requestToggleLock() {
        toggleRequested = true;
    }

    public void setPinnedEnemyName(String playerName) {
        this.pinnedEnemyName = playerName == null ? null : playerName.trim();
        if (this.pinnedEnemyName != null && this.pinnedEnemyName.isEmpty()) {
            this.pinnedEnemyName = null;
        }
    }

    public String getPinnedEnemyName() {
        return pinnedEnemyName;
    }

    public List<String> getNearestEnemyNames() {
        List<String> names = new java.util.ArrayList<>();
        List<Ship> ships = main != null && main.mapManager != null ? main.mapManager.entities.ships : java.util.Collections.emptyList();

        java.util.List<Ship> valid = ships.stream()
                .filter(s -> s != null && s.isValid() && s.isSelectable() && isHostileShip(s))
                .sorted((a, b) -> Double.compare(distanceSqToHero(a), distanceSqToHero(b)))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());

        for (Ship ship : valid) {
            if (ship.playerInfo != null && ship.playerInfo.username != null && !ship.playerInfo.username.isBlank()) {
                names.add(ship.playerInfo.username);
            }
        }

        return names;
    }

    @Override
    public String status() {
        if (target == null || !target.isValid()) return "Idle";

        return "Ready " + target.playerInfo.username + (config.GENERAL.HAK_CLAN_TUTUCU.AUTO_ATTACK ? " + attack" : "");
    }

    @Override
    public boolean canRefresh() {
        return target == null || !target.isValid();
    }

    @Override
    public void tick() {
        if (!main.isRunning() || !isTutucuActive()) return;

        refreshSelectorKeyCode();
        updateTargetCacheIfNeeded();
        if (!toggleRequested) return;
        toggleRequested = false;

        if (isLocked()) {
            unlockTarget();
            return;
        }

        target = getBestLockTarget();
        if (target == null) {
            target = findNearestEnemyShip();
        }

        if (target == null || !target.isValid() || distanceSqToHero(target) > getMaxLockDistanceSq()) {
            target = getBestLockTarget();
            if (target == null || !target.isValid()) return;
        }

        hero.setLocalTarget(target);

        if (config.GENERAL.HAK_CLAN_TUTUCU.ENABLE_LOCKING && isLockCandidate(target)) {
            target.trySelect(config.GENERAL.HAK_CLAN_TUTUCU.AUTO_ATTACK);
        }

        if (config.GENERAL.HAK_CLAN_TUTUCU.AUTO_ATTACK) {
            main.facadeManager.settings.pressKeybind(ATTACK_LASER);
        }
    }

    private boolean isLocked() {
        return target != null && target.isValid() && hero.getLocalTarget() == target;
    }

    private void unlockTarget() {
        hero.setLocalTarget(null);
        target = null;
    }

    private void updateTargetCacheIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastTargetCacheUpdate < TARGET_CACHE_UPDATE_MS) return;

        scanEnemyBatch();
        lastTargetCacheUpdate = now;
    }

    private Ship getBestLockTarget() {
        Ship pinned = findPinnedEnemyShip();
        if (isLockCandidate(pinned)) return pinned;
        if (isLockCandidate(target)) return target;
        if (config.GENERAL.HAK_CLAN_TUTUCU.LOCK_ON_NEAREST) {
            Ship nearest = findNearestEnemyShip();
            if (isLockCandidate(nearest)) return nearest;
        }
        if (isLockCandidate(cachedNearestEnemy)) return cachedNearestEnemy;
        return null;
    }

    private Ship findPinnedEnemyShip() {
        if (pinnedEnemyName == null || pinnedEnemyName.isBlank()) return null;
        List<Ship> ships = main.mapManager.entities.ships;
        for (Ship ship : ships) {
            if (!isLockCandidate(ship) || ship.playerInfo == null || ship.playerInfo.username == null) continue;
            if (ship.playerInfo.username.equalsIgnoreCase(pinnedEnemyName)) {
                return ship;
            }
        }
        return null;
    }

    private Ship findNearestEnemyShip() {
        List<Ship> ships = main.mapManager.entities.ships;
        Ship best = null;
        double bestDistanceSq = getMaxLockDistanceSq();
        double heroX = hero.locationInfo.now.x;
        double heroY = hero.locationInfo.now.y;

        for (Ship ship : ships) {
            if (!isLockCandidate(ship)) continue;

            double dx = heroX - ship.locationInfo.now.x;
            double dy = heroY - ship.locationInfo.now.y;
            double distanceSq = (dx * dx) + (dy * dy);
            if (distanceSq < bestDistanceSq) {
                bestDistanceSq = distanceSq;
                best = ship;
            }
        }

        cachedNearestEnemy = best;
        return best;
    }

    private void scanEnemyBatch() {
        List<Ship> ships = main.mapManager.entities.ships;
        int size = ships.size();
        if (size == 0) {
            cachedNearestEnemy = null;
            cycleBestEnemy = null;
            cycleBestDistanceSq = getMaxLockDistanceSq();
            scanCursor = 0;
            return;
        }

        if (scanCursor >= size) scanCursor = 0;

        double heroX = hero.locationInfo.now.x;
        double heroY = hero.locationInfo.now.y;
        cycleBestDistanceSq = getMaxLockDistanceSq();
        cycleBestEnemy = null;
        int batch = Math.min(SCAN_BATCH_SIZE, size);
        boolean wrapped = false;

        for (int i = 0; i < batch; i++) {
            Ship ship = ships.get(scanCursor++);
            if (scanCursor >= size) {
                scanCursor = 0;
                wrapped = true;
            }

            if (!ship.isValid() || ship instanceof Pet || ship == hero) continue;
            if (!ship.isSelectable() || !isHostileShip(ship)) continue;

            double dx = heroX - ship.locationInfo.now.x;
            double dy = heroY - ship.locationInfo.now.y;
            double distanceSq = (dx * dx) + (dy * dy);
            if (distanceSq < cycleBestDistanceSq) {
                cycleBestDistanceSq = distanceSq;
                cycleBestEnemy = ship;
            }

            if (wrapped) break;
        }

        if (wrapped) {
            cachedNearestEnemy = isLockCandidate(cycleBestEnemy) ? cycleBestEnemy : null;
            cycleBestEnemy = null;
            cycleBestDistanceSq = getMaxLockDistanceSq();
        }
    }

    private boolean isLockCandidate(Ship ship) {
        if (ship == null || !ship.isValid() || ship.address == 0) return false;
        if (!ship.isSelectable() || !isHostileShip(ship)) return false;
        return distanceSqToHero(ship) <= getMaxLockDistanceSq();
    }

    private boolean isHostileShip(Ship ship) {
        if (ship == null || ship.playerInfo == null) return false;
        if (ship == hero) return false;

        Diplomacy diplomacy = ship.playerInfo.getClanDiplomacy();
        if (diplomacy == Diplomacy.ALLIED || diplomacy == Diplomacy.NOT_ATTACK_PACT) return false;
        if (ship.playerInfo.getFaction() == hero.playerInfo.getFaction()) {
            return diplomacy == Diplomacy.WAR;
        }

        return ship.playerInfo.isEnemy();
    }

    private double getMaxLockDistanceSq() {
        double maxDistance = DEFAULT_MAX_LOCK_DISTANCE;
        if (config != null && config.GENERAL != null && config.GENERAL.HAK_CLAN_TUTUCU != null) {
            maxDistance = config.GENERAL.HAK_CLAN_TUTUCU.MAX_LOCK_DISTANCE;
        }
        return maxDistance * maxDistance;
    }

    private double distanceSqToHero(Ship ship) {
        double dx = hero.locationInfo.now.x - ship.locationInfo.now.x;
        double dy = hero.locationInfo.now.y - ship.locationInfo.now.y;
        return (dx * dx) + (dy * dy);
    }

    private void installGlobalHotkey() {
        if (nativeHookRegistered || nativeHookRegistering) return;
        nativeHookRegistering = true;

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        nativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
            }

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                if (!isTutucuActive()) return;
                // Toggle on release only to avoid native auto-repeat spam and stuck pressed states.
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                if (nativeEvent.getKeyCode() != selectorKeyCode) return;
                if (!isTutucuActive()) return;

                long now = System.currentTimeMillis();
                if (now - lastHotkeyToggleMs < 350) return;
                lastHotkeyToggleMs = now;
                toggleRequested = true;
            }
        };

        // registerNativeHook() blocks ~1s on Windows first call; run off the tick thread to avoid freezing the bot.
        Thread hookThread = new Thread(() -> {
            try {
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(nativeKeyListener);
                nativeHookRegistered = true;
            } catch (NativeHookException e) {
                LOG.log(Level.WARNING, "Tutucu global hotkey unavailable", e);
            } finally {
                nativeHookRegistering = false;
            }
        }, "HakClanTutucu-HookInstall");
        hookThread.setDaemon(true);
        hookThread.start();
    }

    private void uninstallGlobalHotkey() {
        if (!nativeHookRegistered) return;

        try {
            if (nativeKeyListener != null) GlobalScreen.removeNativeKeyListener(nativeKeyListener);
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            LOG.log(Level.WARNING, "Tutucu global hotkey unload failed", e);
        } finally {
            nativeHookRegistered = false;
            nativeKeyListener = null;
            selectorKeyCode = -1;
        }
    }

    private boolean isTutucuActive() {
        return main.isRunning() && main.config.GENERAL.CURRENT_MODULE.equals(HakClanTutucuModule.class.getCanonicalName());
    }

    private void refreshSelectorKeyCode() {
        String selectorKey = config.GENERAL.HAK_CLAN_TUTUCU.SELECTOR_KEY;
        if (selectorKey == null || selectorKey.isBlank()) {
            selectorKeyCode = -1;
            return;
        }

        int newKeyCode = toNativeKeyCode(selectorKey);
        if (newKeyCode != selectorKeyCode) {
            selectorKeyCode = newKeyCode;
        }
    }

    private int toNativeKeyCode(String selectorKey) {
        if (selectorKey == null || selectorKey.isBlank()) return -1;

        String normalized = selectorKey.trim();
        if (normalized.length() == 1) {
            char key = Character.toUpperCase(normalized.charAt(0));
            if (key >= 'A' && key <= 'Z') return NativeKeyEvent.VC_A + (key - 'A');
            if (key >= '0' && key <= '9') return NativeKeyEvent.VC_0 + (key - '0');

            switch (key) {
                case ' ': return NativeKeyEvent.VC_SPACE;
                case '-': return NativeKeyEvent.VC_MINUS;
                case '=': return NativeKeyEvent.VC_EQUALS;
                case '[': return NativeKeyEvent.VC_OPEN_BRACKET;
                case ']': return NativeKeyEvent.VC_CLOSE_BRACKET;
                case '\\': return NativeKeyEvent.VC_BACK_SLASH;
                case ';': return NativeKeyEvent.VC_SEMICOLON;
                case '\'': return NativeKeyEvent.VC_QUOTE;
                case ',': return NativeKeyEvent.VC_COMMA;
                case '.': return NativeKeyEvent.VC_PERIOD;
                case '/': return NativeKeyEvent.VC_SLASH;
                case '`': return NativeKeyEvent.VC_BACKQUOTE;
                default: return -1;
            }
        }

        String upper = normalized.toUpperCase();
        switch (upper) {
            case "SPACE": return NativeKeyEvent.VC_SPACE;
            case "TAB": return NativeKeyEvent.VC_TAB;
            case "ENTER": return NativeKeyEvent.VC_ENTER;
            case "ESC": case "ESCAPE": return NativeKeyEvent.VC_ESCAPE;
            case "BACKSPACE": return NativeKeyEvent.VC_BACKSPACE;
            case "DELETE": return NativeKeyEvent.VC_DELETE;
            case "UP": return NativeKeyEvent.VC_UP;
            case "DOWN": return NativeKeyEvent.VC_DOWN;
            case "LEFT": return NativeKeyEvent.VC_LEFT;
            case "RIGHT": return NativeKeyEvent.VC_RIGHT;
            case "SHIFT": return NativeKeyEvent.VC_SHIFT;
            case "CTRL": case "CONTROL": return NativeKeyEvent.VC_CONTROL;
            case "ALT": return NativeKeyEvent.VC_ALT;
            case "PAUSE": return NativeKeyEvent.VC_PAUSE;
            case "CAPSLOCK": return NativeKeyEvent.VC_CAPS_LOCK;
            case "PRINTSCREEN": return NativeKeyEvent.VC_PRINTSCREEN;
            case "INSERT": return NativeKeyEvent.VC_INSERT;
            case "HOME": return NativeKeyEvent.VC_HOME;
            case "END": return NativeKeyEvent.VC_END;
            case "PAGEUP": case "PGUP": return NativeKeyEvent.VC_PAGE_UP;
            case "PAGEDOWN": case "PGDN": return NativeKeyEvent.VC_PAGE_DOWN;
            case "F1": return NativeKeyEvent.VC_F1;
            case "F2": return NativeKeyEvent.VC_F2;
            case "F3": return NativeKeyEvent.VC_F3;
            case "F4": return NativeKeyEvent.VC_F4;
            case "F5": return NativeKeyEvent.VC_F5;
            case "F6": return NativeKeyEvent.VC_F6;
            case "F7": return NativeKeyEvent.VC_F7;
            case "F8": return NativeKeyEvent.VC_F8;
            case "F9": return NativeKeyEvent.VC_F9;
            case "F10": return NativeKeyEvent.VC_F10;
            case "F11": return NativeKeyEvent.VC_F11;
            case "F12": return NativeKeyEvent.VC_F12;
            default:
                try {
                    int code = Integer.parseInt(normalized);
                    if (code >= 0) return code;
                } catch (NumberFormatException ignored) {
                    // fallback to the character path below
                }
                return -1;
        }
    }

}
