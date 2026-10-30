package com.github.manolo8.darkbot.modules.vs;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import eu.darkbot.api.game.enums.PetGear;
import eu.darkbot.api.game.items.Item;
import eu.darkbot.api.game.items.ItemUseResult;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.managers.HeroItemsAPI;
import eu.darkbot.api.managers.PetAPI;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Player-versus-player brain for Tutucu.
 * <ul>
 *   <li>Target choice for 1:1 / 1:N fights: who is hurting us most (damage is attributed to the enemies that have us
 *   targeted, decaying over a few seconds) and who is closest to dying, with hysteresis so the lock doesn't flip-flop.</li>
 *   <li>Macros: timed item sequences fired off a trigger (enemy closing in, first hit, lock), e.g. {@code ISH_01@0;EMP_01@3000}.</li>
 *   <li>Pet gear (HP link by default), emergency heal/defence abilities, and an advisor that analyses both ships.</li>
 * </ul>
 */
public class VsMode {

    private static final double THREAT_HALF_LIFE_MS = 4000;
    private static final long MACRO_REARM_MS = 5000, MACRO_COOLDOWN_MS = 20_000;

    /** Defence / heal abilities in the order we'd rather use them. Unowned ones are skipped. */
    static final SelectableItem[] DEFENCE = {
            SelectableItem.Ability.AEGIS_HP_REPAIR, SelectableItem.Ability.AEGIS_SHIELD_REPAIR,
            SelectableItem.Ability.SOLACE, SelectableItem.Ability.SOLACE_PLUS_NANO_CLUSTER_REPAIRER_PLUS,
            SelectableItem.Ability.LIBERATOR_PLUS_SELF_REPAIR, SelectableItem.Ability.CITADEL_FORTIFY,
            SelectableItem.Ability.CITADEL_PROTECTION, SelectableItem.Ability.CITADEL_PLUS_PRISMATIC_ENDURANCE,
            SelectableItem.Tech.BATTLE_REPAIR_BOT2, SelectableItem.Tech.BATTLE_REPAIR_BOT,
            SelectableItem.Tech.SHIELD_BACKUP2, SelectableItem.Tech.BACKUP_SHIELDS,
    };
    static final SelectableItem.Formation[] OFFENSIVE_FORMATIONS = {
            SelectableItem.Formation.DOUBLE_ARROW, SelectableItem.Formation.BAT,
            SelectableItem.Formation.ARROW, SelectableItem.Formation.STAR,
    };
    static final SelectableItem.Formation[] DEFENSIVE_FORMATIONS = {
            SelectableItem.Formation.DOME, SelectableItem.Formation.TURTLE, SelectableItem.Formation.WHEEL,
    };

    private final Main main;
    private final HeroManager hero;
    private final Config.General.HakClanTutucu cfg;
    private final HeroItemsAPI items;
    private final PetAPI pet;
    private java.util.function.Predicate<Ship> hostile = s -> true;

    private final Map<Ship, double[]> threat = new IdentityHashMap<>(); // ship -> {threat}
    private final Map<Ship, Double> lastDistance = new IdentityHashMap<>();
    private int lastHeroEhp = -1;
    private long lastDamageAt;
    private long lastDiagAt;
    private long lastTickMs;

    // macro state
    private List<MacroStep> steps = List.of();
    private String parsedMacro = "";
    private long macroStartedAt, macroFinishedAt, quietSince;
    private boolean macroRunning;

    private long lastPetCheck, lastDefenceUse, lastSetupUse, lastAdviceAt;
    private volatile List<String> advice = List.of("VS mode off");

    private final PilotBrain pilot;
    private final EnemyProfiler profiler;

    public EnemyProfiler profiler() {
        return profiler;
    }
    private final SlotPlanner slotPlanner;

    /** Items named in the macro (the planner needs them on the bar too). */
    List<SelectableItem> macroItems() {
        List<SelectableItem> out = new ArrayList<>();
        for (MacroStep s : steps) out.add(s.item);
        return out;
    }

    public PilotBrain pilot() {
        return pilot;
    }

    SelectableItem bestDefensiveFormation() {
        return bestDefensive();
    }

    SelectableItem firstOffensiveFormation() {
        return firstOwned(OFFENSIVE_FORMATIONS);
    }

    public VsMode(Main main) {
        this.main = main;
        this.hero = main.hero;
        this.cfg = main.config.GENERAL.HAK_CLAN_TUTUCU;
        this.items = main.pluginAPI.requireAPI(HeroItemsAPI.class);
        this.pet = main.pluginAPI.requireAPI(PetAPI.class);
        this.profiler = new EnemyProfiler(main);
        this.pilot = new PilotBrain(this, main);
        this.slotPlanner = new SlotPlanner(main, this, items);
    }

    /** Lets the module tell VS who is an enemy and who is a friend (clan / faction rules live there). */
    public void setHostileTest(java.util.function.Predicate<Ship> hostile) {
        this.hostile = hostile;
    }

    /** Does this ship count for the macro, according to the "reacts to" switches in the strategy? */
    private boolean macroCares(Ship s, Ship current) {
        if (s instanceof com.github.manolo8.darkbot.core.entities.Npc) return cfg.VS_MACRO_ON_NPCS;
        if (hostile.test(s)) return cfg.VS_MACRO_ON_PLAYERS;
        return cfg.VS_MACRO_ON_FRIENDS && s == current; // a friend only counts if I locked them
    }

    public boolean isEnabled() {
        return cfg.VS_MODE;
    }

    public List<String> advice() {
        return advice;
    }

    // ------------------------------------------------------------------------------------------------ per tick

    /** Called every tick while VS mode is on. {@code enemies}: hostile player ships near the hero. */
    public void tick(List<Ship> enemies, Ship current, boolean engaged) {
        long now = System.currentTimeMillis();
        if (!parsedMacro.equals(cfg.VS_MACRO)) parseMacro(cfg.VS_MACRO);
        trackThreat(enemies, now);

        if (pilot.active()) {
            // the pilot decides items (insta-shield, heals, formation, EMP...) from measurements and a simulation
            pilot.tick(enemies, current, engaged);
        } else {
            if (cfg.VS_MACRO_ENABLED) runMacro(enemies, current, now);
            if (cfg.VS_AUTO_DEFENSE) emergencyDefence(now);
        }
        if (petWanted() && (engaged || !enemies.isEmpty() || current != null)) managePet(now);
        prepare(enemies, now);

        if (now - lastAdviceAt > 350) {
            lastAdviceAt = now;
            List<String> tips = cfg.VS_ADVISOR ? buildAdvice(enemies, current) : List.of();
            if (pilot.active()) {
                List<String> merged = new ArrayList<>(pilot.lines());
                merged.addAll(tips);
                advice = merged;
            } else advice = tips;
            hud = buildHud(enemies, current, now);
        }
        lastTickMs = now;
    }

    // ------------------------------------------------------------------------------------------------ targeting

    private void trackThreat(List<Ship> enemies, long now) {
        double dt = lastTickMs == 0 ? 0 : now - lastTickMs;
        double decay = Math.pow(0.5, dt / THREAT_HALF_LIFE_MS);
        threat.values().forEach(t -> t[0] *= decay);
        threat.keySet().removeIf(s -> !s.isValid());
        lastDistance.keySet().removeIf(s -> !s.isValid());

        int ehp = heroEhp();
        if (lastHeroEhp >= 0 && ehp < lastHeroEhp) {
            double damage = lastHeroEhp - ehp;
            lastDamageAt = now;
            // blame the ships that have us targeted; if nobody does (mines, rockets mid-flight) the closest ones
            List<Ship> culprits = new ArrayList<>();
            for (Ship s : enemies) if (s.isAttacking(hero)) culprits.add(s);
            if (culprits.isEmpty()) culprits.addAll(enemies.size() > 2 ? enemies.subList(0, 2) : enemies);
            for (Ship s : culprits) threat.computeIfAbsent(s, k -> new double[1])[0] += damage / culprits.size();
        }
        lastHeroEhp = ehp;
    }

    public double threatOf(Ship s) {
        double[] t = threat.get(s);
        return t == null ? 0 : t[0];
    }

    /** 0 = about to die, 1 = full health+shield. Unknown max -> 1 so it doesn't distort the choice. */
    static double lifeFraction(Ship s) {
        int max = s.health.maxHp + s.health.maxShield;
        if (max <= 0) return 1;
        return Math.max(0, Math.min(1, (double) (s.health.hp + s.health.shield) / max));
    }

    private double score(Ship s, double maxThreat) {
        double threatN = maxThreat <= 0 ? (s.isAttacking(hero) ? 1 : 0) : threatOf(s) / maxThreat;
        double lifeN = 1 - lifeFraction(s);
        double finisher = lifeN > 0.8 ? 0.3 : 0;
        double attacking = s.isAttacking(hero) ? 0.15 : 0;

        switch (cfg.VS_PRIORITY) {
            case THREAT: return threatN + attacking + lifeN * 0.05;
            case LEAST_LIFE: return lifeN + finisher + attacking * 0.3;
            default: return 0.6 * threatN + 0.4 * lifeN + finisher + attacking;
        }
    }

    /**
     * The target we should be on, or the current one if switching isn't worth it yet (hysteresis + delay).
     * Returns null when there are no enemies.
     */
    public Ship pickTarget(List<Ship> enemies, Ship current, long lockedForMs) {
        if (enemies.isEmpty()) return null;
        // the AI pilot orders kills by (damage they deal) / (time to kill them) and knows who heals, runs or is invulnerable
        if (cfg.VS_PILOT == Config.General.HakClanTutucu.VsPilot.AUTO) return pilot.killOrderPick(enemies, current, lockedForMs);
        // 1:1 (or only one reachable): nothing to decide
        if (enemies.size() == 1) return enemies.get(0);

        double maxThreat = 0;
        for (Ship s : enemies) maxThreat = Math.max(maxThreat, threatOf(s));

        Ship best = null;
        double bestScore = -1;
        for (Ship s : enemies) {
            double sc = score(s, maxThreat);
            if (sc > bestScore) {
                bestScore = sc;
                best = s;
            }
        }
        if (current == null || !current.isValid() || !enemies.contains(current)) return best;
        if (best == current || lockedForMs < cfg.VS_SWITCH_DELAY) return current;
        return bestScore > score(current, maxThreat) + 0.2 ? best : current;
    }

    /** Nothing locked and someone has us targeted: the attacker to lock first (the worst one when several). */
    public Ship pickAttacker(List<Ship> enemies) {
        List<Ship> attackers = new ArrayList<>();
        for (Ship s : enemies) if (s.isAttacking(hero)) attackers.add(s);
        return pickTarget(attackers, null, 0);
    }

    // ------------------------------------------------------------------------------------------------ macros

    private static final class MacroStep {
        final SelectableItem item;
        final String label;
        final long delay;
        boolean done;
        int tries;
        long lastTry;

        MacroStep(SelectableItem item, String label, long delay) {
            this.item = item;
            this.label = label;
            this.delay = delay;
        }
    }

    /** Parses "ISH_01@0; EMP_01@3000" (names are enum constants, "-" works too). Unknown names are reported and skipped. */
    private void parseMacro(String text) {
        parsedMacro = text == null ? "" : text;
        List<MacroStep> out = new ArrayList<>();
        for (String part : parsedMacro.split("[;,]")) {
            part = part.trim();
            if (part.isEmpty()) continue;
            String name = part, delayText = "0";
            int at = part.indexOf('@');
            if (at >= 0) {
                name = part.substring(0, at).trim();
                delayText = part.substring(at + 1).trim();
            }
            long delay = 0;
            try {
                delay = Long.parseLong(delayText.replaceAll("[^0-9]", ""));
                String lower = delayText.toLowerCase(Locale.ROOT);
                if (lower.endsWith("s") && !lower.endsWith("ms")) delay *= 1000; // "3s"
            } catch (NumberFormatException ignored) {
                // keep 0
            }
            SelectableItem item = resolveItem(name);
            if (item == null) System.out.println("VS macro: unknown item '" + name + "' (use names like ISH_01, EMP_01, SMB_01)");
            else out.add(new MacroStep(item, name, delay));
        }
        out.sort((a, b) -> Long.compare(a.delay, b.delay));
        steps = out;
    }

    public static SelectableItem resolveItem(String raw) {
        String name = raw.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        Class<?>[] pools = {SelectableItem.Special.class, SelectableItem.Rocket.class, SelectableItem.Laser.class,
                SelectableItem.Tech.class, SelectableItem.Ability.class, SelectableItem.Cpu.class,
                SelectableItem.Formation.class, SelectableItem.Mine.class};
        for (Class<?> pool : pools) {
            for (Object c : pool.getEnumConstants()) {
                if (((Enum<?>) c).name().equals(name)) return (SelectableItem) c;
            }
        }
        return null;
    }

    private void runMacro(List<Ship> enemies, Ship current, long now) {
        if (!parsedMacro.equals(cfg.VS_MACRO)) parseMacro(cfg.VS_MACRO);
        if (steps.isEmpty()) return;

        double heroX = hero.locationInfo.now.x, heroY = hero.locationInfo.now.y;
        double range = cfg.VS_TRIGGER_DISTANCE;
        List<Ship> pool = new ArrayList<>(enemies);
        if (current != null && !pool.contains(current)) pool.add(current);

        // Only ships of the kinds the player chose can set the macro off, and only real aggression counts:
        // having me targeted (or me losing health with one of them close). An enemy just drifting nearby never does.
        boolean enemyNear = false, aimsNear = false, closingAimed = false, attacked = false, lockedMatch = false;
        for (Ship s : pool) {
            double d = Math.hypot(s.locationInfo.now.x - heroX, s.locationInfo.now.y - heroY);
            Double prev = lastDistance.put(s, d);
            if (!macroCares(s, current)) continue;
            boolean aims = s.isAttacking(hero);
            if (d <= range) enemyNear = true;
            if (aims) attacked = true;
            if (aims && d <= range) aimsNear = true;
            if (aims && d <= range && prev != null && d < prev - 1) closingAimed = true;
            if (s == current) lockedMatch = true;
        }
        boolean hit = attacked || (now - lastDamageAt < 1500 && enemyNear);

        int maxLife = hero.health.maxHp + hero.health.maxShield;
        int lifePercent = maxLife > 0 ? heroEhp() * 100 / maxLife : 100;
        boolean lifeOk = lifePercent <= cfg.VS_MACRO_BELOW_HP;

        if (now - lastDiagAt > 3000 && (enemyNear || hit || macroRunning)) {
            lastDiagAt = now;
            System.out.println("VS: enemies=" + pool.size() + " near=" + enemyNear + " aimsAtMe=" + aimsNear + " hit=" + hit
                    + " life=" + lifePercent + "% lifeOk=" + lifeOk + " trigger=" + cfg.VS_MACRO_TRIGGER + " steps=" + steps.size()
                    + " running=" + macroRunning + " cooldownLeft=" + Math.max(0, MACRO_COOLDOWN_MS - (now - macroFinishedAt)) / 1000 + "s");
        }

        if (!macroRunning) {
            if (enemyNear || hit) quietSince = 0;
            else if (quietSince == 0) quietSince = now;
            // hard cooldown so it never machine-guns items; re-armed early once things have been quiet for a while
            boolean armed = now - macroFinishedAt > MACRO_COOLDOWN_MS;
            boolean fire;
            switch (cfg.VS_MACRO_TRIGGER) {
                case FIRST_HIT: fire = hit; break;
                case ON_LOCK: fire = lockedMatch; break;
                default: fire = closingAimed || aimsNear; // they have me targeted and are in range
            }
            if (armed && fire && lifeOk) {
                macroRunning = true;
                macroStartedAt = now;
                for (MacroStep s : steps) {
                    s.done = false;
                    s.tries = 0;
                }
                System.out.println("VS macro: started (" + cfg.VS_MACRO_TRIGGER + ")");
            }
            if (quietSince != 0 && now - quietSince > MACRO_REARM_MS) macroFinishedAt = 0;
            return;
        }

        boolean all = true;
        for (MacroStep s : steps) {
            if (s.done) continue;
            all = false;
            if (now - macroStartedAt < s.delay || now - s.lastTry < 120) continue;
            s.lastTry = now;
            ItemUseResult r = items.useItem(s.item);
            if (r.isSuccessful() || r == ItemUseResult.ALREADY_SELECTED || r == ItemUseResult.NOT_AVAILABLE
                    || r == ItemUseResult.NOT_USABLE || r == ItemUseResult.FAILED
                    || r == ItemUseResult.INSUFFICIENT_QUANTITY || ++s.tries >= 6) {
                s.done = true;
                System.out.println("VS macro: " + s.label + " -> " + r + " at +" + (now - macroStartedAt) + "ms");
            }
        }
        if (all) {
            macroRunning = false;
            macroFinishedAt = now;
        }
    }

    // ------------------------------------------------------------------------------------------------ ammo mix

    private boolean mixOnB;
    private long mixPhaseStart;

    /**
     * The laser ammo the attack should be using right now when mixing (e.g. x4 + RSB), or null to leave the bot's
     * normal ammo logic alone. The attack loop already re-selects whatever this returns, so a switch is one key press.
     */
    public SelectableItem.Laser ammoChoice(Ship target) {
        SelectableItem.Laser empty = pilot.ammoWhenEmpty();
        if (empty != null) return empty; // the selected ammo ran out: don't stall on an empty slot
        if (!cfg.VS_MIX_ACTIVE()) return pilot.learnedAmmo(target);
        SelectableItem.Laser a = cfg.VS_AMMO_A, b = cfg.VS_AMMO_B;
        boolean aOk = canSelect(a), bOk = canSelect(b);
        if (!aOk && !bOk) return null;
        if (!aOk) return b;
        if (!bOk) return a;

        long now = System.currentTimeMillis();
        if (mixPhaseStart == 0) mixPhaseStart = now;
        long held = now - mixPhaseStart;
        boolean wantB;
        if (cfg.VS_AMMO_MIX_MODE == Config.General.HakClanTutucu.AmmoMixMode.BY_SHIELD) {
            double shield = target != null && target.health.maxShield > 0 ? (double) target.health.shield / target.health.maxShield : 1;
            wantB = shield * 100 < cfg.VS_AMMO_SHIELD_BELOW;
            if (wantB != mixOnB && held < 1500) wantB = mixOnB; // don't flip-flop on a flickering reading
        } else {
            wantB = mixOnB ? held < cfg.VS_AMMO_B_SECONDS * 1000L : held >= cfg.VS_AMMO_A_SECONDS * 1000L;
        }
        if (wantB != mixOnB) {
            mixOnB = wantB;
            mixPhaseStart = now;
        }
        return mixOnB ? b : a;
    }

    // ------------------------------------------------------------------------------------------------ pet & defence

    /** The pet is actually out (spawned in the map), not just "enabled" in some setting. */
    boolean petOut() {
        try {
            return pet.isActive() && pet.isValid();
        } catch (RuntimeException e) {
            return false;
        }
    }

    boolean petWanted() {
        return cfg.VS_PET_ENABLED || cfg.VS_PILOT == Config.General.HakClanTutucu.VsPilot.AUTO;
    }

    private PetGear pickPetGear() {
        PetGear[] order = {cfg.VS_PET_GEAR, PetGear.HP_LINK, PetGear.COMBO_GUARD, PetGear.GUARD};
        for (PetGear g : order) if (g != null && pet.hasGear(g)) return g;
        return null;
    }

    private void managePet(long now) {
        if (now - lastPetCheck < 1500) return;
        lastPetCheck = now;
        try {
            // The bot's pet manager does nothing unless the master Pet switch is on, so a gear set from here was never
            // applied. Turn it on (it is what summons the pet and applies gears), and say so.
            if (!main.config.PET.ENABLED) {
                main.config.PET.ENABLED = true;
                System.out.println("VS pet: turned the bot's Pet setting on (needed to call the pet and change its gear)");
            }
            if (!pet.isEnabled()) pet.setEnabled(true); // asks it to come out
            if (!petOut()) return;
            PetGear gear = pickPetGear();
            if (gear != null && pet.getGear() != gear && !pet.hasCooldown(gear)) {
                pet.setGear(gear);
                System.out.println("VS pet: gear -> " + gear);
            }
        } catch (Exception e) {
            // gear not equipped / pet busy: try again next round
        }
    }

    private void emergencyDefence(long now) {
        if (now - lastDefenceUse < 900) return;
        int max = hero.health.maxHp + hero.health.maxShield;
        double fraction = max <= 0 ? 1 : (double) heroEhp() / max;
        if (fraction * 100 > cfg.VS_DEFENCE_BELOW) return;
        if (now - lastDamageAt > 4000) return; // low but nobody is hurting us: don't burn the ability
        for (SelectableItem item : DEFENCE) {
            Item it = items.getItem(item).orElse(null);
            if (it == null || !it.isAvailable() || !it.isUsable() || !it.isReady()) continue;
            if (items.useItem(item).isSuccessful()) {
                lastDefenceUse = now;
                System.out.println("VS defence: used " + pretty(item) + " at " + (int) (fraction * 100) + "%");
                return;
            }
        }
    }

    int heroEhp() {
        return hero.health.hp + hero.health.shield;
    }

    // ------------------------------------------------------------------------------------------------ advisor

    boolean has(SelectableItem item) {
        if (item == null) return false;
        return items.getItem(item).map(Item::isAvailable).orElse(false);
    }

    /** Owned AND reachable by a slot-bar key (which is how the bot selects items). */
    private boolean canSelect(SelectableItem item) {
        if (item == null) return false;
        return items.getItem(item).map(i -> i.isAvailable() && i.isUsable()).orElse(false);
    }

    private SelectableItem.Formation firstOwned(SelectableItem.Formation[] list) {
        for (SelectableItem.Formation f : list) if (canSelect(f)) return f;
        return null;
    }

    /** The owned formation with the best shield x health multiplier (pure defence). */
    private SelectableItem.Formation bestDefensive() {
        SelectableItem.Formation best = firstOwned(DEFENSIVE_FORMATIONS);
        double bestValue = best == null ? 0 : best.getShieldMultiplier() * best.getHealthMultiplier();
        for (SelectableItem.Formation f : SelectableItem.Formation.values()) {
            if (!canSelect(f)) continue;
            double v = f.getShieldMultiplier() * f.getHealthMultiplier();
            if (v > bestValue + 0.01) {
                bestValue = v;
                best = f;
            }
        }
        return best;
    }

    /** "DOUBLE_ARROW" -> "Double arrow", "UCB_100" -> "UCB-100" (never the raw internal id). */
    public static String pretty(SelectableItem item) {
        if (item == null) return "-";
        if (!(item instanceof Enum)) return item.getId();
        String name = ((Enum<?>) item).name();
        if (item instanceof SelectableItem.Formation || item instanceof SelectableItem.Ability || item instanceof SelectableItem.Tech) {
            String words = name.toLowerCase(Locale.ROOT).replace('_', ' ');
            return Character.toUpperCase(words.charAt(0)) + words.substring(1);
        }
        return name.replace('_', '-');
    }

    public boolean owns(SelectableItem item) {
        return has(item);
    }

    /** What the setup should be against these enemies: {formation, laser}; either can be null (no change). */
    private SelectableItem[] recommendedSetup(List<Ship> enemies) {
        if (enemies.isEmpty()) return new SelectableItem[2];
        int attackers = 0;
        double enemyShield = 0;
        for (Ship s : enemies) {
            if (s.isAttacking(hero)) attackers++;
            if (s.health.maxShield > 0) enemyShield = Math.max(enemyShield, (double) s.health.shield / s.health.maxShield);
        }
        SelectableItem.Formation formation = attackers >= 2 || enemies.size() >= 3 ? bestDefensive() : firstOwned(OFFENSIVE_FORMATIONS);

        SelectableItem.Laser laser = null;
        if (enemyShield > 0.5 && canSelect(SelectableItem.Laser.SAB_50)) laser = SelectableItem.Laser.SAB_50; // drain shield first
        else if (canSelect(SelectableItem.Laser.RSB_75)) laser = SelectableItem.Laser.RSB_75;
        else if (canSelect(SelectableItem.Laser.UCB_100)) laser = SelectableItem.Laser.UCB_100;
        return new SelectableItem[]{formation, laser};
    }

    private volatile boolean prepareRequested;
    private long prepareUntil;
    private String prepareReport = "";

    /** "Prepare for battle": select the strategy's formation / ammo / rocket / pet gear now, one step at a time. */
    public void requestPrepare() {
        prepareRequested = true;
    }

    public String prepareReport() {
        return prepareReport;
    }

    private static final class Attempt {
        int tries;
        long first, last;
    }

    private final Map<SelectableItem, Attempt> attempts = new java.util.HashMap<>();

    private void prepare(List<Ship> enemies, long now) {
        if (prepareRequested) {
            prepareRequested = false;
            prepareUntil = now + 6000;
            attempts.clear();
            prepareReport = "Preparing...";
        }
        boolean manual = now < prepareUntil;
        if (!manual && !(cfg.VS_AUTO_SETUP && !enemies.isEmpty())) return;

        SelectableItem[] advised = recommendedSetup(enemies);
        SelectableItem[] wanted = {
                cfg.VS_PREP_FORMATION != null ? cfg.VS_PREP_FORMATION : advised[0],
                cfg.VS_PREP_LASER != null ? cfg.VS_PREP_LASER : advised[1],
                cfg.VS_PREP_ROCKET};

        List<String> problems = new ArrayList<>();
        boolean pending = false;
        try {
            for (SelectableItem w : wanted) {
                if (w == null) continue;
                Item it = items.getItem(w).orElse(null);
                if (it == null) {
                    problems.add(pretty(w) + ": not found on your bars");
                    continue;
                }
                if (it.isSelected()) continue; // already what we want
                if (!it.isAvailable()) {
                    problems.add(pretty(w) + ": none left");
                    continue;
                }
                if (!it.isUsable()) {
                    // The bot selects items by pressing their slot-bar key; the native "select" call is off (it crashes).
                    problems.add(pretty(w) + ": not on a slot bar (put it on slot 1-0)");
                    continue;
                }

                pending = true;
                Attempt at = attempts.computeIfAbsent(w, k -> new Attempt());
                if (now - at.first > 15_000) { // new encounter: start counting again
                    at.tries = 0;
                    at.first = now;
                }
                if (at.tries >= 3) {
                    problems.add(pretty(w) + ": pressed " + at.tries + "x but the game didn't select it");
                    pending = false;
                    continue;
                }
                if (now - at.last < 900 || now - lastSetupUse < 300) continue;

                ItemUseResult r = items.useItem(w);
                at.tries++;
                at.last = lastSetupUse = now;
                System.out.println("VS prepare: " + pretty(w) + " -> " + r + " (try " + at.tries + ")");
                if (!r.isSuccessful() && r != ItemUseResult.RECENTLY_USED && r != ItemUseResult.NOT_READY)
                    problems.add(pretty(w) + ": " + r);
                break; // one key press per step, then let the game catch up
            }
            if (cfg.VS_PET_ENABLED) lastPetCheck = 0; // let the pet gear go through right away
        } catch (RuntimeException e) {
            return; // items not readable right now
        }

        if (manual) {
            if (pending && now < prepareUntil) prepareReport = "Preparing...";
            else {
                prepareUntil = 0;
                prepareReport = problems.isEmpty() ? "Ready for battle" : String.join("; ", problems);
                System.out.println("VS prepare: " + prepareReport);
            }
        }
    }

    private List<String> buildAdvice(List<Ship> enemies, Ship current) {
        List<String> out = new ArrayList<>();
        try {
            int max = hero.health.maxHp + hero.health.maxShield;
            int ehp = heroEhp();
            if (enemies.isEmpty()) return out;

            int attackers = 0;
            Ship top = null;
            double fastest = 0;
            for (Ship s : enemies) {
                if (s.isAttacking(hero)) attackers++;
                if (top == null || threatOf(s) > threatOf(top)) top = s;
                fastest = Math.max(fastest, s.getSpeed());
            }
            if (top != null && threatOf(top) > 0) out.add("Biggest threat: " + safeName(top));

            SelectableItem[] want = recommendedSetup(enemies);
            if (want[0] != null && want[0] != hero.getFormation())
                out.add("Formation: switch to " + pretty(want[0]) + (attackers >= 2 ? " (outnumbered, defend)" : " (duel, damage)"));
            if (want[1] != null && want[1] != hero.getLaser()) out.add("Ammo: use " + pretty(want[1]));
            if (fastest > hero.getSpeed() * 1.05 && has(SelectableItem.Rocket.DCR_250))
                out.add("Enemy is faster (" + (int) fastest + " vs " + hero.getSpeed() + "): slow it with DCR-250");
            if (attackers >= 2 && has(SelectableItem.Special.SMB_01)) out.add("Grouped enemies: SMB-01 hits them all");
            if (max > 0 && ehp * 100 / max < cfg.VS_DEFENCE_BELOW + 15) out.add("Low health: heal / defence ability now, consider backing off");
            if (cfg.VS_PET_ENABLED && petOut() && pet.getGear() != cfg.VS_PET_GEAR && pet.hasGear(cfg.VS_PET_GEAR))
                out.add("Pet: switch to " + cfg.VS_PET_GEAR.getName());
        } catch (RuntimeException e) {
            out.add("Analysing...");
        }
        return out;
    }

    // ------------------------------------------------------------------------------------------------ readiness

    private void itemProblem(SelectableItem it, List<String> out) {
        if (it == null) return;
        Item i = items.getItem(it).orElse(null);
        String name = pretty(it);
        String problem = i == null ? name + ": not found on your bars"
                : !i.isAvailable() ? name + ": none left"
                : !i.isUsable() ? name + ": not on a slot bar (slot 1-0)" : null;
        if (problem != null && !out.contains(problem)) out.add(problem);
    }

    /** Everything the strategy relies on, checked before the fight: empty list = battle ready. */
    private List<String> readiness() {
        List<String> out = new ArrayList<>();
        if (cfg.VS_MACRO_ENABLED) for (MacroStep st : steps) itemProblem(st.item, out);
        itemProblem(cfg.VS_PREP_FORMATION, out);
        itemProblem(cfg.VS_PREP_LASER, out);
        itemProblem(cfg.VS_PREP_ROCKET, out);
        if (cfg.VS_MIX_ACTIVE()) {
            itemProblem(cfg.VS_AMMO_A, out);
            itemProblem(cfg.VS_AMMO_B, out);
        }
        if (petWanted()) {
            if (!petOut()) out.add("Pet is not out yet (it will be called when a fight starts)");
            else if (pickPetGear() == null) out.add("Pet has no usable gear (HP link / guard)");
        }
        if (cfg.VS_AUTO_DEFENSE) {
            boolean any = false;
            for (SelectableItem d : DEFENCE) {
                Item i = items.getItem(d).orElse(null);
                if (i != null && i.isAvailable() && i.isUsable()) {
                    any = true;
                    break;
                }
            }
            if (!any) out.add("Auto defence: no heal/defence ability on a slot bar");
        }
        return out;
    }

    // ------------------------------------------------------------------------------------------------ HUD

    public enum ChipState {READY, COOLDOWN, MISSING}

    public static final class Chip {
        public final String label, detail;
        public final ChipState state;
        public final double fraction; // cooldown remaining 0..1

        Chip(String label, ChipState state, String detail, double fraction) {
            this.label = label;
            this.state = state;
            this.detail = detail;
            this.fraction = fraction;
        }
    }

    public static final class EnemyRow {
        public final String name;
        public final int distance, lifePercent, threatPercent;
        public final boolean attackingMe, isTarget;

        EnemyRow(String name, int distance, int lifePercent, int threatPercent, boolean attackingMe, boolean isTarget) {
            this.name = name;
            this.distance = distance;
            this.lifePercent = lifePercent;
            this.threatPercent = threatPercent;
            this.attackingMe = attackingMe;
            this.isTarget = isTarget;
        }
    }

    /** Immutable snapshot for the Dashboard, rebuilt a few times a second on the bot thread. */
    public static final class Hud {
        public final int heroLifePercent, speed;
        public final String formation, laser, rocket, petGear, macroStatus, mode, prepare;
        public final List<Chip> chips;
        public final List<EnemyRow> enemies;
        public final List<String> readiness;
        public final List<String> slotPlan;

        Hud(int heroLifePercent, int speed, String formation, String laser, String rocket, String petGear,
            String macroStatus, String mode, String prepare, List<Chip> chips, List<EnemyRow> enemies, List<String> readiness, List<String> slotPlan) {
            this.heroLifePercent = heroLifePercent;
            this.speed = speed;
            this.formation = formation;
            this.laser = laser;
            this.rocket = rocket;
            this.petGear = petGear;
            this.macroStatus = macroStatus;
            this.mode = mode;
            this.prepare = prepare;
            this.chips = chips;
            this.enemies = enemies;
            this.readiness = readiness;
            this.slotPlan = slotPlan;
        }
    }

    private volatile Hud hud;

    public Hud hud() {
        return hud;
    }

    private Chip chipFor(SelectableItem it) {
        Item i = items.getItem(it).orElse(null);
        String label = pretty(it);
        if (i == null || !i.isAvailable()) return new Chip(label, ChipState.MISSING, "not owned", 0);
        double qty = i.getQuantity();
        String amount = qty > 0 && qty < 100_000 ? "x" + (int) qty : "";
        if (i.isReady()) return new Chip(label, ChipState.READY, amount, 0);
        double left = 0, fraction = 1;
        var timer = i.getItemTimer().orElse(null);
        if (timer != null) {
            double total = timer.getTotalTime();
            boolean ms = total > 600;
            left = ms ? timer.getAvailableIn() / 1000.0 : timer.getAvailableIn();
            fraction = total > 0 ? Math.max(0, Math.min(1, timer.getAvailableIn() / total)) : 1;
        }
        return new Chip(label, ChipState.COOLDOWN, left > 0 ? String.format(Locale.ROOT, "%.0fs", left) : "cooldown", fraction);
    }

    private Hud buildHud(List<Ship> enemies, Ship current, long now) {
        try {
            int max = hero.health.maxHp + hero.health.maxShield;
            int life = max > 0 ? Math.min(100, heroEhp() * 100 / max) : 100;

            java.util.LinkedHashSet<SelectableItem> wanted = new java.util.LinkedHashSet<>();
            for (MacroStep step : steps) wanted.add(step.item);
            wanted.add(SelectableItem.Special.ISH_01);
            wanted.add(SelectableItem.Special.EMP_01);
            wanted.add(SelectableItem.Special.SMB_01);
            if (cfg.VS_PREP_ROCKET != null) wanted.add(cfg.VS_PREP_ROCKET);
            List<Chip> chips = new ArrayList<>();
            for (SelectableItem w : wanted) {
                if (chips.size() >= 8) break;
                chips.add(chipFor(w));
            }

            double maxThreat = 0;
            for (Ship s : enemies) maxThreat = Math.max(maxThreat, threatOf(s));
            List<EnemyRow> rows = new ArrayList<>();
            double hx = hero.locationInfo.now.x, hy = hero.locationInfo.now.y;
            for (Ship s : enemies) {
                if (rows.size() >= 4) break;
                rows.add(new EnemyRow(safeName(s),
                        (int) Math.hypot(s.locationInfo.now.x - hx, s.locationInfo.now.y - hy),
                        (int) (lifeFraction(s) * 100),
                        maxThreat > 0 ? (int) (threatOf(s) / maxThreat * 100) : 0,
                        s.isAttacking(hero), s == current));
            }

            String macro;
            if (!cfg.VS_MACRO_ENABLED || steps.isEmpty()) macro = "Macro off";
            else if (macroRunning) macro = "Macro running +" + (now - macroStartedAt) / 100 / 10.0 + "s";
            else if (now - macroFinishedAt < MACRO_COOLDOWN_MS && macroFinishedAt != 0)
                macro = "Macro cooling down " + (MACRO_COOLDOWN_MS - (now - macroFinishedAt)) / 1000 + "s";
            else macro = "Macro armed: " + cfg.VS_MACRO_TRIGGER.toString().split(" \\(")[0];

            int attackers = 0;
            for (Ship s : enemies) if (s.isAttacking(hero)) attackers++;
            String mode = enemies.isEmpty() ? "No enemy in range" : "1:" + enemies.size() + "  (" + attackers + " attacking you)";

            return new Hud(life, hero.getSpeed(), pretty(hero.getFormation()), pretty(hero.getLaser()), pretty(hero.getRocket()),
                    petOut() ? (pet.getGear() != null ? pet.getGear().getName() : "out") : "not out", macro, mode, prepareReport, chips, rows, readiness(), slotPlanner.plan());
        } catch (RuntimeException e) {
            return hud;
        }
    }

    private static String safeName(Ship s) {
        return s.playerInfo != null && s.playerInfo.username != null ? s.playerInfo.username : "enemy";
    }
}
