package com.deeme.modules.astral;

import eu.darkbot.api.config.annotations.Configuration;

@Configuration("astral_gate.ship_type.list")
enum AvailableShips {
    ZEPHYR("ship_zephyr", 12, 16),
    DIMINISHER("ship_diminisher", 15, 15),
    SENTINEL("ship_sentinel", 15, 15),
    PUSAT("ship_pusat", 12, 11);

    private final String shipType;
    private final int maxWeapons;
    private final int maxGenerators;

    AvailableShips(String shipType, int maxWeapons, int maxGenerators) {
        this.shipType = shipType;
        this.maxWeapons = maxWeapons;
        this.maxGenerators = maxGenerators;
    }

    String getShipType() {
        return shipType;
    }

    int getMaxWeapons() {
        return maxWeapons;
    }

    int getMaxGenerators() {
        return maxGenerators;
    }

    boolean matches(String currentShipType) {
        return shipType.equalsIgnoreCase(currentShipType);
    }
}