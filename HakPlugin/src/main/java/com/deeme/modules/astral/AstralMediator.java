package com.deeme.modules.astral;

import eu.darkbot.api.game.other.Gui;
import eu.darkbot.api.managers.GameScreenAPI;

import java.util.Random;

final class AstralMediator {
    private static final String ASTRAL_GUI_KEY = "rogue_lite";
    private static final String SHIP_SELECTION_GUI_KEY = "rogue_lite_selection";

    private final GameScreenAPI gameScreen;
    private final Random random = new Random();

    AstralMediator(GameScreenAPI gameScreen) {
        this.gameScreen = gameScreen;
    }

    boolean isAstralGuiVisible() {
        Gui gui = getAstralGui();
        return gui != null && gui.isVisible();
    }

    void setAstralGuiVisible(boolean visible) {
        Gui gui = getAstralGui();
        if (gui != null && gui.isVisible() != visible) gui.setVisible(visible);
    }

    boolean isShipSelectionGuiVisible() {
        Gui gui = getShipSelectionGui();
        return gui != null && gui.isVisible();
    }

    void setShipSelectionGuiVisible(boolean visible) {
        Gui gui = getShipSelectionGui();
        if (gui != null && gui.isVisible() != visible) gui.setVisible(visible);
    }

    void chooseItem(int index, boolean randomize) {
        Gui gui = getAstralGui();
        if (gui == null) return;

        Rect rewardSlot = getRewardSlot(gui, index);
        click(gui, rewardSlot, randomize);
    }

    void chooseShip(int index, boolean randomize) {
        Gui gui = getShipSelectionGui();
        if (gui == null) return;

        Rect shipSlot = getShipSlot(gui, index);
        click(gui, shipSlot, randomize);
    }

    void clickAcceptButtonInShipSelection() {
        Gui gui = getShipSelectionGui();
        if (gui == null) return;

        Rect acceptButton = getShipAcceptButton(gui);
        click(gui, acceptButton, false);
    }

    private Gui getAstralGui() {
        return gameScreen.getGui(ASTRAL_GUI_KEY);
    }

    private Gui getShipSelectionGui() {
        return gameScreen.getGui(SHIP_SELECTION_GUI_KEY);
    }

    private void click(Gui gui, Rect rect, boolean randomize) {
        int padding = 6;
        int x;
        int y;

        if (randomize) {
            x = rect.randomX(random, padding);
            y = rect.randomY(random, padding);
        } else {
            x = rect.centerX();
            y = rect.centerY();
        }

        gui.click(x, y);
    }

    private Rect getRewardSelection(Gui gui) {
        int width = (int) Math.round(gui.getWidth());
        int height = (int) Math.round(gui.getHeight());

        int left = clamp((int) Math.round(width * 0.03), 12, 40);
        int top = clamp((int) Math.round(height * 0.20), 90, 190);
        int right = Math.max(left + 120, width - left);
        int bottomMargin = clamp((int) Math.round(height * 0.23), 120, 210);
        int bottom = Math.max(top + 60, height - bottomMargin);

        return new Rect(left, top, right, bottom);
    }

    private Rect getRewardSlot(Gui gui, int index) {
        Rect selection = getRewardSelection(gui);
        return selection.slice(clamp(index, 0, 2), 3, 10);
    }

    private Rect getShipSelection(Gui gui) {
        int width = (int) Math.round(gui.getWidth());
        int height = (int) Math.round(gui.getHeight());

        int left = clamp((int) Math.round(width * 0.08), 36, 110);
        int top = clamp((int) Math.round(height * 0.12), 22, 70);
        int right = Math.max(left + 140, width - left);
        int bottom = Math.min(height - 18, top + clamp((int) Math.round(height * 0.45), 95, 150));

        return new Rect(left, top, right, bottom);
    }

    private Rect getShipSlot(Gui gui, int index) {
        Rect selection = getShipSelection(gui);
        return selection.slice(clamp(index, 0, 3), 4, 12);
    }

    private Rect getShipAcceptButton(Gui gui) {
        int width = (int) Math.round(gui.getWidth());
        int height = (int) Math.round(gui.getHeight());

        int buttonWidth = clamp((int) Math.round(width * 0.16), 85, 140);
        int buttonHeight = clamp((int) Math.round(height * 0.07), 24, 40);
        int rightInset = clamp((int) Math.round(width * 0.03), 14, 28);
        int bottomInset = clamp((int) Math.round(height * 0.04), 12, 24);

        int right = Math.max(buttonWidth + 1, width - rightInset);
        int bottom = Math.max(buttonHeight + 1, height - bottomInset);
        int left = Math.max(5, right - buttonWidth);
        int top = Math.max(5, bottom - buttonHeight);

        return new Rect(left, top, right, bottom);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class Rect {
        private final int left;
        private final int top;
        private final int right;
        private final int bottom;

        private Rect(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        private Rect slice(int index, int divisions, int innerPadding) {
            int width = Math.max(1, right - left);
            int sliceWidth = Math.max(1, width / divisions);
            int sliceLeft = left + sliceWidth * index + innerPadding;
            int sliceRight = left + sliceWidth * (index + 1) - innerPadding;
            if (index == divisions - 1) sliceRight = right - innerPadding;
            if (sliceRight <= sliceLeft) sliceRight = sliceLeft + 1;
            return new Rect(sliceLeft, top, sliceRight, bottom);
        }

        private int centerX() {
            return left + Math.max(0, right - left) / 2;
        }

        private int centerY() {
            return top + Math.max(0, bottom - top) / 2;
        }

        private int randomX(Random random, int padding) {
            int min = Math.min(left + padding, right - 1);
            int max = Math.max(min, right - padding);
            return min + random.nextInt(Math.max(1, max - min + 1));
        }

        private int randomY(Random random, int padding) {
            int min = Math.min(top + padding, bottom - 1);
            int max = Math.max(min, bottom - padding);
            return min + random.nextInt(Math.max(1, max - min + 1));
        }
    }
}