package com.deeme.modules.astral;

import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.config.annotations.Option;

@Configuration("astral_gate.portal_infos.entry")
public class PortalInfo {
    @Option("astral_gate.portal_infos.entry.priority")
    @Number(min = 0, max = 50, step = 1)
    public int priority = 99;

    @Option("astral_gate.portal_infos.entry.ignore_limit")
    public boolean ignoreLimit = false;
}