package com.deeme.modules.astral;

import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.config.annotations.Option;

@Configuration("astral_gate.astral_plus_config")
public class AstralPlusConfig {
    @Option("astral_gate.astral_plus_config.min_time_per_action")
    @Number(min = 1, max = 60, step = 1)
    public int minTimePerAction = 3;

    @Option("astral_gate.astral_plus_config.enable_random")
    public boolean enableRandom = false;
}