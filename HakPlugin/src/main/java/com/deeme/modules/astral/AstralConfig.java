package com.deeme.modules.astral;

import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Dropdown;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.config.annotations.Option;
import eu.darkbot.api.config.annotations.Table;
import eu.darkbot.api.game.items.SelectableItem;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration("astral_gate")
public class AstralConfig {
    @Option("astral_gate.radius_min")
    @Number(min = 150, max = 1500, step = 10)
    public int radioMin = 560;

    @Option("astral_gate.min_cpus")
    @Number(min = 0, max = 250, step = 1)
    public int minCPUs = 0;

    @Option("astral_gate.default_laser")
    public String defaultLaser = SelectableItem.Laser.LCB_10.getId();

    @Option("astral_gate.default_rocket")
    public String defaultRocket = SelectableItem.Rocket.PLT_2026.getId();

    @Option("astral_gate.always_closest_npc")
    public boolean alwaysTheClosestNPC = false;

    @Option("astral_gate.min_shield_to_repair")
    @Number(min = 0, max = 1, step = 0.05)
    public double minShieldToRepair = 0.2;

    @Option("astral_gate.use_best_ammo_logic")
    @Dropdown
    public BestAmmoConfig useBestAmmoLogic = BestAmmoConfig.ONLY_MARKED;

    @Option("astral_gate.ship_type")
    @Dropdown
    public AvailableShips shipType = AvailableShips.ZEPHYR;

    @Option("astral_gate.auto_choose")
    public boolean autoChoose = true;

    @Option("astral_gate.chase_low_hp_npcs")
    public boolean chaseLowHpNpcs = true;

    @Option("astral_gate.low_hp_chase_threshold")
    @Number(min = 0.05, max = 0.95, step = 0.05)
    public double lowHpChaseThreshold = 0.25;

    @Option("astral_gate.max_deaths")
    @Number(min = 0, max = 50, step = 1)
    public int maxDeaths = 5;

    @Option("astral_gate.max_runs")
    @Number(min = 0, max = 100, step = 1)
    public int maxRuns = 0;

    @Option("astral_gate.use_ship_ability")
    public boolean useShipAbility = true;

    @Option("astral_gate.use_formations")
    public boolean useFormations = true;

    @Option("astral_gate.use_pet")
    public boolean usePet = true;

    @Option("astral_gate.min_rift_for_brutal")
    @Number(min = 0, max = 30, step = 1)
    public int minRiftForBrutal = 5;

    @Option("astral_gate.portal_infos")
    @Table
    public Map<String, PortalInfo> portalInfos = new LinkedHashMap<>();

    @Option("astral_gate.custom_item_priority")
    public CustomItemPriority customItemPriority = new CustomItemPriority();

    @Option("astral_gate.astral_plus_config")
    public AstralPlusConfig astralPlusConfig = new AstralPlusConfig();
}