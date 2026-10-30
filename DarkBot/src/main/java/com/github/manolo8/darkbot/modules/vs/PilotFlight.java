package com.github.manolo8.darkbot.modules.vs;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.MapManager;
import eu.darkbot.api.game.items.Item;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.managers.HeroItemsAPI;

import java.util.List;

/**
 * Flies the ship during an AI-piloted fight, the way a player would:
 * <ul>
 *   <li>closes in when the target is out of laser range (with a speed boost when it is far),</li>
 *   <li>then holds the fight range, either standing, backing off when too close, or circling the target,</li>
 *   <li>retreats when the simulation says the fight is lost (away from the enemies, boosted),</li>
 *   <li>and never fights the player for the mouse: any real input pauses it for a couple of seconds.</li>
 * </ul>
 */
final class PilotFlight {

    private static final long COMMAND_GAP_MS = 350, FLEE_MS = 6000, USER_YIELD_MS = 2500;
    private static final double MAP_MARGIN = 350;

    static final SelectableItem[] SPEED_ITEMS = {
            SelectableItem.Tech.SPEED_BURST, SelectableItem.Ability.TARTARUS_SPEED_BOOST,
            SelectableItem.Ability.TARTARUS_PLUS_SPEED_BOOST,
    };

    private final HeroManager hero;
    private final Config.General.HakClanTutucu cfg;
    private final HeroItemsAPI items;

    private long lastCommand, fleeUntil, lastBoost, lastFlip;
    private double orbitDir = 1;
    private volatile String status = "";

    private final EnemyProfiler profiler;

    PilotFlight(Main main, HeroItemsAPI items, EnemyProfiler profiler) {
        this.profiler = profiler;
        this.hero = main.hero;
        this.cfg = main.config.GENERAL.HAK_CLAN_TUTUCU;
        this.items = items;
    }

    String status() {
        return status;
    }

    void tick(List<Ship> enemies, Ship target, FightSim.Result outlook, long now) {
        if (!cfg.VS_FLY) {
            status = "";
            return;
        }
        if (now - com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder.lastUserInputAt < USER_YIELD_MS) {
            status = "You're flying";
            return;
        }

        double hx = hero.locationInfo.now.x, hy = hero.locationInfo.now.y;

        // retreat when the fight is lost (and keep going for a few seconds so it doesn't dither)
        if (cfg.VS_FLEE && outlook != null && outlook.win < 0.12 && outlook.survive < 0.35 && !enemies.isEmpty())
            fleeUntil = now + FLEE_MS;
        if (now < fleeUntil && !enemies.isEmpty()) {
            double cx = 0, cy = 0;
            for (Ship s : enemies) {
                cx += s.locationInfo.now.x;
                cy += s.locationInfo.now.y;
            }
            cx /= enemies.size();
            cy /= enemies.size();
            double ang = Math.atan2(hy - cy, hx - cx);
            move(hx + Math.cos(ang) * 2000, hy + Math.sin(ang) * 2000, now);
            boost(now);
            status = "Retreating";
            return;
        }

        if (target == null || !target.isValid()) {
            status = "";
            return;
        }

        double tx = target.locationInfo.now.x, ty = target.locationInfo.now.y;
        double dist = Math.hypot(tx - hx, ty - hy);
        double range = cfg.VS_FIGHT_RANGE;
        double away = Math.atan2(hy - ty, hx - tx); // from the target towards me

        // what the profile says about how this player fights
        EnemyProfiler.Profile pr = profiler.profileOf(target);
        boolean kiter = pr != null && profiler.styleOf(pr) == EnemyProfiler.Style.KITER;
        if (pr != null && profiler.hitAndRun(pr) && !target.isAttacking(hero) && dist > range + 120 && dist < 1600) {
            status = "Waiting for them to come back (hit and run)";
            return; // chasing a hit-and-run player only drags you around
        }

        if (dist > range + 120) {
            move(tx + Math.cos(away) * range * 0.85, ty + Math.sin(away) * range * 0.85, now);
            if (dist > (kiter ? 700 : 1000)) boost(now);
            status = "Closing in";
            return;
        }

        switch (cfg.VS_FLY_STYLE) {
            case ORBIT: {
                double step = Math.max(0.3, Math.min(0.7, hero.getSpeed() * 0.8 / Math.max(300, range))) * orbitDir;
                double[] dest = {tx + Math.cos(away + step) * range * 0.9, ty + Math.sin(away + step) * range * 0.9};
                double[] clamped = clamp(dest[0], dest[1]);
                // pushed against the map edge: circle the other way
                if (Math.hypot(clamped[0] - dest[0], clamped[1] - dest[1]) > 150 && now - lastFlip > 1500) {
                    orbitDir = -orbitDir;
                    lastFlip = now;
                    step = -step;
                    dest = new double[]{tx + Math.cos(away + step) * range * 0.9, ty + Math.sin(away + step) * range * 0.9};
                }
                move(dest[0], dest[1], now);
                status = "Circling the target";
                break;
            }
            case HOLD_RANGE:
                if (dist < range - 150) {
                    move(tx + Math.cos(away) * range * 0.9, ty + Math.sin(away) * range * 0.9, now);
                    status = "Backing off to range";
                } else status = "Holding range";
                break;
            default:
                status = "";
        }
    }

    private static double[] clamp(double x, double y) {
        double w = MapManager.internalWidth, h = MapManager.internalHeight;
        return new double[]{Math.max(MAP_MARGIN, Math.min(w - MAP_MARGIN, x)), Math.max(MAP_MARGIN, Math.min(h - MAP_MARGIN, y))};
    }

    private void move(double x, double y, long now) {
        if (now - lastCommand < COMMAND_GAP_MS) return;
        lastCommand = now;
        double[] p = clamp(x, y);
        hero.drive.move(p[0], p[1]);
    }

    /** A speed item when closing a long gap or running; one press at most every few seconds. */
    private void boost(long now) {
        if (now - lastBoost < 4000) return;
        for (SelectableItem s : SPEED_ITEMS) {
            Item i = items.getItem(s).orElse(null);
            if (i == null || !i.isAvailable() || !i.isUsable() || !i.isReady()) continue;
            if (items.useItem(s).isSuccessful()) {
                lastBoost = now;
                System.out.println("VS pilot: speed boost " + VsMode.pretty(s));
                return;
            }
        }
    }
}
