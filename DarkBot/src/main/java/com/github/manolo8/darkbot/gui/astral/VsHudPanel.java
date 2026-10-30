package com.github.manolo8.darkbot.gui.astral;

import com.github.manolo8.darkbot.modules.vs.VsMode;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Battle HUD for VS mode: what is loaded, what is ready (with cooldowns), who is fighting us and how much damage each
 * is doing, what the macro is waiting for, plus the advisor's tips. Fed from {@link VsMode.Hud} snapshots.
 */
public class VsHudPanel extends JPanel {

    private static final Color READY = new Color(0x22C55E), COOLDOWN = new Color(0xF59E0B), MISSING = new Color(0x6B7280);

    private final JLabel modeLabel = new JLabel("VS");
    private final JLabel loadoutLabel = new JLabel(" ");
    private final JLabel macroLabel = new JLabel(" ");
    private final JLabel readyLabel = new JLabel(" ");
    private final JLabel planLabel = new JLabel(" ");
    private final JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
    private final JPanel enemies = new JPanel(new MigLayout("ins 0, fillx, wrap 1, gapy 3", "[grow,fill]", ""));
    private final JTextArea tips = new JTextArea();
    private final JButton prepareButton = new JButton("Prepare for battle");
    private final JButton strategyButton = new JButton("Strategy...");

    private String lastKey = "";

    private final javax.swing.JComboBox<com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.VsPilot> pilotBox =
            new javax.swing.JComboBox<>(com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.VsPilot.values());
    private final JButton doButton = new JButton("Do suggested action");
    private boolean updatingPilot;

    public VsHudPanel(Runnable onPrepare, Runnable onStrategy,
                      java.util.function.Consumer<com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.VsPilot> onPilot,
                      Runnable onDo) {
        super(new MigLayout("ins 8, fillx, wrap 1, gapy 5", "[grow,fill]", ""));
        putClientProperty("FlatLaf.style", "background: #171A24; arc: 14");

        modeLabel.putClientProperty("FlatLaf.style", "font: +1 bold");
        loadoutLabel.putClientProperty("FlatLaf.style", "foreground: #9AA3B5; font: -1");
        macroLabel.putClientProperty("FlatLaf.style", "foreground: #9AA3B5; font: -1");
        chips.setOpaque(false);
        enemies.setOpaque(false);
        tips.setEditable(false);
        tips.setFocusable(false);
        tips.setLineWrap(true);
        tips.setWrapStyleWord(true);
        tips.setOpaque(false);
        tips.putClientProperty("FlatLaf.style", "foreground: #C4B5FD; font: -1");

        prepareButton.addActionListener(e -> onPrepare.run());
        strategyButton.addActionListener(e -> onStrategy.run());
        prepareButton.putClientProperty("FlatLaf.style", "arc: 10; background: #7C3AED; foreground: #FFFFFF");
        strategyButton.putClientProperty("FlatLaf.style", "arc: 10");

        pilotBox.addActionListener(e -> {
            if (!updatingPilot && pilotBox.getSelectedItem() != null)
                onPilot.accept((com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.VsPilot) pilotBox.getSelectedItem());
        });
        doButton.addActionListener(e -> onDo.run());
        doButton.putClientProperty("FlatLaf.style", "arc: 10; background: #0EA5E9; foreground: #FFFFFF");

        add(modeLabel);
        add(pilotBox, "growx");
        add(doButton, "growx, h 28!, hidemode 3");
        add(loadoutLabel);
        add(chips);
        add(enemies);
        add(readyLabel, "hidemode 3");
        add(planLabel, "hidemode 3");
        planLabel.putClientProperty("FlatLaf.style", "foreground: #38BDF8; font: -1");
        add(macroLabel);
        add(tips, "hidemode 3");
        JPanel buttons = new JPanel(new MigLayout("ins 0, fillx, gapx 6", "[grow,fill][grow,fill]", ""));
        buttons.setOpaque(false);
        buttons.add(prepareButton, "h 30!");
        buttons.add(strategyButton, "h 30!");
        add(buttons);
    }

    public void setPilot(com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.VsPilot mode) {
        updatingPilot = true;
        if (pilotBox.getSelectedItem() != mode) pilotBox.setSelectedItem(mode);
        updatingPilot = false;
        boolean assisted = mode == com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.VsPilot.ASSISTED;
        if (doButton.isVisible() != assisted) doButton.setVisible(assisted);
    }

    private static void setText(JLabel label, String text) {
        if (!text.equals(label.getText())) label.setText(text);
    }

    private final java.util.List<JLabel> chipLabels = new java.util.ArrayList<>();
    private final java.util.List<EnemyView> enemyViews = new java.util.ArrayList<>();

    /** One enemy line whose widgets are reused; only their values change. */
    private static final class EnemyView {
        final JPanel row = new JPanel(new MigLayout("ins 0, fillx, gapx 6", "[110!][grow,fill][grow,fill]", ""));
        final JLabel name = new JLabel();
        final JProgressBar life = bar(100, "", new Color(0x22C55E));
        final JProgressBar threat = bar(0, "threat", new Color(0xF43F5E));

        EnemyView() {
            row.setOpaque(false);
            row.add(name);
            row.add(life, "h 14!");
            row.add(threat, "h 14!");
        }

        void set(VsMode.EnemyRow e) {
            String text = (e.isTarget ? "> " : "") + e.name + (e.attackingMe ? "  !" : "");
            if (!text.equals(name.getText())) name.setText(text);
            name.setForeground(e.isTarget ? new Color(0xF43F5E) : e.attackingMe ? new Color(0xF59E0B) : new Color(0xE8EAF2));
            name.setToolTipText(e.name + "  -  " + e.distance + " away");
            if (life.getValue() != e.lifePercent) life.setValue(e.lifePercent);
            String lifeText = "life " + e.lifePercent + "%";
            if (!lifeText.equals(life.getString())) life.setString(lifeText);
            if (threat.getValue() != e.threatPercent) threat.setValue(e.threatPercent);
        }
    }

    public void update(VsMode.Hud hud, List<String> advice) {
        if (hud == null) {
            setText(modeLabel, "VS: waiting for the game...");
            return;
        }
        setText(modeLabel, "VS  -  " + hud.mode);
        setText(loadoutLabel, hud.formation + "  |  " + hud.laser + "  |  " + hud.rocket + "  |  pet " + hud.petGear
                + "  |  you " + hud.heroLifePercent + "%  speed " + hud.speed
                + (hud.prepare == null || hud.prepare.isEmpty() ? "" : "  |  " + hud.prepare));
        setText(macroLabel, hud.macroStatus);
        if (hud.readiness == null || hud.readiness.isEmpty()) {
            readyLabel.setForeground(READY);
            setText(readyLabel, "Battle ready");
        } else {
            readyLabel.setForeground(COOLDOWN);
            setText(readyLabel, "<html>Not ready:<br>" + String.join("<br>", hud.readiness) + "</html>");
        }

        if (hud.slotPlan == null || hud.slotPlan.isEmpty()) {
            if (planLabel.isVisible()) planLabel.setVisible(false);
        } else {
            if (!planLabel.isVisible()) planLabel.setVisible(true);
            setText(planLabel, "<html>Slot bar plan (drag these onto your bar):<br>" + String.join("<br>", hud.slotPlan) + "</html>");
        }

        // chips: rebuild only when the set of items changes; cooldown seconds / colours just update in place
        StringBuilder structure = new StringBuilder();
        for (VsMode.Chip c : hud.chips) structure.append(c.label).append(';');
        if (!structure.toString().equals(lastKey)) {
            lastKey = structure.toString();
            chips.removeAll();
            chipLabels.clear();
            for (VsMode.Chip c : hud.chips) {
                JLabel l = chip(c);
                chipLabels.add(l);
                chips.add(l);
            }
            chips.revalidate();
        } else {
            for (int i = 0; i < hud.chips.size() && i < chipLabels.size(); i++) styleChip(chipLabels.get(i), hud.chips.get(i));
        }

        // enemies: add / remove rows only when the count changes
        boolean structural = false;
        while (enemyViews.size() < hud.enemies.size()) {
            EnemyView v = new EnemyView();
            enemyViews.add(v);
            enemies.add(v.row);
            structural = true;
        }
        while (enemyViews.size() > hud.enemies.size()) {
            EnemyView v = enemyViews.remove(enemyViews.size() - 1);
            enemies.remove(v.row);
            structural = true;
        }
        for (int i = 0; i < hud.enemies.size(); i++) enemyViews.get(i).set(hud.enemies.get(i));
        if (structural) {
            enemies.revalidate();
            enemies.repaint();
        }

        String text = String.join(System.lineSeparator(), advice);
        if (tips.isVisible() == text.isEmpty()) tips.setVisible(!text.isEmpty());
        if (!text.equals(tips.getText())) tips.setText(text);
    }

    private static void styleChip(JLabel label, VsMode.Chip c) {
        Color color = c.state == VsMode.ChipState.READY ? READY : c.state == VsMode.ChipState.COOLDOWN ? COOLDOWN : MISSING;
        String detail = c.detail == null || c.detail.isEmpty() ? "" : "  " + c.detail;
        String text = c.label + detail;
        if (!text.equals(label.getText())) label.setText(text);
        if (!color.equals(label.getForeground())) {
            label.setForeground(color);
            label.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 160)),
                    BorderFactory.createEmptyBorder(2, 7, 2, 7)));
        }
    }

    private static JLabel chip(VsMode.Chip c) {
        Color color = c.state == VsMode.ChipState.READY ? READY : c.state == VsMode.ChipState.COOLDOWN ? COOLDOWN : MISSING;
        String detail = c.detail == null || c.detail.isEmpty() ? "" : "  " + c.detail;
        JLabel label = new JLabel(c.label + detail);
        label.setOpaque(true);
        label.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
        label.setForeground(color);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 160)),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)));
        label.putClientProperty("FlatLaf.style", "font: -1 bold");
        return label;
    }

    private static JPanel enemyRow(VsMode.EnemyRow e) {
        JPanel row = new JPanel(new MigLayout("ins 0, fillx, gapx 6", "[110!][grow,fill][grow,fill]", ""));
        row.setOpaque(false);
        JLabel name = new JLabel((e.isTarget ? "> " : "") + e.name + (e.attackingMe ? "  !" : ""));
        name.setForeground(e.isTarget ? new Color(0xF43F5E) : e.attackingMe ? new Color(0xF59E0B) : new Color(0xE8EAF2));
        name.setToolTipText(e.name + "  -  " + e.distance + " away");
        row.add(name);
        row.add(bar(e.lifePercent, "life " + e.lifePercent + "%", new Color(0x22C55E)), "h 14!");
        row.add(bar(e.threatPercent, "threat", new Color(0xF43F5E)), "h 14!");
        return row;
    }

    private static JProgressBar bar(int value, String text, Color color) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(Math.max(0, Math.min(100, value)));
        bar.setStringPainted(true);
        bar.setString(text);
        bar.setForeground(color);
        bar.putClientProperty("FlatLaf.style", "arc: 6; font: -2");
        return bar;
    }
}
