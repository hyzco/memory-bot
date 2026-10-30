package com.github.manolo8.darkbot.modules.vs;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import eu.darkbot.api.game.enums.EntityEffect;
import eu.darkbot.api.game.items.Item;
import eu.darkbot.api.game.items.ItemUseResult;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.managers.HeroItemsAPI;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The "pilot": thinks about the fight the way a good player would and either does it (AUTO) or tells you / protects
 * you while you fly (ASSISTED).
 * <p>
 * What it measures, continuously: how fast I'm losing health (and which hits come in bursts and how often), how fast
 * the target loses health, who is aiming at me, shields, item cooldowns and quantities. From those it runs a quick
 * simulation of the fight for "do nothing" and for each action it could take now (insta-shield, heal, formation...),
 * and picks the action only if it clearly improves the odds of winning / surviving by more than it costs. Nothing is
 * spent without a measured reason, and shooting is paused while the target is invulnerable so ammo isn't wasted.
 */
public class PilotBrain {

    public enum Kind {ISH, HEAL, DEF_FORMATION, OFF_FORMATION, EMP, SMB, OFFENSE}

    /** One thing the pilot wants to do right now. */
    public static final class Suggestion {
        public final Kind kind;
        final SelectableItem item;
        public final String text, reason;
        final double gain;

        Suggestion(Kind kind, SelectableItem item, String text, String reason, double gain) {
            this.kind = kind;
            this.item = item;
            this.text = text;
            this.reason = reason;
            this.gain = gain;
        }
    }

    private static final int RUNS = 220;
    private static final double MIN_GAIN = 0.06;
    private static final double ISH_SECONDS = 3.0;
    private static final long DECISION_EVERY_MS = 500, ACTION_GAP_MS = 400, FORMATION_GAP_MS = 8000;

    private static final class Track {
        final ArrayDeque<double[]> ehp = new ArrayDeque<>(); // {t, absolute ehp}
        long lastAggression;
        // what this enemy has shown it does
        double lastShield = -1, lastHull = -1;
        int healEvents;
        long lastHealAt, runnerUntil, ignoredUntil;
        boolean sawIsh;
    }

    private final VsMode vs;
    private final HeroManager hero;
    private final Config.General.HakClanTutucu cfg;
    private final HeroItemsAPI items;
    private final FightSim sim = new FightSim();
    private final PilotFlight flight;
    private FightSim.Result lastOutlook;
    private long lastRocket;

    /** Damage-boosting / debuffing abilities worth using in a fight (the ones the ship actually has are used). */
    static final SelectableItem[] OFFENSE = {
            SelectableItem.Ability.TARTARUS_PLUS_RAPID_FIRE, SelectableItem.Ability.TARTARUS_RAPID_FIRE,
            SelectableItem.Ability.SPEARHEAD_PLUS_NEUTRALIZING_MARKER, SelectableItem.Ability.SPEARHEAD_TARGET_MARKER,
            SelectableItem.Ability.BERSERKER_BSK, SelectableItem.Ability.BERSERKER_SHL, SelectableItem.Ability.BERSERKER_RVG,
            SelectableItem.Ability.HECATE_PLUS_PARTICLE_BEAM_PLUS, SelectableItem.Ability.HECATE_PARTICLE_BEAM,
            SelectableItem.Ability.SOLARIS_PLUS_INCINERATE_PLUS, SelectableItem.Ability.SOLARIS_INC,
            SelectableItem.Ability.VENOM, SelectableItem.Ability.DIMINISHER, SelectableItem.Ability.LIGHTNING,
            SelectableItem.Ability.KERES_SPR, SelectableItem.Ability.KERES_SLE,
            SelectableItem.Ability.RETIARUS_SPC, SelectableItem.Ability.RETIARUS_CHS,
            SelectableItem.Ability.DISRUPTOR_SHIELD_DISARRAY, SelectableItem.Ability.DISRUPTOR_DDOL,
            SelectableItem.Ability.TEMPEST_VOLT_DISCHARGE, SelectableItem.Ability.GOLIATH_X_FROZEN_CLAW,
            SelectableItem.Ability.ORCUS_ASSIMILATE, SelectableItem.Ability.PUSAT_PLUS_SPEED_SAP,
            SelectableItem.Tech.PRECISION_TARGETER, SelectableItem.Tech.ENERGY_LEECH, SelectableItem.Tech.CHAIN_IMPULSE,
            SelectableItem.Tech.EXPLOSIVE_CHARGING_BLOB, SelectableItem.Tech.FROST_FIELD,
    };
    /** Rockets that do damage (not the slowing / utility ones), best first. */
    static final SelectableItem.Rocket[] DAMAGE_ROCKETS = {
            SelectableItem.Rocket.PLT_3030, SelectableItem.Rocket.PLT_2026, SelectableItem.Rocket.PLT_2021,
            SelectableItem.Rocket.R_310,
    };

    private final ArrayDeque<double[]> heroSamples = new ArrayDeque<>(); // {t, ehp}
    private final Map<Ship, Track> tracks = new IdentityHashMap<>();
    private final ArrayDeque<Long> bursts = new ArrayDeque<>();
    private final ArrayDeque<String> actionLog = new ArrayDeque<>();

    private long lastBurstAt, lastDecision, lastAction, lastFormationSwitch, ishUntil, lastDamageAt;
    private double dpsIn, dpsOut, observedHeal;
    private boolean holdFire;
    private volatile Suggestion suggestion;
    private volatile List<String> lines = List.of();

    PilotBrain(VsMode vs, Main main) {
        this.vs = vs;
        this.hero = main.hero;
        this.cfg = main.config.GENERAL.HAK_CLAN_TUTUCU;
        this.items = main.pluginAPI.requireAPI(HeroItemsAPI.class);
        this.flight = new PilotFlight(main, items, vs.profiler());
    }

    boolean active() {
        return cfg.VS_PILOT != Config.General.HakClanTutucu.VsPilot.CLASSIC;
    }

    public List<String> lines() {
        return lines;
    }

    public Suggestion suggestion() {
        return suggestion;
    }

    /** True while shooting would only burn ammo (the target is invulnerable). */
    public boolean holdFire() {
        return active() && cfg.VS_SAVE_AMMO && holdFire;
    }

    // ------------------------------------------------------------------------------------------------ per tick

    void tick(List<Ship> enemies, Ship current, boolean engaged) {
        long now = System.currentTimeMillis();
        sampleHero(now);
        sampleEnemies(enemies, current, now);
        detectBursts(now);
        dpsIn = damageRate(heroSamples, now, 5000);
        Track tt = current == null ? null : tracks.get(current);
        dpsOut = tt == null ? 0 : damageRate(tt.ehp, now, 5000);
        holdFire = current != null && invulnerable(current);

        if (cfg.VS_PILOT == Config.General.HakClanTutucu.VsPilot.AUTO) {
            flight.tick(enemies, current, lastOutlook, now);
            rockets(current, engaged, now);
            trackChase(current, now);
            learnAmmo(current, engaged, now);
        }
        trackFight(enemies, now);

        if (now - lastDecision >= DECISION_EVERY_MS) {
            lastDecision = now;
            try {
                decide(enemies, current, engaged, now);
            } catch (RuntimeException e) {
                lines = List.of("Pilot: analysing...");
            }
        }
    }

    private boolean invulnerable(Ship s) {
        try {
            return s.hasEffect(EntityEffect.ISH) || s.hasEffect(EntityEffect.NPC_ISH);
        } catch (RuntimeException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------------------------------------ measuring

    private double myMax() {
        return Math.max(1, hero.health.maxHp + hero.health.maxShield);
    }

    private static double maxEhp(Ship s, double fallback) {
        int max = s.health.maxHp + s.health.maxShield;
        return max > 0 ? max : fallback;
    }

    private static double ehp(Ship s, double fallbackMax) {
        double max = maxEhp(s, fallbackMax);
        int cur = s.health.hp + s.health.shield;
        return s.health.maxHp + s.health.maxShield > 0 ? cur : max * vsLife(s);
    }

    private static double vsLife(Ship s) {
        return VsMode.lifeFraction(s);
    }

    private void sampleHero(long now) {
        double ehp = vs.heroEhp();
        double[] last = heroSamples.peekLast();
        if (last != null && ehp < last[1]) lastDamageAt = now;
        heroSamples.addLast(new double[]{now, ehp});
        while (!heroSamples.isEmpty() && now - heroSamples.peekFirst()[0] > 10_000) heroSamples.removeFirst();
        // biggest recent rise = what a heal gave me (shield regeneration is slower than this, so it doesn't get in the way)
        double rise = 0;
        double[] prev = null;
        for (double[] s : heroSamples) {
            if (prev != null && now - s[0] < 4000 && s[1] - prev[1] > 0) rise += s[1] - prev[1];
            prev = s;
        }
        if (rise > observedHeal * 0.5) observedHeal = Math.max(observedHeal * 0.98, rise);
    }

    private void sampleEnemies(List<Ship> enemies, Ship current, long now) {
        tracks.keySet().removeIf(s -> !s.isValid());
        List<Ship> all = new ArrayList<>(enemies);
        if (current != null && !all.contains(current)) all.add(current);
        for (Ship s : all) {
            Track t = tracks.computeIfAbsent(s, k -> new Track());
            t.ehp.addLast(new double[]{now, ehp(s, myMax())});
            while (!t.ehp.isEmpty() && now - t.ehp.peekFirst()[0] > 10_000) t.ehp.removeFirst();
            if (s.isAttacking(hero)) t.lastAggression = now;
            profile(s, t, now);
        }
    }

    /** What the enemy has shown: healing itself, insta-shielding. (Running is detected while chasing, see tick.) */
    private void profile(Ship s, Track t, long now) {
        double max = maxEhp(s, myMax());
        if (t.ehp.size() >= 2) {
            double[] last = t.ehp.peekLast();
            double[] prev = null;
            for (double[] x : t.ehp) {
                if (x == last) break;
                prev = x;
            }
            // a jump up of 4% of its health in one step is a heal, not natural shield regeneration
            if (prev != null && last[1] - prev[1] > 0.04 * max && now - t.lastHealAt > 1000) {
                t.healEvents++;
                t.lastHealAt = now;
            }
        }
        if (t.healEvents > 0 && now - t.lastHealAt > 20_000) t.healEvents = 0;
        if (invulnerable(s)) t.sawIsh = true;
    }

    private boolean isHealer(Ship s) {
        Track t = tracks.get(s);
        if (t != null && t.healEvents >= 2) return true;
        EnemyProfiler.Profile pr = vs.profiler().profileOf(s);
        return pr != null && pr.healEvents >= 2; // known from earlier meetings
    }

    public boolean ignored(Ship s) {
        Track t = tracks.get(s);
        return t != null && t.ignoredUntil > System.currentTimeMillis();
    }

    private boolean isRunner(Ship s) {
        Track t = tracks.get(s);
        return t != null && t.runnerUntil > System.currentTimeMillis();
    }

    /** Average rate of loss (units per second) in a sample window, counting only drops (heals/regen don't cancel damage). */
    private static double damageRate(ArrayDeque<double[]> samples, long now, long windowMs) {
        double lost = 0, first = -1;
        double[] prev = null;
        for (double[] s : samples) {
            if (now - s[0] > windowMs) {
                prev = s;
                continue;
            }
            if (first < 0) first = prev != null ? prev[0] : s[0];
            if (prev != null && s[1] < prev[1]) lost += prev[1] - s[1];
            prev = s;
        }
        if (first < 0) return 0;
        double span = Math.max(1500, now - first); // short spans make the rate jumpy
        return lost / (span / 1000.0);
    }

    /** A burst = a lot of damage in a very short time (rocket volley, skill). Remember when, to predict the next one. */
    private void detectBursts(long now) {
        double window = 0;
        double[] prev = null;
        for (double[] s : heroSamples) {
            if (now - s[0] <= 600) {
                if (prev != null && s[1] < prev[1]) window += prev[1] - s[1];
            }
            prev = s;
        }
        if (window >= 0.06 * myMax() && now - lastBurstAt > 1200) {
            lastBurstAt = now;
            bursts.addLast(now);
            while (bursts.size() > 6 || (!bursts.isEmpty() && now - bursts.peekFirst() > 45_000)) bursts.removeFirst();
        }
    }

    /** {period seconds, seconds until the next burst} when the bursts are regular enough to predict, else null. */
    private double[] burstPattern(long now) {
        if (bursts.size() < 3) return null;
        List<Long> b = new ArrayList<>(bursts);
        double sum = 0;
        for (int i = 1; i < b.size(); i++) sum += b.get(i) - b.get(i - 1);
        double mean = sum / (b.size() - 1);
        double var = 0;
        for (int i = 1; i < b.size(); i++) var += Math.pow((b.get(i) - b.get(i - 1)) - mean, 2);
        double sd = Math.sqrt(var / (b.size() - 1));
        if (mean < 1200 || sd > 0.35 * mean) return null; // too irregular to rely on
        double next = b.get(b.size() - 1) + mean - now;
        while (next < -mean) next += mean;
        return new double[]{mean / 1000.0, next / 1000.0};
    }

    // ------------------------------------------------------------------------------------------------ the decision

    private boolean ready(SelectableItem it) {
        Item i = items.getItem(it).orElse(null);
        return i != null && i.isAvailable() && i.isUsable() && i.isReady();
    }

    private boolean aiming(Ship s) {
        return s.isAttacking(hero);
    }

    private FightSim.Params params(List<Ship> enemies, Ship current, long now, double[] pattern) {
        List<Ship> foes = new ArrayList<>();
        if (current != null) foes.add(current);
        for (Ship s : enemies) if (!foes.contains(s) && (aiming(s) || foes.size() < 2)) foes.add(s);
        if (foes.size() > 4) foes = foes.subList(0, 4);
        if (foes.isEmpty()) return null;

        double myMax = myMax();
        FightSim.Params p = new FightSim.Params();
        p.myMax = myMax;
        p.myEhp = Math.max(1, vs.heroEhp());
        int n = foes.size();
        p.foeEhp = new double[n];
        p.foeDps = new double[n];

        double[] weight = new double[n];
        double wsum = 0;
        int aimers = 0;
        for (int i = 0; i < n; i++) {
            Ship s = foes.get(i);
            p.foeEhp[i] = Math.max(1, ehp(s, myMax));
            weight[i] = (aiming(s) ? 1.0 : 0.12) * (1 + vs.threatOf(s) / Math.max(1, myMax * 0.05));
            wsum += weight[i];
            if (aiming(s)) aimers++;
        }
        double total = dpsIn;
        if (total <= 0 && aimers > 0) total = myMax / 45.0 * aimers; // nobody has hurt me yet: assume a slow kill
        for (int i = 0; i < n; i++) p.foeDps[i] = wsum <= 0 ? 0 : total * weight[i] / wsum;

        double out = dpsOut;
        if (out <= 0) out = p.foeEhp[0] / 25.0; // not measured yet: assume a 25 s kill
        p.dpsOut = Math.max(out, p.foeEhp[0] / 400.0);

        if (pattern != null) {
            p.burstShare = 0.5;
            p.burstPeriod = pattern[0];
            p.nextBurstIn = Math.max(0, pattern[1]);
        }
        return p;
    }

    private SelectableItem firstReadyDefence() {
        for (SelectableItem d : VsMode.DEFENCE) if (ready(d)) return d;
        return null;
    }

    private boolean isDefensive(SelectableItem f) {
        for (SelectableItem d : VsMode.DEFENSIVE_FORMATIONS) if (d == f) return true;
        return false;
    }

    private boolean isOffensive(SelectableItem f) {
        for (SelectableItem d : VsMode.OFFENSIVE_FORMATIONS) if (d == f) return true;
        return false;
    }

    private void decide(List<Ship> enemies, Ship current, boolean engaged, long now) {
        double[] pattern = burstPattern(now);
        FightSim.Params p = params(enemies, current, now, pattern);
        if (p == null) {
            suggestion = null;
            lines = List.of(cfg.VS_PILOT == Config.General.HakClanTutucu.VsPilot.AUTO
                    ? "Pilot: AI flying - no enemy in range" : "Pilot: assisting - no enemy in range");
            return;
        }

        FightSim.Action none = new FightSim.Action("none");
        FightSim.Result r0 = sim.run(p, none, RUNS);
        lastOutlook = r0;
        double myPct = p.myEhp / p.myMax * 100;
        boolean hurtLately = now - lastDamageAt < 3000 || dpsIn > 0.01 * p.myMax;
        boolean burstSoon = pattern != null && pattern[1] < 0.8;
        boolean ishActive = now < ishUntil || safeEffect(hero, EntityEffect.ISH);
        // about to win with no real danger: nothing defensive is worth spending
        boolean aboutToWin = !Double.isNaN(r0.avgSecondsToWin) && r0.avgSecondsToWin < 4 && r0.survive > 0.9;

        List<Suggestion> options = new ArrayList<>();

        // a known heavy hitter is on me: get defensive before the damage lands, not after
        boolean knownDanger = false;
        for (Ship s : enemies) {
            if (!aiming(s)) continue;
            EnemyProfiler.Profile pr = vs.profiler().profileOf(s);
            if (pr != null && vs.profiler().threat(pr, p.myMax) >= 3) knownDanger = true;
        }

        // --- insta-shield: only when damage is coming (measured or predicted) and the sim says it changes the outcome
        if (!ishActive && !aboutToWin && ready(SelectableItem.Special.ISH_01) && (hurtLately || burstSoon)) {
            FightSim.Action a = new FightSim.Action("ISH");
            a.invulnSeconds = ISH_SECONDS;
            a.cost = 0.12;
            FightSim.Result r = sim.run(p, a, RUNS);
            double gain = r.utility() - r0.utility() - a.cost;
            if (gain > MIN_GAIN && (r0.survive < 0.92 || myPct < 70 || burstSoon)) {
                String why = burstSoon ? "volley expected in " + String.format(Locale.ROOT, "%.1f", pattern[1]) + "s, "
                        : "";
                options.add(new Suggestion(Kind.ISH, SelectableItem.Special.ISH_01, "Insta-Shield now",
                        why + "survive " + pct(r0.survive) + " -> " + pct(r.survive), gain + (r0.survive < 0.6 ? 0.2 : 0)));
            }
        }

        // --- heal / defence ability: only when actually hurt
        SelectableItem heal = myPct < 80 && hurtLately && !aboutToWin ? firstReadyDefence() : null;
        if (heal != null) {
            FightSim.Action a = new FightSim.Action("HEAL");
            a.healAbs = Math.max(0.12 * p.myMax, observedHeal);
            a.healSeconds = 5;
            a.cost = 0.08;
            FightSim.Result r = sim.run(p, a, RUNS);
            double gain = r.utility() - r0.utility() - a.cost;
            if (gain > MIN_GAIN) {
                options.add(new Suggestion(Kind.HEAL, heal, VsMode.pretty(heal) + " now",
                        "life " + (int) myPct + "%, survive " + pct(r0.survive) + " -> " + pct(r.survive),
                        gain + (r0.survive < 0.6 ? 0.2 : 0)));
            }
        }

        // --- formation: defensive when outnumbered / losing, offensive when comfortably winning
        SelectableItem current_f = hero.getFormation();
        boolean formationFree = now - lastFormationSwitch > FORMATION_GAP_MS;
        int aimers = 0;
        for (Ship s : enemies) if (aiming(s)) aimers++;
        if (formationFree && (aimers >= 2 || r0.win < 0.5 || knownDanger) && (hurtLately || knownDanger)) {
            SelectableItem def = vs.bestDefensiveFormation();
            if (def != null && !isDefensive(current_f) && def != current_f) {
                FightSim.Action a = new FightSim.Action("DEF");
                a.dmgInMul = 0.85;
                a.dmgOutMul = 0.92;
                a.cost = 0.03;
                FightSim.Result r = sim.run(p, a, RUNS);
                double gain = r.utility() - r0.utility() - a.cost;
                if (gain > MIN_GAIN * 0.5 || knownDanger)
                    options.add(new Suggestion(Kind.DEF_FORMATION, def, VsMode.pretty(def) + " formation",
                            knownDanger && !hurtLately ? "profile: this player hits hard"
                                    : "outnumbered/losing: survive " + pct(r0.survive) + " -> " + pct(r.survive),
                            gain + (knownDanger ? 0.1 : 0)));
            }
        } else if (formationFree && r0.win > 0.75 && r0.survive > 0.9 && !enemies.isEmpty()) {
            SelectableItem off = vs.firstOffensiveFormation();
            if (off != null && !isOffensive(current_f) && off != current_f) {
                FightSim.Action a = new FightSim.Action("OFF");
                a.dmgOutMul = 1.12;
                a.dmgInMul = 1.08;
                a.cost = 0.03;
                FightSim.Result r = sim.run(p, a, RUNS);
                double gain = r.utility() - r0.utility() - a.cost;
                if (gain > 0.01)
                    options.add(new Suggestion(Kind.OFF_FORMATION, off, VsMode.pretty(off) + " formation",
                            "comfortably winning: finish faster", gain));
            }
        }

        // --- offensive toolkit: damage boosts / debuffs the ship has, when the fight is on and not trivial
        if (current != null && !holdFire && distance(current) <= cfg.VS_FIGHT_RANGE + 200 && r0.win < 0.97 && (engaged || aimersNow(enemies) > 0)) {
            SelectableItem off = firstReady(OFFENSE);
            if (off != null) {
                FightSim.Action a = new FightSim.Action("OFFENSE");
                a.dmgOutMul = 1.15;
                a.outSeconds = 6;
                a.cost = 0.03;
                FightSim.Result r = sim.run(p, a, RUNS);
                double gain = r.utility() - r0.utility() - a.cost;
                if (gain > 0.015)
                    options.add(new Suggestion(Kind.OFFENSE, off, VsMode.pretty(off) + " now",
                            "boost damage: win " + pct(r0.win) + " -> " + pct(r.win), gain));
            }
        }

        // --- utility items with a concrete trigger (not worth simulating)
        Ship target = current;
        boolean cloaked = false;
        for (Ship s : enemies) if (s.isInvisible() && distance(s) < 900) cloaked = true;
        boolean enemyIsh = target != null && invulnerable(target);
        if (ready(SelectableItem.Special.EMP_01)) {
            if (cloaked && cfg.VS_EMP_ON_CLOAK)
                options.add(new Suggestion(Kind.EMP, SelectableItem.Special.EMP_01, "EMP-01 now", "an enemy is cloaked close to you", 0.25));
            else if (enemyIsh && cfg.VS_EMP_ON_ISH)
                options.add(new Suggestion(Kind.EMP, SelectableItem.Special.EMP_01, "EMP-01 now", "target is insta-shielded", 0.2));
        }
        if (ready(SelectableItem.Special.SMB_01)) {
            int near = 0;
            double shield = 0;
            for (Ship s : enemies) {
                if (distance(s) <= 650) {
                    near++;
                    shield += s.health.maxShield > 0 ? (double) s.health.shield / s.health.maxShield : 0;
                }
            }
            if (near >= 2 && shield / near > 0.4)
                options.add(new Suggestion(Kind.SMB, SelectableItem.Special.SMB_01, "SMB-01 now",
                        near + " enemies bunched up with shields", 0.15));
        }

        Suggestion best = null;
        for (Suggestion s : options) if (best == null || s.gain > best.gain) best = s;
        suggestion = best;

        lines = describe(r0, p, pattern, best, current, myPct);

        // --- act
        boolean auto = cfg.VS_PILOT == Config.General.HakClanTutucu.VsPilot.AUTO;
        boolean safety = cfg.VS_PILOT == Config.General.HakClanTutucu.VsPilot.ASSISTED && cfg.VS_ASSIST_SAFETY
                && best != null && (best.kind == Kind.ISH || best.kind == Kind.HEAL) && r0.survive < 0.35;
        if (best != null && (auto || safety) && now - lastAction > ACTION_GAP_MS) execute(best, now, safety ? "safety net" : "pilot");
    }

    private int aimersNow(List<Ship> enemies) {
        int n = 0;
        for (Ship s : enemies) if (aiming(s)) n++;
        return n;
    }

    private SelectableItem firstReady(SelectableItem[] list) {
        for (SelectableItem it : list) if (ready(it)) return it;
        return null;
    }

    /** Fire rockets while the target is in rocket range: select a damage rocket if none is, then launch on a fixed beat. */
    private void rockets(Ship target, boolean engaged, long now) {
        if (!cfg.VS_ROCKETS || target == null || !engaged || holdFire || now - lastRocket < 2600) return;
        if (distance(target) > 650) return;
        try {
            // a runner we're catching up with: slow it with a decelerator rocket (then back to damage rockets)
            if (slowUntil > now && hero.getRocket() == SelectableItem.Rocket.DCR_250) {
                hero.launchRocket();
                slowUntil = 0;
                lastRocket = now;
                fightActions++;
                System.out.println("VS pilot: decelerator fired at a runner");
                return;
            }
            if (isRunner(target) && now - lastSlow > 12_000) {
                Item dcr = items.getItem(SelectableItem.Rocket.DCR_250).orElse(null);
                if (dcr != null && dcr.isAvailable() && dcr.isUsable() && dcr.getQuantity() > 0 && items.useItem(SelectableItem.Rocket.DCR_250).isSuccessful()) {
                    lastSlow = now;
                    slowUntil = now + 2500;
                    lastRocket = now - 2000; // launch it in ~0.6 s, once the selection has registered
                    return;
                }
            }
            SelectableItem.Rocket selected = hero.getRocket();
            boolean damaging = false;
            Item sel = selected == null ? null : items.getItem(selected).orElse(null);
            if (selected != null) for (SelectableItem.Rocket r : DAMAGE_ROCKETS) if (r == selected) damaging = true;
            if (!damaging || sel == null || sel.getQuantity() <= 0) {
                for (SelectableItem.Rocket r : DAMAGE_ROCKETS) {
                    Item i = items.getItem(r).orElse(null);
                    if (i != null && i.isAvailable() && i.isUsable() && i.getQuantity() > 0) {
                        items.useItem(r);
                        lastRocket = now - 1800; // give the selection a moment before the first launch
                        return;
                    }
                }
                return; // no damage rocket to fire
            }
            hero.launchRocket();
            lastRocket = now;
        } catch (RuntimeException ignored) {
            // items not readable right now
        }
    }

    private static String pct(double v) {
        return (int) Math.round(v * 100) + "%";
    }

    private double distance(Ship s) {
        return Math.hypot(s.locationInfo.now.x - hero.locationInfo.now.x, s.locationInfo.now.y - hero.locationInfo.now.y);
    }

    private static boolean safeEffect(Ship s, EntityEffect e) {
        try {
            return s.hasEffect(e);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private List<String> describe(FightSim.Result r0, FightSim.Params p, double[] pattern, Suggestion best, Ship current, double myPct) {
        List<String> out = new ArrayList<>();
        String mode = cfg.VS_PILOT == Config.General.HakClanTutucu.VsPilot.AUTO ? "AI flying" : "assisting you";
        out.add("Pilot (" + mode + "): win " + pct(r0.win) + "  survive " + pct(r0.survive));
        String rates = "You lose " + (int) dpsIn + "/s, they lose " + (int) dpsOut + "/s";
        if (!Double.isNaN(r0.avgSecondsToWin)) rates += "  -  win in ~" + (int) Math.round(r0.avgSecondsToWin) + "s";
        if (!Double.isNaN(r0.avgSecondsToDie)) rates += "  -  you'd last ~" + (int) Math.round(r0.avgSecondsToDie) + "s";
        out.add(rates + (dpsOut <= 0 ? "  (estimating)" : ""));
        if (pattern != null)
            out.add("Volleys every " + String.format(Locale.ROOT, "%.1f", pattern[0]) + "s, next in "
                    + String.format(Locale.ROOT, "%.1f", Math.max(0, pattern[1])) + "s");
        if (holdFire() && current != null) out.add("Holding fire: target is invulnerable (saving ammo)");
        if (!flight.status().isEmpty()) out.add("Flying: " + flight.status());
        if (current != null) {
            List<String> traits = new ArrayList<>();
            if (isRunner(current)) traits.add("runs away");
            if (isHealer(current)) traits.add("heals itself");
            Track t = tracks.get(current);
            if (t != null && t.sawIsh) traits.add("uses insta-shield");
            if (!traits.isEmpty()) out.add("Target " + String.join(", ", traits));
        }
        String ammoLine = ammoSummary();
        if (ammoLine != null) out.add(ammoLine);
        if (r0.win < 0.2 && r0.survive < 0.2) out.add("Outlook is bad: nothing in your kit turns this fight around - consider leaving");
        if (best != null) out.add("Next: " + best.text + "  (" + best.reason + ")");
        for (String l : actionLog) out.add("Done: " + l);
        return out;
    }

    // ------------------------------------------------------------------------------------------------ acting

    private void execute(Suggestion s, long now, String who) {
        ItemUseResult r = items.useItem(s.item);
        lastAction = now;
        if (r.isSuccessful()) {
            if (s.kind == Kind.ISH) ishUntil = now + (long) (ISH_SECONDS * 1000);
            if (s.kind == Kind.DEF_FORMATION || s.kind == Kind.OFF_FORMATION) lastFormationSwitch = now;
        }
        String line = String.format(Locale.ROOT, "%tT", new java.util.Date(now)) + " " + s.text + " [" + who + "] -> " + r;
        fightActions++;
        actionLog.addFirst(line);
        while (actionLog.size() > 3) actionLog.removeLast();
        System.out.println("VS pilot: " + line + " (" + s.reason + ")");
    }

    /** The "do it" button / hotkey of the assisted mode: do what the pilot currently recommends. */
    public String doSuggested() {
        Suggestion s = suggestion;
        if (s == null) return "Nothing to do right now";
        execute(s, System.currentTimeMillis(), "you");
        return s.text;
    }

    // ------------------------------------------------------------------------------------------------ re-engaging

    /** An enemy that attacked me a moment ago, isn't aiming at me now, and is still around: a returning attacker. */
    public Ship returningAttacker(List<Ship> enemies, long withinMs) {
        long now = System.currentTimeMillis();
        Ship best = null;
        for (Ship s : enemies) {
            Track t = tracks.get(s);
            if (t == null || t.lastAggression == 0 || aiming(s)) continue;
            if (now - t.lastAggression > withinMs) continue;
            if (best == null || distance(s) < distance(best)) best = s;
        }
        return best;
    }

    /** Would fighting this enemy go well? Uses the same simulation with default assumptions (no measurements yet). */
    public boolean worthEngaging(Ship foe, List<Ship> enemies) {
        FightSim.Params p = new FightSim.Params();
        p.myMax = myMax();
        p.myEhp = Math.max(1, vs.heroEhp());
        List<Ship> foes = new ArrayList<>();
        foes.add(foe);
        for (Ship s : enemies) if (s != foe && aiming(s) && foes.size() < 4) foes.add(s);
        int n = foes.size();
        p.foeEhp = new double[n];
        p.foeDps = new double[n];
        for (int i = 0; i < n; i++) {
            p.foeEhp[i] = Math.max(1, ehp(foes.get(i), p.myMax));
            p.foeDps[i] = p.myMax / 45.0;
        }
        p.dpsOut = Math.max(dpsOut, p.foeEhp[0] / 25.0);
        FightSim.Result r = sim.run(p, new FightSim.Action("engage"), RUNS);
        return r.win >= 0.35 || r.survive >= 0.6;
    }

    // ------------------------------------------------------------------------------------------------ chasing

    private long chaseStart;
    private double chaseStartDist;
    private Ship chaseTarget;
    private long lastSlow, slowUntil;

    /**
     * Chasing something that keeps its distance is a trap (and burns boosts): if after several seconds of closing in the
     * gap hasn't shrunk, call the target a runner, and give up on it for a while so another target can be taken.
     */
    private void trackChase(Ship current, long now) {
        boolean chasing = current != null && "Closing in".equals(flight.status());
        if (!chasing) {
            chaseStart = 0;
            chaseTarget = null;
            return;
        }
        double d = distance(current);
        if (chaseTarget != current || chaseStart == 0) {
            chaseTarget = current;
            chaseStart = now;
            chaseStartDist = d;
            return;
        }
        if (now - chaseStart > 8000) {
            Track t = tracks.get(current);
            if (t != null && d > chaseStartDist - 120) {
                t.runnerUntil = now + 30_000;
                if (cfg.VS_ABANDON_RUNNERS) t.ignoredUntil = now + 25_000;
                System.out.println("VS pilot: target keeps its distance (" + (int) d + ") - treating it as a runner");
            }
            chaseStart = now;
            chaseStartDist = d;
        }
    }

    /** True when the current target should be let go (a runner we can't catch) in favour of another one. */
    public boolean shouldAbandon(Ship current) {
        return cfg.VS_PILOT == Config.General.HakClanTutucu.VsPilot.AUTO && cfg.VS_ABANDON_RUNNERS && current != null && ignored(current);
    }

    // ------------------------------------------------------------------------------------------------ kill order

    /**
     * Who to shoot first. Minimising the damage I take means killing in order of (damage they deal) / (time to kill them):
     * big threats that die quickly come first. Healers are worth killing early, ships that can't be damaged right now
     * (insta-shield) or that run away are not worth shooting at the moment.
     */
    public Ship killOrderPick(List<Ship> enemies, Ship current, long lockedForMs) {
        if (enemies.isEmpty()) return null;
        if (enemies.size() == 1) return enemies.get(0);

        double myDps = dpsOut > 0 ? dpsOut : 0;
        double maxThreat = 0;
        for (Ship s : enemies) maxThreat = Math.max(maxThreat, vs.threatOf(s));

        Ship best = null;
        double bestScore = -1, curScore = -1;
        for (Ship s : enemies) {
            double ehp = Math.max(1, ehp(s, myMax()));
            double dps = vs.threatOf(s) / 5.8 + (aiming(s) ? 0.05 * dpsIn : 0.0) + 1; // +1 keeps ties orderable
            double ttk = ehp / Math.max(myDps, maxEhp(s, myMax()) / 25.0);
            double score = dps / Math.max(0.5, ttk);
            if (invulnerable(s)) score *= 0.1;
            if (isHealer(s)) score *= 1.4;
            if (isRunner(s) || ignored(s)) score *= 0.3;
            if (distance(s) > cfg.VS_FIGHT_RANGE + 250) score *= 0.7;
            if (s == current) curScore = score;
            if (score > bestScore) {
                bestScore = score;
                best = s;
            }
        }
        if (current == null || !current.isValid() || curScore < 0) return best;
        boolean currentUseless = invulnerable(current) || ignored(current);
        if (best == current) return current;
        if (!currentUseless && lockedForMs < cfg.VS_SWITCH_DELAY) return current;
        return currentUseless || bestScore > curScore * 1.4 ? best : current;
    }

    // ------------------------------------------------------------------------------------------------ fights

    private long fightStart, lastEnemySeen;
    private double fightStartLife;
    private int fightActions;

    /** One summary line per fight in the log: how it went, so settings can be tuned from real results. */
    private void trackFight(List<Ship> enemies, long now) {
        boolean contact = !enemies.isEmpty() && (aimersNow(enemies) > 0 || now - lastDamageAt < 3000);
        if (contact) lastEnemySeen = now;
        if (fightStart == 0 && contact) {
            fightStart = now;
            fightStartLife = vs.heroEhp() / myMax();
            fightActions = 0;
        } else if (fightStart != 0 && now - lastEnemySeen > 6000) {
            System.out.println(String.format(Locale.ROOT, "VS pilot: fight over - %ds, life %d%% -> %d%%, %d actions, outlook now %s",
                    (now - fightStart) / 1000, (int) (fightStartLife * 100), (int) (vs.heroEhp() / myMax() * 100), fightActions,
                    lastOutlook == null ? "-" : "win " + pct(lastOutlook.win)));
            fightStart = 0;
        }
    }

    // ------------------------------------------------------------------------------------------------ ammo

    // --- learning which ammo hurts this enemy most, separately while its shield is up and once it is on hull

    private static final class AmmoStat {
        double shieldDmg, shieldSec, hullDmg, hullSec;

        double dps(boolean shieldPhase) {
            double sec = shieldPhase ? shieldSec : hullSec;
            return sec <= 0 ? 0 : (shieldPhase ? shieldDmg : hullDmg) / sec;
        }

        double seconds(boolean shieldPhase) {
            return shieldPhase ? shieldSec : hullSec;
        }
    }

    private static final SelectableItem.Laser[] LEARN_ORDER = {
            SelectableItem.Laser.UCB_100, SelectableItem.Laser.RSB_75, SelectableItem.Laser.SAB_50, SelectableItem.Laser.MCB_50};
    private final Map<SelectableItem.Laser, AmmoStat> ammoStats = new java.util.EnumMap<>(SelectableItem.Laser.class);
    private SelectableItem.Laser learnedChoice;
    private long learnedSince;
    private long lastLearnTick;

    private void learnAmmo(Ship target, boolean engaged, long now) {
        double dt = Math.min(200, now - lastLearnTick);
        lastLearnTick = now;
        if (!cfg.VS_AMMO_LEARN || target == null || !engaged || holdFire || dt <= 0) return;
        if (distance(target) > cfg.VS_FIGHT_RANGE + 120 || now - lastRocket < 1500) return; // rockets would muddy the numbers
        SelectableItem.Laser using = hero.getLaser();
        Track t = tracks.get(target);
        if (using == null || t == null) return;

        double shield = target.health.shield, hull = target.health.hp;
        if (t.lastShield >= 0) {
            AmmoStat st = ammoStats.computeIfAbsent(using, k -> new AmmoStat());
            double dShield = Math.max(0, t.lastShield - shield), dHull = Math.max(0, t.lastHull - hull);
            boolean shieldPhase = t.lastShield > 0;
            // old samples fade, so the numbers follow the current enemy
            st.shieldDmg *= 0.9995;
            st.shieldSec *= 0.9995;
            st.hullDmg *= 0.9995;
            st.hullSec *= 0.9995;
            if (shieldPhase) {
                st.shieldDmg += dShield;
                st.shieldSec += dt / 1000.0;
            }
            if (!shieldPhase || dHull > 0) {
                st.hullDmg += dHull;
                st.hullSec += (shieldPhase ? 0 : dt) / 1000.0;
            }
        }
        t.lastShield = shield;
        t.lastHull = hull;
    }

    /** The ammo to shoot right now according to what has worked best so far (tries each ammo for a couple of seconds first). */
    public SelectableItem.Laser learnedAmmo(Ship target) {
        if (cfg.VS_PILOT != Config.General.HakClanTutucu.VsPilot.AUTO || !cfg.VS_AMMO_LEARN || target == null) return null;
        long now = System.currentTimeMillis();
        boolean shieldPhase = target.health.maxShield > 0 && (double) target.health.shield / target.health.maxShield > 0.1;
        if (learnedChoice != null && now - learnedSince < 3000) return learnedChoice; // hold a choice for a moment

        List<SelectableItem.Laser> usable = new ArrayList<>();
        for (SelectableItem.Laser l : LEARN_ORDER) {
            Item i = items.getItem(l).orElse(null);
            if (i != null && i.isAvailable() && i.isUsable() && i.getQuantity() > 0) usable.add(l);
        }
        if (usable.size() < 2) return null; // nothing to choose between

        SelectableItem.Laser pick = null;
        for (SelectableItem.Laser l : usable) { // explore what hasn't been tried long enough in this phase
            AmmoStat st = ammoStats.get(l);
            if (st == null || st.seconds(shieldPhase) < 2.0) {
                pick = l;
                break;
            }
        }
        if (pick == null) {
            double best = -1;
            for (SelectableItem.Laser l : usable) {
                double d = ammoStats.get(l).dps(shieldPhase);
                if (d > best) {
                    best = d;
                    pick = l;
                }
            }
            // switch only for a clearly better ammo
            SelectableItem.Laser cur = hero.getLaser();
            AmmoStat cs = cur == null ? null : ammoStats.get(cur);
            if (cur != null && cs != null && usable.contains(cur) && best < cs.dps(shieldPhase) * 1.15) pick = cur;
        }
        if (pick != learnedChoice) {
            learnedChoice = pick;
            learnedSince = now;
        }
        return learnedChoice;
    }

    private String ammoSummary() {
        SelectableItem.Laser bs = null, bh = null;
        double ds = 0, dh = 0;
        for (Map.Entry<SelectableItem.Laser, AmmoStat> e : ammoStats.entrySet()) {
            if (e.getValue().shieldSec >= 2 && e.getValue().dps(true) > ds) {
                ds = e.getValue().dps(true);
                bs = e.getKey();
            }
            if (e.getValue().hullSec >= 2 && e.getValue().dps(false) > dh) {
                dh = e.getValue().dps(false);
                bh = e.getKey();
            }
        }
        if (bs == null && bh == null) return null;
        return "Best ammo: " + (bs == null ? "?" : VsMode.pretty(bs) + " on shield (" + (int) ds + "/s)")
                + "   " + (bh == null ? "?" : VsMode.pretty(bh) + " on hull (" + (int) dh + "/s)");
    }

    /** The current laser ammo ran out: the best owned replacement (so the fight doesn't stall on an empty slot). */
    public SelectableItem.Laser ammoWhenEmpty() {
        if (!active()) return null;
        SelectableItem.Laser now = hero.getLaser();
        if (now != null) {
            Item i = items.getItem(now).orElse(null);
            if (i != null && i.getQuantity() > 0) return null;
        }
        SelectableItem.Laser[] order = {cfg.VS_AMMO_A, cfg.VS_AMMO_B, SelectableItem.Laser.UCB_100, SelectableItem.Laser.RSB_75,
                SelectableItem.Laser.MCB_50, SelectableItem.Laser.SAB_50, SelectableItem.Laser.MCB_25, SelectableItem.Laser.LCB_10};
        for (SelectableItem.Laser l : order) {
            if (l == null || l == now) continue;
            Item i = items.getItem(l).orElse(null);
            if (i != null && i.isAvailable() && i.isUsable() && i.getQuantity() > 0) return l;
        }
        return null;
    }
}
