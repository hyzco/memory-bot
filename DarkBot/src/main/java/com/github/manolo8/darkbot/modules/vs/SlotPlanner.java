package com.github.manolo8.darkbot.modules.vs;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.objects.facades.SlotBarsProxy;
import com.github.manolo8.darkbot.core.objects.slotbars.SlotBar;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.managers.HeroItemsAPI;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Works out which items the fight needs on the slot bar and where they should go.
 * <p>
 * The bot can only use an item by pressing its slot-bar key, so everything the strategy and the pilot rely on has to
 * sit on the bar. This build can neither read where slots are on screen nor assign an item to a slot, so the bot can't
 * drag items itself: it tells you exactly what to put where (preferred slots first: main ammo on 1, second ammo on 2,
 * decelerator on 3, then the free ones) and the plan updates as you do it.
 */
final class SlotPlanner {

    private final Main main;
    private final VsMode vs;
    private final Config.General.HakClanTutucu cfg;
    private final HeroItemsAPI items;

    SlotPlanner(Main main, VsMode vs, HeroItemsAPI items) {
        this.main = main;
        this.vs = vs;
        this.cfg = main.config.GENERAL.HAK_CLAN_TUTUCU;
        this.items = items;
    }

    private static String slotName(int n) {
        return n == 10 ? "0" : String.valueOf(n);
    }

    /** Wanted items with the slot they prefer (0 = any free one), most important first. */
    private Map<SelectableItem, Integer> wanted() {
        Map<SelectableItem, Integer> w = new LinkedHashMap<>();
        boolean auto = cfg.VS_PILOT == Config.General.HakClanTutucu.VsPilot.AUTO;
        boolean pilot = cfg.VS_PILOT != Config.General.HakClanTutucu.VsPilot.CLASSIC;

        // ammo: the two you fight with take the first slots
        SelectableItem a = null, b = null;
        if (cfg.VS_MIX_ACTIVE()) {
            a = cfg.VS_AMMO_A;
            b = cfg.VS_AMMO_B;
        } else {
            a = cfg.VS_PREP_LASER != null ? cfg.VS_PREP_LASER : (auto && cfg.VS_AMMO_LEARN ? SelectableItem.Laser.UCB_100 : null);
            b = auto && cfg.VS_AMMO_LEARN ? SelectableItem.Laser.RSB_75 : null;
        }
        if (a != null) w.put(a, 1);
        if (b != null && b != a) w.put(b, 2);
        if (auto && cfg.VS_ROCKETS) w.put(SelectableItem.Rocket.DCR_250, 3);

        if (auto && cfg.VS_AMMO_LEARN) {
            w.putIfAbsent(SelectableItem.Laser.SAB_50, 0);
            w.putIfAbsent(SelectableItem.Laser.MCB_50, 0);
        }
        if (cfg.VS_PREP_ROCKET != null) w.putIfAbsent(cfg.VS_PREP_ROCKET, 0);
        if (auto && cfg.VS_ROCKETS) for (SelectableItem r : PilotBrain.DAMAGE_ROCKETS) if (vs.has(r)) {
            w.putIfAbsent(r, 0);
            break;
        }
        if (cfg.VS_PREP_FORMATION != null) w.putIfAbsent(cfg.VS_PREP_FORMATION, 0);
        if (auto) {
            SelectableItem d = vs.bestDefensiveFormation(), o = vs.firstOffensiveFormation();
            if (d != null) w.putIfAbsent(d, 0);
            if (o != null) w.putIfAbsent(o, 0);
        }

        // specials and the macro
        if (pilot || cfg.VS_MACRO_ENABLED) {
            w.putIfAbsent(SelectableItem.Special.ISH_01, 0);
            w.putIfAbsent(SelectableItem.Special.EMP_01, 0);
            w.putIfAbsent(SelectableItem.Special.SMB_01, 0);
        }
        for (SelectableItem m : vs.macroItems()) w.putIfAbsent(m, 0);

        // the ship's own abilities: a couple of heals, speed, and the damage tools it has
        int heals = 0;
        for (SelectableItem d : VsMode.DEFENCE) if (heals < 2 && vs.has(d)) {
            w.putIfAbsent(d, 0);
            heals++;
        }
        if (auto) {
            for (SelectableItem s : PilotFlight.SPEED_ITEMS) if (vs.has(s)) {
                w.putIfAbsent(s, 0);
                break;
            }
            int offense = 0;
            for (SelectableItem o : PilotBrain.OFFENSE) if (offense < 3 && vs.has(o)) {
                w.putIfAbsent(o, 0);
                offense++;
            }
        }
        // only what the ship actually owns can be placed
        w.keySet().removeIf(i -> !vs.has(i));
        return w;
    }

    /** The plan as short lines for the HUD; empty when everything needed is already where it should be. */
    List<String> plan() {
        List<String> out = new ArrayList<>();
        try {
            SlotBar bar = main.facadeManager.slotBars.standardBar;
            boolean[] used = new boolean[11];
            for (int i = 0; i < 10; i++) {
                SlotBar.Slot s = bar.getSlot(i);
                if (s != null && s.item != null && s.item.address != 0) used[i + 1] = true;
            }

            Map<SelectableItem, Integer> wanted = wanted();
            boolean[] reserved = new boolean[11];
            for (Integer p : wanted.values()) if (p != null && p > 0) reserved[p] = true;

            List<SelectableItem> missing = new ArrayList<>();
            for (Map.Entry<SelectableItem, Integer> e : wanted.entrySet()) {
                SelectableItem it = e.getKey();
                int pref = e.getValue();
                var item = items.getItem(it).orElse(null);
                if (item == null) continue;
                int now = item.isUsable() ? firstStandardSlot(item) : -1;

                if (pref > 0) {
                    if (now == pref) continue;
                    String where = now > 0 ? "move " + VsMode.pretty(it) + " from slot " + slotName(now) + " to slot " + slotName(pref)
                            : "put " + VsMode.pretty(it) + " on slot " + slotName(pref);
                    if (used[pref] && now != pref) where += " (swap with what is there)";
                    out.add(capitalize(where));
                    if (now > 0) used[now] = false;
                    used[pref] = true;
                } else if (!item.isUsable()) {
                    missing.add(it);
                }
            }
            for (SelectableItem it : missing) {
                int slot = 0;
                for (int s = 4; s <= 10 && slot == 0; s++) if (!used[s] && !reserved[s]) slot = s;
                for (int s = 1; s <= 10 && slot == 0; s++) if (!used[s]) slot = s;
                if (slot == 0) {
                    out.add("No free slot for " + VsMode.pretty(it) + " - clear one");
                } else {
                    used[slot] = true;
                    out.add("Put " + VsMode.pretty(it) + " on slot " + slotName(slot));
                }
            }
        } catch (RuntimeException e) {
            // bars not readable right now (reload): the next refresh will have them
        }
        return out;
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** Slot number (1..10) on the standard bar, or -1 when the item is only on another bar. */
    private static int firstStandardSlot(eu.darkbot.api.game.items.Item item) {
        if (!(item instanceof com.github.manolo8.darkbot.core.objects.slotbars.Item)) return -1;
        com.github.manolo8.darkbot.core.objects.slotbars.Item core = (com.github.manolo8.darkbot.core.objects.slotbars.Item) item;
        if (core.getSlotBarType() != SlotBarsProxy.Type.DEFAULT_BAR) return -1;
        int n = core.getFirstSlotNumber();
        return n >= 1 && n <= 10 ? n : -1;
    }
}
