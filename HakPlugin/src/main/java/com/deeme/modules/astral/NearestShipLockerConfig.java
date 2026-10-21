package com.deeme.modules.astral;

import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.config.annotations.Option;

@Configuration("nearest_ship_locker")
public class NearestShipLockerConfig {
    @Option("nearest_ship_locker.enabled")
    public boolean enabled = false;

    @Option("nearest_ship_locker.enemy_only")
    public boolean enemyOnly = true;

    @Option("nearest_ship_locker.ignore_invisible")
    public boolean ignoreInvisible = true;

    @Option("nearest_ship_locker.include_pets")
    public boolean includePets = false;

    @Option("nearest_ship_locker.auto_lock")
    public boolean autoLock = true;

    @Option("nearest_ship_locker.sticky_target")
    public boolean stickyTarget = true;

    @Option("nearest_ship_locker.clear_when_empty")
    public boolean clearWhenEmpty = false;

    @Option("nearest_ship_locker.max_range")
    @Number(min = 0, max = 6000, step = 50)
    public int maxRange = 2500;

    @Option("nearest_ship_locker.refresh_ms")
    @Number(min = 50, max = 2000, step = 25)
    public int refreshMs = 200;
}
