package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.entities.FakeNpc;
import com.github.manolo8.darkbot.core.entities.Npc;
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
import eu.darkbot.api.managers.AttackAPI;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manual-assist combat module: lock the closest enemy (player and/or NPC) with one key and let the bot run the fight.
 *
 * Target lookup does not scan every entity per query: entities live in a uniform spatial grid that is kept in sync
 * incrementally (a fixed batch per tick), and a lookup only walks the few cells around the hero, expanding ring by ring
 * and stopping as soon as no farther ring can contain a closer target.
 */
@Feature(name = "HAK Clan Tutucu", description = "Locks the nearest enemy (players / NPCs) with a hotkey and fights it.")
public class HakClanTutucuModule implements Module {

    private static final Logger LOG = Logger.getLogger(HakClanTutucuModule.class.getName());
    private static final double DEFAULT_MAX_LOCK_DISTANCE = 1100;
    private static final double GRID_CELL = 600;
    private static final int SYNC_BUDGET = 64;
    private static final long NEXT_TARGET_DELAY_MS = 250;

    private Main main;
    private HeroManager hero;
    private Config config;
    private AttackAPI attack;
    private com.github.manolo8.darkbot.modules.vs.VsMode vs;

    private final SpatialGrid<Ship> shipGrid = new SpatialGrid<>(GRID_CELL);
    private final SpatialGrid<Npc> npcGrid = new SpatialGrid<>(GRID_CELL);
    private final Map<String, Ship> shipsByName = new HashMap<>();

    private volatile Entity target;
    private volatile boolean engaged;
    private volatile String pinnedEnemyName;
    private long nextTargetAt;
    private long lockStartedAt;
    /** Click straight away instead of waiting on the (unreliable) native select, and retry quickly until the game confirms. */
    private static final long CLICK_RETRY_MS = 800; // > the game's double-click window: a quicker 2nd click = fly to target
    private long lastClickFallback;
    private long unlockedSince;
    private boolean holdingFire;
    /** When the game last confirmed our lock; 0 = not confirmed yet for the current target. */
    private long lockConfirmedAt;
    private int clickAttempts;
    private long lastDiag;
    private String lastMessage = "";

    private transient int selectorKeyCode = -1;
    private volatile boolean toggleRequested;
    private volatile boolean cycleRequested;
    private volatile boolean lockNextRequested;
    private volatile boolean lockPinnedRequested;
    /** The current lock was the player's own choice (hand pick / pin / next-target): VS never swaps it. */
    private boolean lockIsManual;
    private long suppressAutoLockUntil;
    private volatile boolean releaseRequested;
    private transient int cycleKeyCode = -1;
    private volatile long lastHotkeyToggleMs;
    private transient NativeKeyListener nativeKeyListener;
    private transient com.github.kwhat.jnativehook.mouse.NativeMouseListener nativeMouseListener;
    /** Mouse button bound to lock / cycle (3 = middle, 4 = back, 5 = forward), 0 when the key is a keyboard key. */
    private volatile int selectorMouse, cycleMouse, pinMouse, assistMouse, petMouse;
    private transient volatile int petKeyCode = -1;
    private volatile boolean petTargetRequested;
    private transient volatile int assistKeyCode = -1;
    private volatile boolean assistRequested;
    private transient volatile int pinKeyCode = -1;
    private volatile boolean pinRequested;
    private volatile boolean nativeHookRegistered;
    private volatile boolean nativeHookRegistering;

    @Override
    public void install(Main main) {
        this.main = main;
        this.hero = main.hero;
        this.config = main.config;
        this.attack = main.pluginAPI.requireAPI(AttackAPI.class);
        this.vs = new com.github.manolo8.darkbot.modules.vs.VsMode(main);
        this.vs.setHostileTest(this::isHostileShip);
        com.github.manolo8.darkbot.modules.utils.AttackAPIImpl.ammoOverride =
                () -> vs.ammoChoice(target instanceof Ship ? (Ship) target : null);
        refreshSelectorKeyCode();
        installGlobalHotkey();
    }

    @Override
    public void uninstall() {
        com.github.manolo8.darkbot.modules.utils.AttackAPIImpl.ammoOverride = null;
        uninstallGlobalHotkey();
    }

    // ------------------------------------------------------------------------------------------------ public API

    public boolean isVsMode() {
        return config != null && config.GENERAL.HAK_CLAN_TUTUCU.VS_MODE;
    }

    public void setVsMode(boolean on) {
        config.GENERAL.HAK_CLAN_TUTUCU.VS_MODE = on;
    }

    public com.github.manolo8.darkbot.modules.vs.VsMode getVs() {
        return vs;
    }

    /** Assisted mode's "do it" button: do what the pilot recommends right now. */
    public String doPilotSuggestion() {
        return vs == null ? "" : vs.pilot().doSuggested();
    }

    public void requestVsPrepare() {
        if (vs != null) vs.requestPrepare();
    }

    public List<String> getVsAdvice() {
        return vs == null || !isVsMode() ? List.of() : vs.advice();
    }

    public void requestToggleLock() {
        toggleRequested = true;
    }

    /** Lock button / hotkey: lock the nearest enemy, or jump to the next-nearest one if already locked. */
    public void requestLockNext() {
        lockNextRequested = true;
    }

    public void requestRelease() {
        releaseRequested = true;
    }

    public void setPinnedEnemyName(String playerName) {
        String name = playerName == null ? null : playerName.trim();
        this.pinnedEnemyName = name == null || name.isEmpty() ? null : name;
    }

    public String getPinnedEnemyName() {
        return pinnedEnemyName;
    }

    public String getLockedName() {
        Entity t = target;
        return t != null && t.isValid() && nameOf(t) != null ? nameOf(t) : null;
    }

    public String getSelectorKeyLabel() {
        String key = config != null ? config.GENERAL.HAK_CLAN_TUTUCU.SELECTOR_KEY : null;
        if (key == null || key.isBlank()) return "?";
        int mouse = parseMouseButton(key);
        return mouse == 0 ? key.toUpperCase(Locale.ROOT) : mouse == 3 ? "Middle click" : "Mouse " + mouse;
    }

    public static final class EnemyEntry {
        public final String name;
        public final int distance;
        public final boolean npc;

        EnemyEntry(String name, int distance, boolean npc) {
            this.name = name;
            this.distance = distance;
            this.npc = npc;
        }
    }

    public List<String> getNearestEnemyNames() {
        List<String> names = new ArrayList<>();
        for (EnemyEntry e : getNearestEnemies()) names.add(e.name);
        return names;
    }

    /** Closest valid targets (max 5) for the configured target type. Safe to call from the UI thread. */
    public List<EnemyEntry> getNearestEnemies() {
        List<EnemyEntry> out = new ArrayList<>();
        if (main == null || hero == null) return out;
        try {
            double x = hero.locationInfo.now.x, y = hero.locationInfo.now.y, range = maxLockDistance();
            Config.General.HakClanTutucu.TargetType type = targetType();

            List<Entity> found = new ArrayList<>();
            if (type != Config.General.HakClanTutucu.TargetType.NPCS)
                found.addAll(shipGrid.nearest(x, y, range, 5, this::isLockableShip));
            if (type != Config.General.HakClanTutucu.TargetType.PLAYERS)
                found.addAll(npcGrid.nearest(x, y, range, 5, this::isLockableNpc));

            found.sort((a, b) -> Double.compare(distSq(a, x, y), distSq(b, x, y)));
            for (Entity e : found) {
                String name = nameOf(e);
                if (name == null || name.isBlank()) continue;
                out.add(new EnemyEntry(name, (int) Math.sqrt(distSq(e, x, y)), e instanceof Npc));
                if (out.size() >= 5) break;
            }
        } catch (RuntimeException ignored) {
            // map is mid-update (reload / map change); the next refresh will have a consistent list
        }
        return out;
    }

    @Override
    public String status() {
        Entity t = target;
        if (t == null || !t.isValid()) {
            return lastMessage.isEmpty()
                    ? "Idle - press " + getSelectorKeyLabel() + " to lock (" + targetType().label() + ")"
                    : lastMessage;
        }
        boolean locked = hero.getTarget() == t;
        String fight = !locked ? " - locking..." : engaged ? (attack.isAttacking() ? " - fighting" : " - engaging") : " - locked";
        return nameOf(t) + " (" + (int) Math.sqrt(distSq(t, hero.locationInfo.now.x, hero.locationInfo.now.y)) + ")" + fight;
    }

    @Override
    public boolean canRefresh() {
        return target == null || !target.isValid();
    }

    // ------------------------------------------------------------------------------------------------ main loop

    @Override
    public void tick() {
        if (!main.isRunning() || !isTutucuActive()) return;

        refreshSelectorKeyCode();
        syncIndexes();
        profileNow();

        if (target != null && !isTargetUsable(target)) {
            boolean next = engaged && config.GENERAL.HAK_CLAN_TUTUCU.AUTO_NEXT_TARGET;
            release();
            if (next) nextTargetAt = System.currentTimeMillis() + NEXT_TARGET_DELAY_MS;
        }

        // The player clicked another enemy in the game: follow that instead of fighting their choice.
        if (target != null && System.currentTimeMillis() - lockStartedAt > 600) {
            Entity manual = manualTarget();
            if (manual != null && manual != target) {
                lockOn(manual);
                lockIsManual = true;
                if (config.GENERAL.HAK_CLAN_TUTUCU.AUTO_PIN_MANUAL && usernameOf(manual) != null)
                    setPinnedEnemyName(usernameOf(manual));
            }
        }

        if (isVsMode()) vsTick();
        retaliate();

        if (releaseRequested) {
            releaseRequested = false;
            nextTargetAt = 0;
            suppressAutoLockUntil = System.currentTimeMillis() + 8000; // you let go on purpose: don't grab someone again at once
            release();
            return;
        }

        if (petTargetRequested) {
            petTargetRequested = false;
            lastMessage = targetPinnedPet();
            return;
        }

        if (assistRequested) {
            assistRequested = false;
            if (vs != null && isVsMode()) lastMessage = "Pilot: " + vs.pilot().doSuggested();
        }

        if (pinRequested) {
            pinRequested = false;
            String result = togglePin();
            lastMessage = result == null ? "Select a player in the game first, then pin" : result;
            return;
        }

        if (lockPinnedRequested) {
            lockPinnedRequested = false;
            nextTargetAt = 0;
            Ship pinned = findPinnedShip();
            if (pinned != null && pinned != target) {
                lockOn(pinned);
                lockIsManual = true;
            } else if (pinned == null) lastMessage = "Pinned player not in range";
            return;
        }

        if (lockNextRequested) {
            lockNextRequested = false;
            nextTargetAt = 0;
            if (target != null) cycleTarget();
            else acquire();
            return;
        }

        if (cycleRequested) {
            cycleRequested = false;
            nextTargetAt = 0;
            cycleTarget();
            return;
        }

        if (toggleRequested) {
            toggleRequested = false;
            nextTargetAt = 0;
            if (target != null) {
                suppressAutoLockUntil = System.currentTimeMillis() + 8000;
                release();
            } else acquire();
            return;
        }

        if (target == null) {
            if (nextTargetAt != 0 && System.currentTimeMillis() >= nextTargetAt) {
                nextTargetAt = 0;
                acquire();
            }
            return;
        }

        fight();
    }

    /** Hostile ships near us, nearest first: players and/or NPCs according to the Players / NPCs / Both setting. */
    private List<Ship> vsEnemies() {
        double x = hero.locationInfo.now.x, y = hero.locationInfo.now.y, range = maxLockDistance();
        Config.General.HakClanTutucu.TargetType type = targetType();
        List<Ship> out = new ArrayList<>();
        if (type != Config.General.HakClanTutucu.TargetType.NPCS) out.addAll(shipGrid.nearest(x, y, range, 8, this::isLockableShip));
        if (type != Config.General.HakClanTutucu.TargetType.PLAYERS) out.addAll(npcGrid.nearest(x, y, range, 8, this::isLockableNpc));
        out.sort((a, b) -> Double.compare(distSq(a, x, y), distSq(b, x, y)));
        return out.size() > 8 ? new ArrayList<>(out.subList(0, 8)) : out;
    }

    /**
     * Profiles, straight away, everybody who attacks us plus the pinned player and our target: the moment a player
     * starts shooting they're in the list. Runs whether or not VS mode is on.
     */
    private void profileNow() {
        if (vs == null || !config.GENERAL.HAK_CLAN_TUTUCU.VS_PROFILE) return;
        long now = System.currentTimeMillis();
        Ship pinned = findPinnedShip();
        Ship current = target instanceof Ship ? (Ship) target : null;
        double hx = hero.locationInfo.now.x, hy = hero.locationInfo.now.y;

        List<Ship> candidates = new ArrayList<>();
        List<Ship> focus = new ArrayList<>();
        if (current != null && !(current instanceof Npc) && !(current instanceof Pet)) {
            candidates.add(current);
            focus.add(current);
        }
        if (pinned != null && !candidates.contains(pinned)) {
            candidates.add(pinned);
            focus.add(pinned);
        }
        for (Ship s : main.mapManager.entities.ships) {
            if (s == null || s == hero || !s.isValid() || s instanceof Pet || candidates.contains(s)) continue;
            if (!s.isAttacking(hero)) continue;
            if (Math.hypot(s.locationInfo.now.x - hx, s.locationInfo.now.y - hy) > 3000) continue;
            candidates.add(s);
            focus.add(s);
        }
        vs.profiler().tick(candidates, focus, now);
    }

    private void vsTick() {
        List<Ship> enemies = vsEnemies();
        Ship current = target instanceof Ship ? (Ship) target : null;
        vs.tick(enemies, current, engaged);

        boolean userDriven = cycleRequested || lockNextRequested || releaseRequested || lockPinnedRequested
                || pinRequested || toggleRequested;
        if (userDriven) return;
        long now = System.currentTimeMillis();

        if (current != null) {
            // The player's own pick (hand / pin / next target) and friends outside the enemy list are never swapped.
            if (lockIsManual || !enemies.contains(current)) return;
            // A target that keeps its distance isn't worth chasing: let it go and take another one.
            if (vs.pilot().shouldAbandon(current)) {
                Ship alt = null;
                for (Ship e : enemies) if (e != current && !vs.pilot().ignored(e)) {
                    alt = e;
                    break;
                }
                release();
                if (alt != null) lockOn(alt);
                else suppressAutoLockUntil = now + 5000;
                lastMessage = alt != null ? "Switched: the last target kept running" : "Target keeps running - let it go";
                return;
            }
            Ship pick = vs.pickTarget(enemies, current, now - lockStartedAt);
            if (pick != null && pick != current) lockOn(pick);
        }
    }

    /**
     * "Attack back": nothing locked and something has us targeted -> lock it and start attacking. Works with or
     * without VS mode (VS picks the worst attacker, otherwise the nearest one).
     */
    private void retaliate() {
        Config.General.HakClanTutucu c = config.GENERAL.HAK_CLAN_TUTUCU;
        // Assisted mode is "I fly": only fight back on its own if the player explicitly asked for it.
        boolean allowed = c.AUTO_RETALIATE || (isVsMode() && c.VS_PILOT != Config.General.HakClanTutucu.VsPilot.ASSISTED);
        if (target != null || !allowed || !c.ENABLE_LOCKING) return;
        if (cycleRequested || lockNextRequested || releaseRequested || lockPinnedRequested || pinRequested || toggleRequested) return;
        long now = System.currentTimeMillis();
        if (now < suppressAutoLockUntil) return; // you let go on purpose a moment ago

        List<Ship> attackers = new ArrayList<>();
        List<Ship> nearby = vsEnemies();
        for (Ship s : nearby) if (s.isAttacking(hero)) attackers.add(s); // nearest first
        if (attackers.isEmpty()) {
            // The pilot remembers who attacked a moment ago: when one comes back (or stopped aiming for a second),
            // re-engage it if the simulation says the fight is worth it.
            if (isVsMode() && c.VS_PILOT == Config.General.HakClanTutucu.VsPilot.AUTO) {
                Ship back = vs.pilot().returningAttacker(nearby, 10_000);
                if (back != null && vs.pilot().worthEngaging(back, nearby)) {
                    nextTargetAt = 0;
                    lockOn(back);
                    engaged = true;
                    lastMessage = "Re-engaging";
                    System.out.println("VS pilot: re-engaging " + nameOf(back));
                }
            }
            return;
        }

        Ship pick = isVsMode() ? vs.pickAttacker(attackers) : attackers.get(0);
        if (pick == null) return;
        nextTargetAt = 0;
        lockOn(pick);
        engaged = true; // that's the point: start shooting back
        lastMessage = "Attacking back";
        System.out.println("Tutucu: attacking back " + nameOf(pick));
    }

    private void acquire() {
        Entity picked = pickTarget();
        if (picked == null) {
            lastMessage = "No target within range";
            return;
        }
        lockOn(picked);
    }

    private void lockOn(Entity picked) {
        lastMessage = "";
        clickAttempts = 0;
        lockIsManual = false;
        lockConfirmedAt = 0;
        target = picked;
        lockStartedAt = System.currentTimeMillis();
        engaged = config.GENERAL.HAK_CLAN_TUTUCU.AUTO_ATTACK;
        attack.setTarget((eu.darkbot.api.game.other.Lockable) picked);
        fight();
    }

    private void fight() {
        Entity t = target;
        if (t == null) return;
        attack.setTarget((eu.darkbot.api.game.other.Lockable) t);
        boolean holdFire = isVsMode() && vs != null && vs.pilot().holdFire();
        if (engaged && holdFire) {
            // target is invulnerable: stay locked on it, but stop shooting so no ammo is wasted
            attack.tryLockTarget();
            if (!holdingFire) {
                holdingFire = true;
                attack.stopAttack();
                System.out.println("VS pilot: holding fire (target invulnerable)");
            }
        } else if (engaged) {
            if (holdingFire) {
                holdingFire = false;
                System.out.println("VS pilot: resuming fire");
            }
            attack.tryLockAndAttack();
        } else if (config.GENERAL.HAK_CLAN_TUTUCU.ENABLE_LOCKING) attack.tryLockTarget();

        long now = System.currentTimeMillis();
        boolean locked = hero.getTarget() == t;

        // The native select can be silently refused (map clicks gated, stale click state...). If the game still
        // doesn't have this ship targeted shortly after, click it on screen like a player would.
        if (locked) {
            unlockedSince = 0;
            lockConfirmedAt = now;
        } else if (unlockedSince == 0) unlockedSince = now;

        // The game confirmed this lock earlier and now has nothing selected, right after the player pressed something:
        // they deselected / took over. Respect that instead of clicking the target again (which made them click twice).
        long userAt = com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder.lastUserInputAt;
        if (!locked && lockConfirmedAt != 0 && userAt > lockConfirmedAt && now - userAt < 1000) {
            System.out.println("Tutucu: you took over (deselected) - released the lock");
            suppressAutoLockUntil = now + 8000;
            release();
            lastMessage = "Released: you took control";
            return;
        }
        boolean reallyUnlocked = !locked && now - unlockedSince >= (clickAttempts == 0 ? 120 : 400) && !attack.isAttacking();
        if (reallyUnlocked && (clickAttempts == 0 || now - lastClickFallback > CLICK_RETRY_MS)
                && !com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder.entityClickCooling()
                && config.GENERAL.HAK_CLAN_TUTUCU.ENABLE_LOCKING && t instanceof Ship) {
            lastClickFallback = now;
            int cw = com.github.manolo8.darkbot.core.manager.MapManager.clientWidth;
            int ch = com.github.manolo8.darkbot.core.manager.MapManager.clientHeight;
            if (cw < 100 || cw > 16384 || ch < 100 || ch > 16384) return;
            try {
                double[] view = com.github.manolo8.darkbot.core.api.Utils.viewRect();
                double bw = view == null ? 0 : view[2], bh = view == null ? 0 : view[3];
                if (view == null) {
                    System.out.println("Tutucu: click skipped, game view size unknown");
                } else {
                    com.github.manolo8.darkbot.core.api.Utils.SelectEntityClick click =
                            com.github.manolo8.darkbot.core.api.Utils.createSelectEntityClick(t);
                    // Window-message click only: KekkaPlayer's native input/select calls hard-crash this JVM.
                    boolean sent = com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder.clickEntity(click.clickX, click.clickY);
                    if (!sent) System.out.println("Tutucu: click not sent " + click.clickX + "," + click.clickY
                            + " (outside game window or not docked); view=" + (int) bw + "x" + (int) bh);
                    else {
                        clickAttempts++;
                        hero.setLocalTarget((eu.darkbot.api.game.other.Lockable) t);
                    }
                }
            } catch (RuntimeException e) {
                LOG.log(Level.FINE, "Tutucu click fallback failed", e);
            }
        }

        if (now - lastDiag > 2000) {
            lastDiag = now;
            System.out.println("Tutucu: target=" + nameOf(t) + " dist=" + (int) Math.sqrt(distSq(t, hero.locationInfo.now.x, hero.locationInfo.now.y))
                    + " selectable=" + t.isSelectable() + " gameTarget=" + (hero.getTarget() == t)
                    + " attackLocked=" + attack.isLocked() + " engaged=" + engaged
                    + " destroyed=" + main.repairManager.isDestroyed()
                    + " clientSize=" + com.github.manolo8.darkbot.core.manager.MapManager.clientWidth + "x"
                    + com.github.manolo8.darkbot.core.manager.MapManager.clientHeight);
        }
    }

    private void release() {
        try {
            attack.stopAttack();
        } catch (RuntimeException ignored) {
            // nothing to stop
        }
        attack.setTarget(null);
        hero.setLocalTarget(null);
        target = null;
        engaged = false;
    }

    private boolean isTargetUsable(Entity t) {
        if (!t.isValid() || t.address == 0) return false;
        // far out of range (flew away / teleported) -> let go instead of chasing a stale lock
        double max = maxLockDistance() * 2;
        return distSq(t, hero.locationInfo.now.x, hero.locationInfo.now.y) <= max * max;
    }

    /** The enemy the player currently has selected in the game (by hand), if it is a valid target for us. */
    private Entity manualTarget() {
        Object selected = hero.getTarget();
        if (selected instanceof Npc) return isLockableNpc((Npc) selected) ? (Entity) selected : null;
        // picked by hand: follow it even if it's a friend, the player chose it
        if (selected instanceof Ship) return isLockableShip((Ship) selected, true) ? (Entity) selected : null;
        return null;
    }

    private static String usernameOf(Entity e) {
        return e instanceof Ship && ((Ship) e).playerInfo != null ? ((Ship) e).playerInfo.username : null;
    }

    /** Pins the player you have selected (in the game or the bot's lock) so Tutucu keeps going back to them. */
    public String pinSelected() {
        Entity e = manualTarget();
        if (e == null) e = target;
        String name = usernameOf(e);
        if (name == null || name.isBlank()) return null;
        setPinnedEnemyName(name);
        lockPinnedRequested = true;
        return name;
    }

    /** Hotkey / button: unpins if someone is pinned, otherwise pins the selected player. Returns a status line or null. */
    public String togglePin() {
        String old = pinnedEnemyName;
        String result;
        if (old != null) {
            pinnedEnemyName = null;
            lockIsManual = false; // the lock stays, but it's an ordinary one again
            result = "Unpinned " + old;
        } else {
            String name = pinSelected();
            result = name == null ? null : "Pinned " + name;
        }
        System.out.println("Tutucu pin: " + (result == null ? "nobody selected" : result));
        return result;
    }

    /** Button / hotkey: target the pinned player's pet; pressing again while on the pet goes back to the player. */
    public void requestPinnedPetTarget() {
        petTargetRequested = true;
    }

    private String targetPinnedPet() {
        Ship owner = findPinnedShip();
        if (owner == null) return pinnedEnemyName == null ? "Pin a player first" : "Pinned player not in range";

        // already on their pet: the same key brings you back to the player
        if (target instanceof Pet) {
            lockOn(owner);
            lockIsManual = true;
            return "Back on " + nameOf(owner);
        }

        Pet found = null;
        double best = Double.MAX_VALUE;
        for (Pet p : main.mapManager.entities.pets) {
            if (p == null || !p.isValid() || p.address == 0 || !p.isSelectable()) continue;
            boolean theirs = p.getOwner().orElse(null) == owner || p.getOwnerId() == owner.getId();
            if (!theirs) continue;
            double d = distSq(p, hero.locationInfo.now.x, hero.locationInfo.now.y);
            if (d < best) {
                best = d;
                found = p;
            }
        }
        if (found == null) return "Their pet isn't out (or not selectable)";
        lockOn(found);
        lockIsManual = true;
        return "Targeting " + nameOf(owner) + "'s pet";
    }

    public void requestLockPinned() {
        lockPinnedRequested = true;
    }

    private static final int CANDIDATES = 6;

    /** Nearest valid targets of the configured type (grid lookups, not a scan of every entity). */
    private List<Entity> candidates(int k) {
        double x = hero.locationInfo.now.x, y = hero.locationInfo.now.y, range = maxLockDistance();
        Config.General.HakClanTutucu.TargetType type = targetType();
        List<Entity> out = new ArrayList<>();
        if (type != Config.General.HakClanTutucu.TargetType.NPCS)
            out.addAll(shipGrid.nearest(x, y, range, k, this::isLockableShip));
        if (type != Config.General.HakClanTutucu.TargetType.PLAYERS)
            out.addAll(npcGrid.nearest(x, y, range, k, this::isLockableNpc));
        return out;
    }

    private double score(Entity e, double x, double y) {
        double dist = Math.sqrt(distSq(e, x, y));
        Config.General.HakClanTutucu.Priority priority = config.GENERAL.HAK_CLAN_TUTUCU.PRIORITY;
        if (priority == null || priority == Config.General.HakClanTutucu.Priority.NEAREST || !(e instanceof Ship)) return dist;

        Ship ship = (Ship) e;
        double hp = ship.health.maxHp > 0 ? Math.max(0, Math.min(1, ship.health.hpPercent())) : 1;
        if (priority == Config.General.HakClanTutucu.Priority.LOWEST_HEALTH) return hp * 10_000 + dist;

        // SMART: enemies already shooting you come first, then weak ones, then the closest
        double attackers = !(ship instanceof Npc) && ship.isAttacking(hero) ? 0.55 : 1;
        return dist * attackers * (0.6 + 0.4 * hp);
    }

    /** Pinned player first, otherwise the best-scoring candidate of the configured type. */
    private Entity pickTarget() {
        Entity manual = manualTarget();
        if (manual != null) return manual;

        Ship pinned = findPinnedShip();
        if (pinned != null) return pinned;
        if (!config.GENERAL.HAK_CLAN_TUTUCU.LOCK_ON_NEAREST) return null;
        if (isVsMode()) return vs.pickTarget(vsEnemies(), null, 0);

        double x = hero.locationInfo.now.x, y = hero.locationInfo.now.y;
        Entity best = null;
        double bestScore = Double.MAX_VALUE;
        for (Entity e : candidates(CANDIDATES)) {
            double sc = score(e, x, y);
            if (sc < bestScore) {
                bestScore = sc;
                best = e;
            }
        }
        return best;
    }

    /** Moves the lock to the next-nearest target (hotkey), wrapping around. */
    private void cycleTarget() {
        double x = hero.locationInfo.now.x, y = hero.locationInfo.now.y;
        List<Entity> list = candidates(CANDIDATES);
        if (list.isEmpty()) {
            lastMessage = "No target within range";
            return;
        }
        list.sort((a, b) -> Double.compare(distSq(a, x, y), distSq(b, x, y)));
        int idx = list.indexOf(target);
        Entity next = list.get((idx + 1) % list.size());
        if (next == target && list.size() == 1) return;
        release();
        lockOn(next);
        lockIsManual = true; // you asked for this one
    }

    private static <T> T firstOf(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    private Ship findPinnedShip() {
        String name = pinnedEnemyName;
        if (name == null) return null;
        Ship ship = shipsByName.get(name.toLowerCase(Locale.ROOT));
        return ship != null && isLockableShip(ship, true) ? ship : null;
    }

    // ------------------------------------------------------------------------------------------------ indexes

    private void syncIndexes() {
        // name -> ship lookup for the pinned player is filled by the same rolling batch (no extra pass over all ships)
        shipGrid.sync(main.mapManager.entities.ships, SYNC_BUDGET, ship -> {
            String name = ship.playerInfo != null ? ship.playerInfo.username : null;
            if (name != null && !name.isBlank()) shipsByName.put(name.toLowerCase(Locale.ROOT), ship);
        });
        if (targetType() != Config.General.HakClanTutucu.TargetType.PLAYERS)
            npcGrid.sync(main.mapManager.entities.npcs, SYNC_BUDGET, null);
        if (shipsByName.size() > 512) shipsByName.values().removeIf(s -> !s.isValid());
    }

    // ------------------------------------------------------------------------------------------------ filters

    private boolean isLockableShip(Ship ship) {
        return isLockableShip(ship, false);
    }

    /** {@code anyRelation}: skip the enemy check (pinned or hand-picked players can be friends). */
    private boolean isLockableShip(Ship ship, boolean anyRelation) {
        return ship != null && ship.isValid() && ship.address != 0 && !(ship instanceof Pet) && ship != hero
                && ship.isSelectable() && (anyRelation || isHostileShip(ship));
    }

    private boolean isLockableNpc(Npc npc) {
        if (npc == null || !npc.isValid() || npc.address == 0 || npc instanceof FakeNpc || !npc.isSelectable()) return false;
        return !config.GENERAL.HAK_CLAN_TUTUCU.ONLY_MARKED_NPCS || (npc.npcInfo != null && npc.npcInfo.kill);
    }

    private boolean isHostileShip(Ship ship) {
        if (ship == null || ship.playerInfo == null) return false;
        if (ship == hero) return false;
        if (config.GENERAL.HAK_CLAN_TUTUCU.LOCK_FRIENDS) return true;

        Diplomacy diplomacy = ship.playerInfo.getClanDiplomacy();
        if (diplomacy == Diplomacy.ALLIED || diplomacy == Diplomacy.NOT_ATTACK_PACT) return false;
        if (ship.playerInfo.getFaction() == hero.playerInfo.getFaction()) {
            return diplomacy == Diplomacy.WAR;
        }

        return ship.playerInfo.isEnemy();
    }

    private Config.General.HakClanTutucu.TargetType targetType() {
        Config.General.HakClanTutucu.TargetType type = config.GENERAL.HAK_CLAN_TUTUCU.TARGET_TYPE;
        return type == null ? Config.General.HakClanTutucu.TargetType.PLAYERS : type;
    }

    private double maxLockDistance() {
        double maxDistance = DEFAULT_MAX_LOCK_DISTANCE;
        if (config != null && config.GENERAL != null && config.GENERAL.HAK_CLAN_TUTUCU != null) {
            maxDistance = config.GENERAL.HAK_CLAN_TUTUCU.MAX_LOCK_DISTANCE;
        }
        return maxDistance;
    }

    private static String nameOf(Entity e) {
        return e instanceof Ship && ((Ship) e).playerInfo != null ? ((Ship) e).playerInfo.username : null;
    }

    private static double distSq(Entity e, double x, double y) {
        double dx = x - e.locationInfo.now.x, dy = y - e.locationInfo.now.y;
        return dx * dx + dy * dy;
    }

    // ------------------------------------------------------------------------------------------------ spatial index

    /**
     * Uniform grid over entity positions. {@link #sync} moves at most {@code budget} entities per call (O(1) per
     * tick); {@link #nearest} walks rings of cells around a point and stops once no farther ring can improve the result.
     */
    static final class SpatialGrid<T extends Entity> {
        private final double cell;
        private final Long2ObjectOpenHashMap<List<T>> cells = new Long2ObjectOpenHashMap<>();
        private final IdentityHashMap<T, Long> keyOf = new IdentityHashMap<>();
        private int cursor;

        SpatialGrid(double cell) {
            this.cell = cell;
        }

        private static long key(long cx, long cy) {
            return (cx << 32) ^ (cy & 0xffffffffL);
        }

        private long keyFor(T e) {
            return key((long) Math.floor(e.locationInfo.now.x / cell), (long) Math.floor(e.locationInfo.now.y / cell));
        }

        synchronized void sync(List<? extends T> source, int budget, java.util.function.Consumer<T> visitor) {
            int n = source.size();
            if (n == 0) {
                cells.clear();
                keyOf.clear();
                cursor = 0;
                return;
            }
            if (keyOf.size() > n + 64) { // many despawned entities piled up: rebuild
                cells.clear();
                keyOf.clear();
                budget = n;
            }
            if (cursor >= n) cursor = 0;

            int steps = Math.min(budget, n);
            for (int i = 0; i < steps; i++) {
                T e;
                try {
                    e = source.get(cursor);
                } catch (IndexOutOfBoundsException ex) {
                    cursor = 0;
                    return;
                }
                cursor = (cursor + 1) % n;
                if (e == null) continue;
                if (visitor != null && e.isValid()) visitor.accept(e);

                Long old = keyOf.get(e);
                if (!e.isValid()) {
                    if (old != null) detach(e, old);
                    continue;
                }
                long k = keyFor(e);
                if (old != null && old == k) continue;
                if (old != null) detach(e, old);
                cells.computeIfAbsent(k, x -> new ArrayList<>(4)).add(e);
                keyOf.put(e, k);
            }
        }

        private void detach(T e, long k) {
            List<T> list = cells.get(k);
            if (list != null) {
                list.remove(e);
                if (list.isEmpty()) cells.remove(k);
            }
            keyOf.remove(e);
        }

        /** Up to {@code k} closest entities within {@code maxDist} that pass the filter, nearest first. */
        synchronized List<T> nearest(double x, double y, double maxDist, int k, Predicate<T> filter) {
            List<T> best = new ArrayList<>(k + 1);
            List<Double> bestD = new ArrayList<>(k + 1);
            long cx = (long) Math.floor(x / cell), cy = (long) Math.floor(y / cell);
            int maxRing = (int) Math.ceil(maxDist / cell) + 1;
            double maxSq = maxDist * maxDist;

            for (int r = 0; r <= maxRing; r++) {
                for (long dx = -r; dx <= r; dx++) {
                    for (long dy = -r; dy <= r; dy++) {
                        if (Math.max(Math.abs(dx), Math.abs(dy)) != r) continue;
                        List<T> bucket = cells.get(key(cx + dx, cy + dy));
                        if (bucket == null) continue;
                        for (int i = 0; i < bucket.size(); i++) {
                            T e = bucket.get(i);
                            if (!e.isValid()) continue;
                            double ex = x - e.locationInfo.now.x, ey = y - e.locationInfo.now.y;
                            double d2 = ex * ex + ey * ey;
                            if (d2 > maxSq || !filter.test(e)) continue;

                            int pos = best.size();
                            while (pos > 0 && bestD.get(pos - 1) > d2) pos--;
                            if (pos < k) {
                                best.add(pos, e);
                                bestD.add(pos, d2);
                                if (best.size() > k) {
                                    best.remove(k);
                                    bestD.remove(k);
                                }
                            }
                        }
                    }
                }
                // anything in ring r+1 or beyond is at least r*cell away
                if (best.size() >= k && bestD.get(k - 1) <= (r * cell) * (r * cell)) break;
            }
            return best;
        }
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
                // our own hotkeys are commands, not "the player is playing": they must not delay the click they trigger
                boolean ours = keyMatches(selectorKeyCode, nativeEvent) || keyMatches(cycleKeyCode, nativeEvent)
                        || keyMatches(pinKeyCode, nativeEvent) || keyMatches(assistKeyCode, nativeEvent)
                        || keyMatches(petKeyCode, nativeEvent);
                if (!ours && isOwnAppFocused()) com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder.markUserInput();
                if (!isTutucuActive()) return;
                // Toggle on release only to avoid native auto-repeat spam and stuck pressed states.
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                handleHotkey(keyMatches(selectorKeyCode, nativeEvent), keyMatches(cycleKeyCode, nativeEvent),
                        keyMatches(pinKeyCode, nativeEvent), keyMatches(assistKeyCode, nativeEvent), keyMatches(petKeyCode, nativeEvent));
            }
        };

        nativeMouseListener = new com.github.kwhat.jnativehook.mouse.NativeMouseListener() {
            // Some hook builds never report the release of the extra buttons, so react to the press too;
            // the debounce in handleHotkey makes sure one physical click is one action.
            @Override
            public void nativeMousePressed(com.github.kwhat.jnativehook.mouse.NativeMouseEvent e) {
                onMouseButton(e.getButton());
            }

            @Override
            public void nativeMouseReleased(com.github.kwhat.jnativehook.mouse.NativeMouseEvent e) {
                onMouseButton(e.getButton());
            }
        };

        // registerNativeHook() blocks ~1s on Windows first call; run off the tick thread to avoid freezing the bot.
        Thread hookThread = new Thread(() -> {
            try {
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(nativeKeyListener);
                GlobalScreen.addNativeMouseListener(nativeMouseListener);
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

    private long lastMouseDiag;

    private void onMouseButton(int button) {
        boolean ours = button == selectorMouse || button == cycleMouse || button == pinMouse || button == assistMouse
                || button == petMouse;
        if (!ours && isOwnAppFocused()) com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder.markUserInput();
        if (button < 3) return; // left/right clicks are never hotkeys
        boolean isLock = button == selectorMouse, isCycle = button == cycleMouse, isPin = button == pinMouse;
        boolean isAssist = button == assistMouse, isPet = button == petMouse;
        long now = System.currentTimeMillis();
        if (now - lastMouseDiag > 1500) {
            lastMouseDiag = now;
            System.out.println("Tutucu hotkey: mouse button " + button + " (lock=" + selectorMouse + ", cycle=" + cycleMouse
                    + ", pin=" + pinMouse + ") match=" + (isLock || isCycle || isPin) + " active=" + isTutucuActive() + " ownAppFocused=" + isOwnAppFocused());
        }
        if (!isLock && !isCycle && !isPin && !isAssist && !isPet) return;
        if (!isTutucuActive() || !isOwnAppFocused()) return; // a mouse button is never "typing"
        trigger(isLock, isCycle, isPin, isAssist, isPet);
    }

    private long lastKeyDiag;

    private void handleHotkey(boolean isLock, boolean isCycle, boolean isPin, boolean isAssist, boolean isPet) {
        if (!isLock && !isCycle && !isPin && !isAssist && !isPet) return;
        boolean active = isTutucuActive(), focused = isOwnAppFocused(), typing = isTypingInTextField();
        long now = System.currentTimeMillis();
        if ((!active || !focused || typing) && now - lastKeyDiag > 1500) {
            lastKeyDiag = now;
            System.out.println("Tutucu hotkey: key matched but ignored (active=" + active + " ownAppFocused=" + focused
                    + " typingInTextField=" + typing + ")");
        }
        if (!active || !focused || typing) return;
        trigger(isLock, isCycle, isPin, isAssist, isPet);
    }

    private void trigger(boolean isLock, boolean isCycle, boolean isPin, boolean isAssist, boolean isPet) {

        long now = System.currentTimeMillis();
        if (now - lastHotkeyToggleMs < 250) return;
        lastHotkeyToggleMs = now;
        if (isPet) petTargetRequested = true;
        else if (isAssist) assistRequested = true;
        else if (isPin) pinRequested = true;
        else if (isLock) lockNextRequested = true;
        else if (isCycle) cycleRequested = true;
    }

    /** "MOUSE3" (middle), "MOUSE4" (back), "MOUSE5" (forward) and friendly aliases; 0 if not a mouse button. */
    public static int parseMouseButton(String key) {
        if (key == null) return 0;
        switch (key.trim().toUpperCase(Locale.ROOT).replace(" ", "")) {
            case "MOUSE3": case "MIDDLE": case "MIDDLEMOUSE": case "WHEEL": return 3;
            case "MOUSE4": case "BACK": case "SIDE1": case "X1": return 4;
            case "MOUSE5": case "FORWARD": case "SIDE2": case "X2": return 5;
            default: return 0;
        }
    }

    private void uninstallGlobalHotkey() {
        if (!nativeHookRegistered) return;

        try {
            if (nativeKeyListener != null) GlobalScreen.removeNativeKeyListener(nativeKeyListener);
            if (nativeMouseListener != null) GlobalScreen.removeNativeMouseListener(nativeMouseListener);
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            LOG.log(Level.WARNING, "Tutucu global hotkey unload failed", e);
        } finally {
            nativeHookRegistered = false;
            nativeKeyListener = null;
            nativeMouseListener = null;
            selectorKeyCode = -1;
            selectorMouse = cycleMouse = 0;
        }
    }

    /** True when the foreground window belongs to this process (bot UI or the game window it hosts). */
    private static boolean isOwnAppFocused() {
        try {
            com.sun.jna.platform.win32.WinDef.HWND fg = com.sun.jna.platform.win32.User32.INSTANCE.GetForegroundWindow();
            if (fg == null) return false;
            com.sun.jna.ptr.IntByReference pid = new com.sun.jna.ptr.IntByReference();
            com.sun.jna.platform.win32.User32.INSTANCE.GetWindowThreadProcessId(fg, pid);
            return pid.getValue() == ProcessHandle.current().pid();
        } catch (Throwable t) {
            return true; // never block the hotkey just because the check failed
        }
    }

    private static boolean isTypingInTextField() {
        java.awt.Component owner = java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return owner instanceof javax.swing.text.JTextComponent;
    }

    private boolean isTutucuActive() {
        return main.isRunning() && main.config.GENERAL.CURRENT_MODULE.equals(HakClanTutucuModule.class.getCanonicalName());
    }

    private void refreshSelectorKeyCode() {
        String selectorKey = config.GENERAL.HAK_CLAN_TUTUCU.SELECTOR_KEY;
        selectorMouse = parseMouseButton(selectorKey);
        cycleMouse = parseMouseButton(config.GENERAL.HAK_CLAN_TUTUCU.CYCLE_KEY);
        String pinKey = config.GENERAL.HAK_CLAN_TUTUCU.PIN_KEY;
        pinMouse = parseMouseButton(pinKey);
        pinKeyCode = pinMouse != 0 ? -1 : toNativeKeyCode(pinKey);
        String assistKey = config.GENERAL.HAK_CLAN_TUTUCU.ASSIST_KEY;
        assistMouse = parseMouseButton(assistKey);
        assistKeyCode = assistMouse != 0 ? -1 : toNativeKeyCode(assistKey);
        String petKey = config.GENERAL.HAK_CLAN_TUTUCU.PIN_PET_KEY;
        petMouse = parseMouseButton(petKey);
        petKeyCode = petMouse != 0 ? -1 : toNativeKeyCode(petKey);
        if (selectorKey == null || selectorKey.isBlank() || selectorMouse != 0) {
            selectorKeyCode = -1;
            cycleKeyCode = toNativeKeyCode(config.GENERAL.HAK_CLAN_TUTUCU.CYCLE_KEY);
            return;
        }

        int newKeyCode = toNativeKeyCode(selectorKey);
        if (newKeyCode != selectorKeyCode) {
            selectorKeyCode = newKeyCode;
        }
        cycleKeyCode = toNativeKeyCode(config.GENERAL.HAK_CLAN_TUTUCU.CYCLE_KEY);
    }

    /** Bound key flag: only the numeric keypad's key, not the same key on the main row. */
    private static final int NUMPAD_FLAG = 0x10000;
    private static final Map<String, Integer> KEY_NAMES = buildKeyNames();

    private static String normKey(String s) {
        return s.toUpperCase(Locale.ROOT).replaceAll("[\\s_\\-]", "");
    }

    /**
     * Every key the hook knows (its VC_* constants), by name. The codes are keyboard scan codes, not alphabet-ordered,
     * so a letter can't be computed from 'A' (that is why Z and X used to fail): always look them up.
     */
    private static Map<String, Integer> buildKeyNames() {
        Map<String, Integer> m = new HashMap<>();
        for (java.lang.reflect.Field f : NativeKeyEvent.class.getFields()) {
            if (!f.getName().startsWith("VC_") || f.getType() != int.class) continue;
            try {
                m.put(normKey(f.getName().substring(3)), f.getInt(null));
            } catch (IllegalAccessException ignored) {
                // skip
            }
        }
        String[][] aliases = {{"ESC", "ESCAPE"}, {"CTRL", "CONTROL"}, {"DEL", "DELETE"}, {"INS", "INSERT"},
                {"PGUP", "PAGEUP"}, {"PGDN", "PAGEDOWN"}, {"RETURN", "ENTER"}, {"SPACEBAR", "SPACE"},
                {"WIN", "META"}, {"WINDOWS", "META"}, {"SUPER", "META"}, {"CMD", "META"}, {"MENU", "CONTEXTMENU"},
                {"APPS", "CONTEXTMENU"}, {"PRTSC", "PRINTSCREEN"}, {"PRINTSCR", "PRINTSCREEN"}, {"TILDE", "BACKQUOTE"},
                {"GRAVE", "BACKQUOTE"}, {"APOSTROPHE", "QUOTE"}, {"BACKSLASH", "BACKSLASH"}, {"CAPS", "CAPSLOCK"},
                {"BREAK", "PAUSE"}};
        for (String[] a : aliases) if (m.containsKey(a[1]) && !m.containsKey(a[0])) m.put(a[0], m.get(a[1]));
        for (int d = 0; d <= 9; d++) {
            Integer code = m.get(String.valueOf(d));
            if (code == null) continue;
            m.put("NUMPAD" + d, code | NUMPAD_FLAG);
            m.put("NUM" + d, code | NUMPAD_FLAG);
            m.put("KP" + d, code | NUMPAD_FLAG);
        }
        return m;
    }

    private static boolean keyMatches(int bound, NativeKeyEvent e) {
        if (bound < 0) return false;
        boolean numpad = e.getKeyLocation() == NativeKeyEvent.KEY_LOCATION_NUMPAD;
        if ((bound & NUMPAD_FLAG) != 0) return numpad && e.getKeyCode() == (bound & 0xFFFF);
        return e.getKeyCode() == bound;
    }

    private int toNativeKeyCode(String selectorKey) {
        if (selectorKey == null || selectorKey.isBlank()) return -1;

        String text = selectorKey.trim();
        if (text.length() == 1) {
            char key = Character.toUpperCase(text.charAt(0));
            Integer named = KEY_NAMES.get(String.valueOf(key));
            if (named != null) return named; // letters and digits
            switch (key) {
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
                case ' ': return NativeKeyEvent.VC_SPACE;
                default: break;
            }
        }

        Integer byName = KEY_NAMES.get(normKey(text));
        if (byName != null) return byName;
        // "VC_X" / "VC X" spelled out
        if (normKey(text).startsWith("VC")) {
            byName = KEY_NAMES.get(normKey(text).substring(2));
            if (byName != null) return byName;
        }
        try {
            int raw = Integer.parseInt(text);
            if (raw >= 0) return raw; // a raw hook scan code
        } catch (NumberFormatException ignored) {
            // not a number
        }
        return -1;
    }

}
