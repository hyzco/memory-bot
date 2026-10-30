package com.github.manolo8.darkbot.core.api.adapters;

import com.github.manolo8.darkbot.core.api.GameAPI;
import com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder;
import eu.darkbot.api.KekkaPlayer;

/**
 * Mouse / keyboard input for the hosted game as plain window messages.
 * <p>
 * KekkaPlayer's native input calls (keyClick, mouseClick, ...) access-violate inside KekkaPlayer.dll in this setup
 * (a hard, uncatchable JVM crash, typically right after a lock when the bot starts attacking). Window messages go
 * through the game's own input handling, the same as a real mouse and keyboard. The native calls are only used as
 * a last resort when the game window isn't docked (no window to post to).
 */
public class KekkaWindowInput implements GameAPI.Interaction {
    private final KekkaPlayer kekka;

    public KekkaWindowInput(KekkaPlayer kekka) {
        this.kekka = kekka;
    }

    @Override
    public int getVersion() {
        return kekka.getVersion();
    }

    @Override
    public void tick() {
        kekka.tick();
    }

    @Override
    public void keyClick(int keyCode) {
        if (NativeWindowEmbedder.canPostInput()) NativeWindowEmbedder.pressKey((char) keyCode);
        else kekka.keyClick(keyCode);
    }

    @Override
    public void sendText(String text) {
        if (NativeWindowEmbedder.canPostInput()) NativeWindowEmbedder.typeText(text);
        else kekka.sendText(text);
    }

    @Override
    public void mouseMove(int x, int y) {
        if (NativeWindowEmbedder.canPostInput()) NativeWindowEmbedder.postMouse(0, x, y);
        else kekka.mouseMove(x, y);
    }

    @Override
    public void mouseDown(int x, int y) {
        if (NativeWindowEmbedder.canPostInput()) NativeWindowEmbedder.postMouse(1, x, y);
        else kekka.mouseDown(x, y);
    }

    @Override
    public void mouseUp(int x, int y) {
        if (NativeWindowEmbedder.canPostInput()) NativeWindowEmbedder.postMouse(2, x, y);
        else kekka.mouseUp(x, y);
    }

    @Override
    public void mouseClick(int x, int y) {
        if (!NativeWindowEmbedder.clickGame(x, y)) {
            if (!NativeWindowEmbedder.canPostInput()) kekka.mouseClick(x, y);
        }
    }
}
