package com.deeme.modules.astral;

import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.config.annotations.Option;

@Configuration("astral_gate.custom_item_priority.item_infos.entry")
public class ItemInfo {
    @Option("astral_gate.custom_item_priority.item_infos.entry.priority")
    @Number(min = 0, max = 50, step = 1)
    public int priority = 99;
}