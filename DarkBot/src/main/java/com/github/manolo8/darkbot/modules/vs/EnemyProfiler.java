package com.github.manolo8.darkbot.modules.vs;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.entities.Player;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import eu.darkbot.api.game.enums.EntityEffect;
import eu.darkbot.api.game.items.SelectableItem;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Builds a profile of every player who attacks us (and of whoever is pinned) the moment they show up: who they are,
 * what their ship can do, and how they fight, then keeps refining it from what is observed.
 * <ul>
 *   <li>At once: name, clan, faction, rank, ship type (and what abilities that ship family can have), hull+shield,
 *   top speed, formation, pet.</li>
 *   <li>While they fight: damage per second on me and how it splits shield/hull, volley size and rhythm, the range they
 *   attack from, how they move (charge / circle / back off while shooting / camp), attack-pause cycles (hit and run),
 *   insta-shield uses and cooldown, cloaking, healing, formation changes.</li>
 *   <li>Remembered between sessions by name, so a returning player is known on sight.</li>
 * </ul>
 */
public final class EnemyProfiler {

    /** What the profile says about a player. Public fields so it can be saved as JSON. */
    public static final class Profile {
        public String name = "", clan = "", shipType = "";
        public int faction, rank, shipId;
        public double maxEhp, topSpeed;
        public boolean hasPet;
        public int encounters;
        public long firstSeen, lastSeen;

        public double aimSeconds, observedSeconds;
        public List<Integer> attackDistances = new ArrayList<>();
        public double dmgToMe, shieldDmg, hullDmg, dpsEma, maxBurst;
        public List<Long> burstTimes = new ArrayList<>();
        public double burstPeriod; // seconds, 0 = not regular
        public double votesCharge, votesOrbit, votesKite, votesCamp;
        public List<Double> attackSegs = new ArrayList<>(), pauseSegs = new ArrayList<>();
        public int ishCount;
        public double ishMinInterval = 0, ishDuration;
        public int cloakCount;
        public double cloakDuration;
        public int healEvents;
        public int offensiveFormation, defensiveFormation, formationChanges;
        public List<Integer> effects = new ArrayList<>();
    }

    /** Session-only state used to compute rates between ticks. */
    private static final class Live {
        long lastAt, aimStart, pauseStart, lastIshStart, lastCloakStart, lastBurstAt, lastDistAt, lastHealAt, announcedAt;
        double lastDist = -1, lastAngle = Double.NaN, radial, tangential, lastEhpFrac = -1;
        boolean aiming, ish, cloaked;
        SelectableItem lastFormation;
        long ishStart, cloakStart;
        final java.util.ArrayDeque<double[]> recentDamage = new java.util.ArrayDeque<>(); // {t, dmg}
    }

    public enum Style {CHARGER, ORBITER, KITER, CAMPER, UNKNOWN}

    private static final String[][] SHIP_FAMILIES = {
            {"aegis", "heals HP and shield, repair pod"},
            {"citadel", "draw fire, fortify, protection, travel"},
            {"spearhead", "ultimate cloak, Jam-X, target marker"},
            {"solace", "nano cluster repair"},
            {"spectrum", "prismatic reflecting shield"},
            {"tartarus", "speed boost, rapid fire"},
            {"mimesis", "hologram, scramble, phase out"},
            {"disruptor", "redirect, shield disarray, DDOL"},
            {"berserker", "berserker damage skills"},
            {"zephyr", "missile skills"},
            {"solaris", "incinerate"},
            {"keres", "special weapons"},
            {"retiarus", "special weapons"},
            {"orcus", "assimilate"},
            {"basilisk", "noxious nebula, heightened valour"},
            {"liberator", "self repair"},
            {"pusat", "speed sap"},
            {"tempest", "voltage link / discharge / backup"},
            {"hecate", "particle beam, stockpile"},
            {"hammerclaw", "reallocate"},
            {"goliath", "frozen claw (X)"},
            {"venom", "venom skill"},
            {"sentinel", "sentinel skill"},
            {"diminisher", "diminisher skill"},
            {"lightning", "lightning skill"},
    };

    private static final int MAX_PROFILES = 250;
    private static final long SAVE_EVERY_MS = 20_000;

    private final Main main;
    private final HeroManager hero;
    private final Gson gson = new GsonBuilder().create();
    private final Path file = Paths.get("hak-profiles.json");
    private final Map<String, Profile> profiles = new LinkedHashMap<>();
    private final Map<Ship, Live> live = new IdentityHashMap<>();

    private boolean dirty;
    private long lastSave, lastHeroAt;
    private double lastHeroShield = -1, lastHeroHull = -1;
    private volatile List<String> lines = List.of();

    public EnemyProfiler(Main main) {
        this.main = main;
        this.hero = main.hero;
        load();
        Runtime.getRuntime().addShutdownHook(new Thread(this::save, "profiles-save"));
    }

    // ------------------------------------------------------------------------------------------------ persistence

    private void load() {
        if (!Files.exists(file)) return;
        try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Map<String, Profile> loaded = gson.fromJson(r, new TypeToken<LinkedHashMap<String, Profile>>() {}.getType());
            if (loaded != null) profiles.putAll(loaded);
        } catch (IOException | RuntimeException e) {
            System.out.println("VS profile: couldn't read " + file + ": " + e);
        }
    }

    private synchronized void save() {
        if (!dirty) return;
        try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(profiles, w);
            dirty = false;
        } catch (IOException | RuntimeException e) {
            System.out.println("VS profile: couldn't save " + file + ": " + e);
        }
    }

    private void trim() {
        if (profiles.size() <= MAX_PROFILES) return;
        List<Map.Entry<String, Profile>> all = new ArrayList<>(profiles.entrySet());
        all.sort((a, b) -> Long.compare(a.getValue().lastSeen, b.getValue().lastSeen));
        for (int i = 0; i < all.size() - MAX_PROFILES; i++) profiles.remove(all.get(i).getKey());
    }

    // ------------------------------------------------------------------------------------------------ access

    public List<String> lines() {
        return lines;
    }

    public synchronized Profile profileOf(Ship s) {
        if (s == null || s.playerInfo == null || s.playerInfo.username == null) return null;
        return profiles.get(s.playerInfo.username.toLowerCase(Locale.ROOT));
    }

    // ------------------------------------------------------------------------------------------------ tick

    /**
     * @param ships   players worth profiling now (aiming at me, pinned, or my target)
     * @param focus   the ones to show on the HUD, most important first (target, pinned, attackers)
     */
    public synchronized void tick(List<Ship> ships, List<Ship> focus, long now) {
        live.keySet().removeIf(s -> !s.isValid());

        // my own health changes this tick, split shield / hull, to attribute to the attackers
        double sh = hero.health.shield, hu = hero.health.hp;
        double dShield = lastHeroShield < 0 ? 0 : Math.max(0, lastHeroShield - sh);
        double dHull = lastHeroShield < 0 ? 0 : Math.max(0, lastHeroHull - hu);
        lastHeroShield = sh;
        lastHeroHull = hu;
        double myMax = Math.max(1, hero.health.maxHp + hero.health.maxShield);

        List<Ship> aimers = new ArrayList<>();
        for (Ship s : ships) if (s.isAttacking(hero)) aimers.add(s);

        for (Ship s : ships) {
            if (!(s.playerInfo != null && s.playerInfo.username != null && !s.playerInfo.username.isBlank())) continue;
            observe(s, now, aimers, dShield, dHull, myMax);
        }

        List<String> out = new ArrayList<>();
        for (Ship s : focus) {
            Profile p = profileOf(s);
            if (p != null) out.addAll(describe(p, s, myMax));
            if (out.size() > 14) break;
        }
        lines = out;

        if (dirty && now - lastSave > SAVE_EVERY_MS) {
            lastSave = now;
            trim();
            save();
        }
    }

    private void observe(Ship s, long now, List<Ship> aimers, double dShield, double dHull, double myMax) {
        String key = s.playerInfo.username.toLowerCase(Locale.ROOT);
        Profile p = profiles.get(key);
        Live l = live.get(s);
        boolean aiming = s.isAttacking(hero);

        if (p == null) {
            p = new Profile();
            p.name = s.playerInfo.username;
            p.firstSeen = now;
            profiles.put(key, p);
        }
        if (l == null) {
            l = new Live();
            live.put(s, l);
            if (now - p.lastSeen > 5 * 60_000L) p.encounters++; // a new meeting, not a flicker in the list
            if (p.encounters == 0) p.encounters = 1;
        }
        p.lastSeen = now;
        dirty = true;

        // ---- who they are and what they have (instantly)
        p.clan = s.playerInfo.clanTag == null ? "" : s.playerInfo.clanTag;
        p.faction = s.playerInfo.factionId;
        p.rank = s.playerInfo.rank;
        p.shipId = s.getShipId();
        if (s instanceof Player) {
            Player pl = (Player) s;
            String type = pl.getShipType();
            if (type != null && !type.isBlank()) p.shipType = type;
            p.hasPet = pl.hasPet() || p.hasPet;
        }
        double max = s.health.maxHp + s.health.maxShield;
        if (max > p.maxEhp) p.maxEhp = max;
        if (s.getSpeed() > p.topSpeed) p.topSpeed = s.getSpeed();
        for (Integer e : s.getEffects()) if (!p.effects.contains(e) && p.effects.size() < 24) p.effects.add(e);

        double dt = l.lastAt == 0 ? 0 : Math.min(0.25, (now - l.lastAt) / 1000.0);
        l.lastAt = now;

        // ---- announce at first sight of an attacker / pinned player
        if ((aiming || false) && l.announcedAt == 0) {
            l.announcedAt = now;
            System.out.println("VS profile: " + String.join(" | ", describe(p, s, myMax)));
        }

        double dx = s.locationInfo.now.x - hero.locationInfo.now.x, dy = s.locationInfo.now.y - hero.locationInfo.now.y;
        double dist = Math.hypot(dx, dy);
        double angle = Math.atan2(dy, dx);
        if (dt > 0) p.observedSeconds += dt;

        // ---- movement relative to me (smoothed): closing speed and sideways speed
        if (l.lastDist >= 0 && dt > 0.02) {
            double rad = (l.lastDist - dist) / dt; // + = coming closer
            double dAng = angle - l.lastAngle;
            while (dAng > Math.PI) dAng -= 2 * Math.PI;
            while (dAng < -Math.PI) dAng += 2 * Math.PI;
            double tan = Math.abs(dAng) * dist / dt;
            l.radial = l.radial * 0.85 + rad * 0.15;
            l.tangential = l.tangential * 0.85 + tan * 0.15;
        }
        l.lastDist = dist;
        l.lastAngle = angle;

        if (aiming && dt > 0) {
            p.aimSeconds += dt;
            if (p.attackDistances.size() < 60 && now - l.lastDistAt > 500) {
                p.attackDistances.add((int) dist);
                l.lastDistAt = now;
            } else if (p.attackDistances.size() >= 60 && now - l.lastDistAt > 2000) {
                p.attackDistances.set((int) (now / 2000 % 60), (int) dist);
                l.lastDistAt = now;
            }
            if (l.tangential > 120 && l.tangential > 1.5 * Math.abs(l.radial)) p.votesOrbit += dt;
            else if (l.radial > 60) p.votesCharge += dt;
            else if (l.radial < -60) p.votesKite += dt;
            else p.votesCamp += dt;
        }

        // ---- attack / pause rhythm (hit and run shows up here)
        if (aiming && !l.aiming) {
            if (l.pauseStart > 0) addCapped(p.pauseSegs, (now - l.pauseStart) / 1000.0);
            l.aimStart = now;
        } else if (!aiming && l.aiming) {
            addCapped(p.attackSegs, (now - l.aimStart) / 1000.0);
            l.pauseStart = now;
        }
        l.aiming = aiming;

        // ---- insta-shield and cloak: how often, how long
        boolean ish = invulnerable(s);
        if (ish && !l.ish) {
            l.ishStart = now;
            p.ishCount++;
            if (l.lastIshStart > 0 && now - l.lastIshStart > 3000) {
                double interval = (now - l.lastIshStart) / 1000.0;
                p.ishMinInterval = p.ishMinInterval == 0 ? interval : Math.min(p.ishMinInterval, interval);
            }
            l.lastIshStart = now;
        } else if (!ish && l.ish) {
            double dur = (now - l.ishStart) / 1000.0;
            p.ishDuration = p.ishDuration == 0 ? dur : p.ishDuration * 0.7 + dur * 0.3;
        }
        l.ish = ish;

        boolean cloaked = s.isInvisible();
        if (cloaked && !l.cloaked) {
            l.cloakStart = now;
            p.cloakCount++;
        } else if (!cloaked && l.cloaked) {
            double dur = (now - l.cloakStart) / 1000.0;
            p.cloakDuration = p.cloakDuration == 0 ? dur : p.cloakDuration * 0.7 + dur * 0.3;
        }
        l.cloaked = cloaked;

        // ---- healing: a jump up of more than 4% of its health in one step is a heal, not shield regeneration
        double frac = VsMode.lifeFraction(s);
        if (l.lastEhpFrac >= 0 && frac - l.lastEhpFrac > 0.04 && now - l.lastHealAt > 1000) {
            p.healEvents++;
            l.lastHealAt = now;
        }
        l.lastEhpFrac = frac;

        // ---- formation
        if (s instanceof Player) {
            SelectableItem f = ((Player) s).getFormation();
            if (f != null && f != l.lastFormation) {
                if (l.lastFormation != null) p.formationChanges++;
                l.lastFormation = f;
                if (isIn(f, VsMode.OFFENSIVE_FORMATIONS)) p.offensiveFormation++;
                else if (isIn(f, VsMode.DEFENSIVE_FORMATIONS)) p.defensiveFormation++;
            }
        }

        // ---- damage they deal to me (shared between everyone aiming at me)
        if (aiming && !aimers.isEmpty() && (dShield + dHull) > 0) {
            double n = aimers.size();
            double dmg = (dShield + dHull) / n;
            p.dmgToMe += dmg;
            p.shieldDmg += dShield / n;
            p.hullDmg += dHull / n;
            if (dt > 0) p.dpsEma = p.dpsEma == 0 ? dmg / dt : p.dpsEma * 0.97 + (dmg / dt) * 0.03;

            l.recentDamage.addLast(new double[]{now, dmg});
        }
        while (!l.recentDamage.isEmpty() && now - l.recentDamage.peekFirst()[0] > 600) l.recentDamage.removeFirst();
        double window = 0;
        for (double[] d : l.recentDamage) window += d[1];
        if (window > p.maxBurst) p.maxBurst = window;
        if (window >= 0.06 * myMax && now - l.lastBurstAt > 1200) {
            l.lastBurstAt = now;
            p.burstTimes.add(now);
            while (p.burstTimes.size() > 8) p.burstTimes.remove(0);
            p.burstPeriod = regularPeriod(p.burstTimes);
        }
    }

    private static void addCapped(List<Double> list, double v) {
        list.add(v);
        while (list.size() > 12) list.remove(0);
    }

    private static boolean isIn(SelectableItem f, SelectableItem[] set) {
        for (SelectableItem s : set) if (s == f) return true;
        return false;
    }

    private static boolean invulnerable(Ship s) {
        try {
            return s.hasEffect(EntityEffect.ISH) || s.hasEffect(EntityEffect.NPC_ISH);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /** Seconds between volleys when they are regular enough to count on, else 0. */
    private static double regularPeriod(List<Long> times) {
        if (times.size() < 3) return 0;
        double sum = 0;
        for (int i = 1; i < times.size(); i++) sum += times.get(i) - times.get(i - 1);
        double mean = sum / (times.size() - 1), var = 0;
        for (int i = 1; i < times.size(); i++) var += Math.pow((times.get(i) - times.get(i - 1)) - mean, 2);
        double sd = Math.sqrt(var / (times.size() - 1));
        return mean >= 1200 && sd <= 0.35 * mean ? mean / 1000.0 : 0;
    }

    // ------------------------------------------------------------------------------------------------ reading a profile

    private static double avg(List<Double> l) {
        if (l.isEmpty()) return 0;
        double s = 0;
        for (double d : l) s += d;
        return s / l.size();
    }

    public Style styleOf(Profile p) {
        double total = p.votesCharge + p.votesOrbit + p.votesKite + p.votesCamp;
        if (total < 3) return Style.UNKNOWN;
        double max = Math.max(Math.max(p.votesCharge, p.votesOrbit), Math.max(p.votesKite, p.votesCamp));
        if (max == p.votesOrbit) return Style.ORBITER;
        if (max == p.votesKite) return Style.KITER;
        if (max == p.votesCharge) return Style.CHARGER;
        return Style.CAMPER;
    }

    /** Short attacks with pauses in between, repeatedly: they hit and back off. */
    public boolean hitAndRun(Profile p) {
        return p.attackSegs.size() >= 2 && p.pauseSegs.size() >= 2 && avg(p.attackSegs) < 7 && avg(p.pauseSegs) > 3;
    }

    private int medianAttackDistance(Profile p) {
        if (p.attackDistances.isEmpty()) return -1;
        List<Integer> sorted = new ArrayList<>(p.attackDistances);
        java.util.Collections.sort(sorted);
        return sorted.get(sorted.size() / 2);
    }

    /** 0 unknown, 1 low, 2 medium, 3 high, 4 extreme: from what they've done to me (or their size before any damage). */
    public int threat(Profile p, double myMax) {
        double ten = p.dpsEma * 10 / myMax; // share of my health they'd take in 10 s
        int level = ten >= 0.6 ? 4 : ten >= 0.35 ? 3 : ten >= 0.15 ? 2 : p.dmgToMe > 0 ? 1 : 0;
        if (p.maxBurst >= 0.2 * myMax) level = Math.max(level, 3);
        if (level == 0 && p.maxEhp > 1.5 * myMax) level = 2;
        return level;
    }

    private static String[] knownFamily(String shipType) {
        if (shipType == null) return null;
        String t = shipType.toLowerCase(Locale.ROOT);
        for (String[] f : SHIP_FAMILIES) if (t.contains(f[0])) return f;
        return null;
    }

    /** What the pilot should do about this player, from the profile. */
    public List<String> counters(Profile p, double myMax, int mySpeed) {
        List<String> out = new ArrayList<>();
        Style st = styleOf(p);
        if (threat(p, myMax) >= 3) out.add("heavy hitter: defensive formation early, insta-shield on the volley");
        if (p.burstPeriod > 0) out.add(String.format(Locale.ROOT, "volleys every %.1fs: time the shield for them", p.burstPeriod));
        if (hitAndRun(p)) out.add("hit and run: don't chase, stay locked, burst when they come back");
        else if (st == Style.KITER) out.add("backs off while shooting: close in with a boost, slow it with DCR-250");
        else if (st == Style.CAMPER) out.add("stays put: stand your ground, no need to chase");
        else if (st == Style.ORBITER) out.add("circles you: hold range and keep firing");
        if (p.ishCount > 0) out.add("insta-shields: hold fire while it's up (saves ammo)"
                + (p.ishMinInterval > 0 ? String.format(Locale.ROOT, ", about every %.0fs", p.ishMinInterval) : ""));
        if (p.cloakCount > 0) out.add("cloaks: keep EMP-01 ready");
        if (p.healEvents >= 2) out.add("heals itself: burst it down, don't trade slowly");
        if (p.hasPet) out.add("has a pet: target the pet with your pet key to cut the HP link");
        if (p.dmgToMe > 0 && p.shieldDmg / (p.shieldDmg + p.hullDmg + 1) > 0.8) out.add("mostly hits your shield: shield-boosting formation helps");
        if (p.dmgToMe > 0 && p.hullDmg / (p.shieldDmg + p.hullDmg + 1) > 0.6) out.add("goes for your hull: keep repair / heal ready");
        if (p.topSpeed > 0 && mySpeed > 0) {
            if (p.topSpeed > mySpeed * 1.1) out.add("faster than you: you can't run from it");
            else if (p.topSpeed < mySpeed * 0.9) out.add("slower than you: you can leave when you want");
        }
        return out;
    }

    private List<String> describe(Profile p, Ship s, double myMax) {
        List<String> out = new ArrayList<>();
        int level = threat(p, myMax);
        String threatText = level == 0 ? "unknown" : new String[]{"", "low", "medium", "HIGH", "EXTREME"}[level];

        StringBuilder head = new StringBuilder();
        head.append(s.isAttacking(hero) ? "! " : "").append(p.name);
        if (!p.clan.isBlank()) head.append(" [").append(p.clan).append("]");
        if (!p.shipType.isBlank()) head.append("  ").append(p.shipType.replace("ship_", ""));
        head.append("  threat ").append(threatText);
        if (p.dpsEma > 0) head.append(String.format(Locale.ROOT, " (~%d/s%s)", (int) p.dpsEma,
                p.maxBurst > 0 ? ", volley " + (int) p.maxBurst : ""));
        if (p.encounters > 1) head.append("  seen ").append(p.encounters).append("x");
        out.add(head.toString());

        List<String> can = new ArrayList<>();
        if (p.maxEhp > 0) can.add((int) p.maxEhp + " hp");
        if (p.topSpeed > 0) can.add("speed " + (int) p.topSpeed);
        String[] fam = knownFamily(p.shipType);
        if (fam != null) can.add("ship can: " + fam[1]);
        if (p.ishCount > 0) can.add("insta-shield x" + p.ishCount);
        if (p.cloakCount > 0) can.add("cloak x" + p.cloakCount);
        if (p.healEvents > 0) can.add("heals x" + p.healEvents);
        if (p.hasPet) can.add("pet");
        if (p.offensiveFormation + p.defensiveFormation > 0)
            can.add(p.offensiveFormation >= p.defensiveFormation ? "offensive formation" : "defensive formation");
        if (!can.isEmpty()) out.add("   " + String.join(", ", can));

        Style st = styleOf(p);
        List<String> how = new ArrayList<>();
        if (st != Style.UNKNOWN) how.add(st.name().toLowerCase(Locale.ROOT));
        int d = medianAttackDistance(p);
        if (d > 0) how.add("shoots from ~" + d);
        if (hitAndRun(p)) how.add(String.format(Locale.ROOT, "hit and run (%.0fs on, %.0fs off)", avg(p.attackSegs), avg(p.pauseSegs)));
        if (p.burstPeriod > 0) how.add(String.format(Locale.ROOT, "volley every %.1fs", p.burstPeriod));
        if (!how.isEmpty()) out.add("   " + String.join(", ", how));

        List<String> counters = counters(p, myMax, hero.getSpeed());
        for (int i = 0; i < Math.min(2, counters.size()); i++) out.add("   -> " + counters.get(i));
        return out;
    }
}
