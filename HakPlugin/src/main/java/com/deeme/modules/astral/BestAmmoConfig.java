package com.deeme.modules.astral;

import eu.darkbot.api.config.annotations.Configuration;

@Configuration("astral_gate.use_best_ammo_logic.list")
enum BestAmmoConfig {
    ALWAYS,
    SPECIAL_LOGIC,
    ONLY_MARKED
}