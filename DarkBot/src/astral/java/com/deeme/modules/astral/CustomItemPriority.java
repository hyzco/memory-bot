package com.deeme.modules.astral;

import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Option;
import eu.darkbot.api.config.annotations.Table;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration("astral_gate.custom_item_priority")
public class CustomItemPriority {
    @Option("astral_gate.custom_item_priority.active")
    public boolean active = false;

    @Option("astral_gate.custom_item_priority.item_infos")
    @Table
    public Map<String, ItemInfo> itemInfos = new LinkedHashMap<>();
}