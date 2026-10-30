package com.github.manolo8.darkbot.modules.vs;

import java.util.Random;

/**
 * Small Monte-Carlo model of a fight, used to compare "what if I do X now" with "what if I do nothing".
 * <p>
 * It is deliberately simple: damage in/out are the measured rates with random variation (fights are noisy), hits can
 * come in periodic bursts (rocket volleys), foes die one after another with a short retarget delay. The effect of an
 * action (insta-shield, heal, formation) is a few numbers on top of that. The numbers it is fed are measured live, so
 * it adapts to the actual ship, enemy and ammo instead of assuming them.
 */
final class FightSim {

    /** The situation to simulate. All HP values are absolute (hull + shield). */
    static final class Params {
        double myEhp, myMax;
        /** My damage per second on the current target. */
        double dpsOut;
        /** Foes: index 0 is the one I'm shooting. */
        double[] foeEhp, foeDps;
        /** Share (0..1) of incoming damage that arrives as periodic bursts, and their period / time to the next one. */
        double burstShare, burstPeriod, nextBurstIn = -1;
        /** Random variation of rates (sigma of a log-normal factor). */
        double noise = 0.35;
        double horizon = 40, dt = 0.5;
    }

    /** What an action does for a while. Neutral values = no effect. */
    static final class Action {
        final String name;
        double invulnSeconds, healAbs, healSeconds = 4;
        double dmgInMul = 1, dmgOutMul = 1;
        /** How long dmgOutMul lasts (an ability), 0 = the whole fight (a formation). */
        double outSeconds;
        /** Preference penalty so items aren't spent for a negligible gain. */
        double cost;

        Action(String name) {
            this.name = name;
        }
    }

    static final class Result {
        double win, survive, avgLifeLeft, avgSecondsToWin = Double.NaN, avgSecondsToDie = Double.NaN;

        /** What the pilot maximises: winning matters most, surviving (even without winning) counts too. */
        double utility() {
            return win + 0.6 * survive;
        }
    }

    private final Random rnd = new Random();

    private double factor(double sigma) {
        return Math.exp(rnd.nextGaussian() * sigma - sigma * sigma / 2);
    }

    Result run(Params p, Action a, int runs) {
        int wins = 0, survived = 0, winSamples = 0, dieSamples = 0;
        double life = 0, winTime = 0, dieTime = 0;
        int n = p.foeEhp.length;

        for (int r = 0; r < runs; r++) {
            double me = p.myEhp;
            double[] foe = p.foeEhp.clone();
            int target = 0;
            double retargetUntil = 0, nextBurst = p.nextBurstIn;
            double t = 0;
            boolean dead = false, won = false;

            while (t < p.horizon) {
                double dt = p.dt;

                // my damage
                if (t >= retargetUntil) {
                    double out = p.dpsOut * factor(p.noise) * dt * (a.outSeconds <= 0 || t < a.outSeconds ? a.dmgOutMul : 1);
                    foe[target] -= out;
                    if (foe[target] <= 0) {
                        target = -1;
                        for (int i = 0; i < n; i++) if (foe[i] > 0) {
                            target = i;
                            break;
                        }
                        if (target < 0) {
                            won = true;
                            winTime += t;
                            winSamples++;
                            break;
                        }
                        retargetUntil = t + 1.2;
                    }
                }

                // incoming damage from everything still alive
                double dps = 0;
                for (int i = 0; i < n; i++) if (foe[i] > 0) dps += p.foeDps[i];
                double base = dps * (1 - p.burstShare) * dt;
                double burst = 0;
                if (p.burstPeriod > 0 && nextBurst >= 0 && t >= nextBurst) {
                    burst = dps * p.burstShare * p.burstPeriod;
                    nextBurst += p.burstPeriod;
                }
                double dmg = (base + burst) * factor(p.noise) * a.dmgInMul;
                if (a.invulnSeconds > 0 && t < a.invulnSeconds) dmg = 0;
                if (a.healAbs > 0 && t < a.healSeconds) me += a.healAbs / a.healSeconds * dt;

                me = Math.min(p.myMax, me) - dmg;
                if (me <= 0) {
                    dead = true;
                    dieTime += t;
                    dieSamples++;
                    break;
                }
                t += dt;
            }

            if (won && !dead) wins++;
            if (!dead) {
                survived++;
                life += me / p.myMax;
            }
        }

        Result res = new Result();
        res.win = (double) wins / runs;
        res.survive = (double) survived / runs;
        res.avgLifeLeft = survived == 0 ? 0 : life / survived;
        if (winSamples > 0) res.avgSecondsToWin = winTime / winSamples;
        if (dieSamples > 0) res.avgSecondsToDie = dieTime / dieSamples;
        return res;
    }
}
