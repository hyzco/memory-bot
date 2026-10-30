package com.github.manolo8.darkbot.core.api.adapters;

import com.github.manolo8.darkbot.core.BotInstaller;
import com.github.manolo8.darkbot.core.api.Capability;
import com.github.manolo8.darkbot.core.api.GameAPIImpl;
import com.github.manolo8.darkbot.core.utils.ByteUtils;
import com.github.manolo8.darkbot.utils.StartupParams;
import eu.darkbot.api.DarkMem;
import eu.darkbot.api.RunnerKekkaPlayer;

/**
 * Hosts the game in a real Flash ActiveX control (KekkaRunner.exe + DarkFlash.ocx) instead of
 * the KekkaPlayer emulator, reading memory/sending input the same way DarkMemAdapter does.
 */
public class RunnerKekkaAdapter extends GameAPIImpl<
        RunnerKekkaPlayer,
        RunnerKekkaPlayer,
        DarkMem,
        ByteUtils.ExtraMemoryReader,
        RunnerKekkaPlayer.RunnerKekkaInput,
        NoopAPIAdapter.NoOpDirectInteraction> {

    public RunnerKekkaAdapter(StartupParams params, RunnerKekkaPlayer runner, BotInstaller botInstaller) {
        super(params,
                runner,
                runner,
                runner.getMemory(),
                new ByteUtils.ExtraMemoryReader(runner.getMemory(), botInstaller),
                runner.getInteraction(),
                new NoopAPIAdapter.NoOpDirectInteraction(),
                Capability.LOGIN,
                Capability.INITIALLY_SHOWN,
                Capability.CREATE_WINDOW_THREAD,
                Capability.PROXY,
                Capability.HANDLER_FLASH_PATH,
                Capability.HANDLER_CLIENT_SIZE,
                Capability.HANDLER_MIN_CLIENT_SIZE);
    }

    @Override
    public String getVersion() {
        return runner().getVersionTag();
    }

    private RunnerKekkaPlayer runner() {
        return window;
    }
}
