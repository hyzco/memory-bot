package com.deeme.modules.astral;

import eu.darkbot.api.PluginAPI;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.extensions.Behavior;
import eu.darkbot.api.extensions.Configurable;
import eu.darkbot.api.extensions.ExtraMenus;
import eu.darkbot.api.extensions.Feature;
import eu.darkbot.api.game.entities.Pet;
import eu.darkbot.api.game.entities.Ship;
import eu.darkbot.api.game.other.Lockable;
import eu.darkbot.api.managers.AttackAPI;
import eu.darkbot.api.managers.EntitiesAPI;
import eu.darkbot.api.managers.HeroAPI;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Feature(name = "Tutucu Nearest Ship Locker", description = "Tutucu-style compact panel to lock the nearest ship")
public class NearestShipLocker implements Behavior, Configurable<NearestShipLockerConfig>, ExtraMenus {
    private final HeroAPI hero;
    private final AttackAPI attack;
    private final EntitiesAPI entities;

    private volatile boolean enabled;
    private volatile boolean enemyOnly;
    private volatile boolean ignoreInvisible;
    private volatile boolean includePets;
    private volatile boolean autoLock;
    private volatile boolean stickyTarget;
    private volatile boolean clearWhenEmpty;
    private volatile int maxRange;
    private volatile int refreshMs;

    private volatile String status = "Idle";
    private volatile String targetText = "None";

    private volatile long nextScanAt;

    private JFrame panel;
    private JLabel statusLabel;
    private JLabel targetLabel;
    private JToggleButton enabledToggle;

    public NearestShipLocker(PluginAPI api) {
        this.hero = api.requireAPI(HeroAPI.class);
        this.attack = api.requireAPI(AttackAPI.class);
        this.entities = api.requireAPI(EntitiesAPI.class);
        applyConfig(new NearestShipLockerConfig());
    }

    @Override
    public void setConfig(ConfigSetting<NearestShipLockerConfig> configSetting) {
        NearestShipLockerConfig cfg = configSetting.getValue();
        if (cfg == null) cfg = new NearestShipLockerConfig();
        applyConfig(cfg);
        refreshPanelFromState();
    }

    @Override
    public void onTickBehavior() {
        if (!enabled) {
            status = "Disabled";
            return;
        }

        long now = System.currentTimeMillis();
        if (now < nextScanAt) return;
        nextScanAt = now + Math.max(50, refreshMs);

        Ship current = attack.getTargetAs(Ship.class);
        Ship target = null;

        if (stickyTarget && isCandidateValid(current)) {
            target = current;
        } else {
            target = findNearestShip();
        }

        if (target == null) {
            targetText = "None";
            status = "No valid ship target";
            if (clearWhenEmpty) {
                attack.setTarget(null);
                hero.setLocalTarget(null);
            }
            return;
        }

        Lockable currentTarget = attack.getTarget();
        if (currentTarget != target) {
            attack.setTarget(target);
            hero.setLocalTarget(target);
        }

        if (autoLock) attack.tryLockTarget();

        double dist = hero.distanceTo(target);
        targetText = labelFor(target, dist);
        status = autoLock ? "Targeting + locking nearest ship" : "Targeting nearest ship";
    }

    @Override
    public void onStoppedBehavior() {
        if (!enabled) return;
        status = "Waiting (bot stopped/loading)";
    }

    @Override
    public Collection<JComponent> getExtraMenuItems(PluginAPI api) {
        List<JComponent> items = new ArrayList<>();
        JMenuItem openPanel = create("Tutucu Ship Locker", e -> showPanel());
        items.add(openPanel);
        return items;
    }

    @Override
    public boolean autoSubmenu() {
        return true;
    }

    private Ship findNearestShip() {
        Ship best = null;
        double bestDist = Double.MAX_VALUE;

        for (Ship ship : entities.getShips()) {
            if (!isCandidateValid(ship)) continue;

            double dist = hero.distanceTo(ship);
            if (maxRange > 0 && dist > maxRange) continue;

            if (dist < bestDist) {
                bestDist = dist;
                best = ship;
            }
        }

        return best;
    }

    private boolean isCandidateValid(Ship ship) {
        if (ship == null) return false;
        if (!ship.isValid() || !ship.isSelectable()) return false;
        if (ship.isBlacklisted()) return false;
        if (!includePets && ship instanceof Pet) return false;
        if (ignoreInvisible && ship.isInvisible()) return false;
        return !enemyOnly || ship.getEntityInfo().isEnemy();
    }

    private String labelFor(Ship ship, double dist) {
        String username = ship.getEntityInfo().getUsername();
        if (username == null || username.isEmpty()) username = "Unknown";
        return username + " #" + ship.getId() + " (" + (int) Math.round(dist) + ")";
    }

    private void applyConfig(NearestShipLockerConfig cfg) {
        this.enabled = cfg.enabled;
        this.enemyOnly = cfg.enemyOnly;
        this.ignoreInvisible = cfg.ignoreInvisible;
        this.includePets = cfg.includePets;
        this.autoLock = cfg.autoLock;
        this.stickyTarget = cfg.stickyTarget;
        this.clearWhenEmpty = cfg.clearWhenEmpty;
        this.maxRange = cfg.maxRange;
        this.refreshMs = cfg.refreshMs;
    }

    private void showPanel() {
        SwingUtilities.invokeLater(() -> {
            if (panel == null) panel = buildPanel();
            panel.setVisible(true);
            panel.toFront();
            panel.requestFocus();
        });
    }

    private JFrame buildPanel() {
        JFrame frame = new JFrame("Tutucu Ship Locker");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(420, 300));

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(new Color(22, 22, 26));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(22, 22, 26));

        JLabel title = new JLabel("TUTUCU LOCK PANEL");
        title.setForeground(new Color(100, 220, 255));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        enabledToggle = new JToggleButton();
        enabledToggle.setFocusPainted(false);
        enabledToggle.addActionListener(e -> {
            this.enabled = enabledToggle.isSelected();
            syncToggleLabel();
        });
        syncToggleLabel();

        header.add(title, BorderLayout.WEST);
        header.add(enabledToggle, BorderLayout.EAST);

        JPanel controls = new JPanel(new GridLayout(0, 2, 6, 6));
        controls.setBackground(new Color(30, 30, 36));

        JCheckBox enemyOnlyCheck = createCheck("Enemy only", enemyOnly, v -> enemyOnly = v);
        JCheckBox invisCheck = createCheck("Ignore invisible", ignoreInvisible, v -> ignoreInvisible = v);
        JCheckBox petsCheck = createCheck("Include pets", includePets, v -> includePets = v);
        JCheckBox lockCheck = createCheck("Auto lock", autoLock, v -> autoLock = v);
        JCheckBox stickyCheck = createCheck("Sticky target", stickyTarget, v -> stickyTarget = v);
        JCheckBox clearCheck = createCheck("Clear when none", clearWhenEmpty, v -> clearWhenEmpty = v);

        controls.add(enemyOnlyCheck);
        controls.add(invisCheck);
        controls.add(petsCheck);
        controls.add(lockCheck);
        controls.add(stickyCheck);
        controls.add(clearCheck);

        JPanel numbers = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        numbers.setBackground(new Color(30, 30, 36));

        numbers.add(createLabel("Max range"));
        JSpinner rangeSpinner = new JSpinner(new SpinnerNumberModel(maxRange, 0, 6000, 50));
        rangeSpinner.addChangeListener(e -> maxRange = (Integer) rangeSpinner.getValue());
        numbers.add(rangeSpinner);

        numbers.add(new JSeparator(JSeparator.VERTICAL));
        numbers.add(createLabel("Refresh ms"));
        JSpinner refreshSpinner = new JSpinner(new SpinnerNumberModel(refreshMs, 50, 2000, 25));
        refreshSpinner.addChangeListener(e -> refreshMs = (Integer) refreshSpinner.getValue());
        numbers.add(refreshSpinner);

        JPanel footer = new JPanel(new GridLayout(0, 1));
        footer.setBackground(new Color(22, 22, 26));
        statusLabel = createLabel("Status: " + status);
        targetLabel = createLabel("Target: " + targetText);
        footer.add(statusLabel);
        footer.add(targetLabel);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(22, 22, 26));
        bottom.add(numbers, BorderLayout.NORTH);
        bottom.add(footer, BorderLayout.SOUTH);

        root.add(header, BorderLayout.NORTH);
        root.add(controls, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        frame.setContentPane(root);

        Timer timer = new Timer(250, e -> {
            if (statusLabel != null) statusLabel.setText("Status: " + status);
            if (targetLabel != null) targetLabel.setText("Target: " + targetText);
            if (enabledToggle != null && enabledToggle.isSelected() != enabled) {
                enabledToggle.setSelected(enabled);
                syncToggleLabel();
            }
        });
        timer.start();

        frame.pack();
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private void syncToggleLabel() {
        if (enabledToggle == null) return;
        enabledToggle.setSelected(enabled);
        enabledToggle.setText(enabled ? "LOCKER ON" : "LOCKER OFF");
        enabledToggle.setBackground(enabled ? new Color(36, 130, 74) : new Color(140, 45, 45));
        enabledToggle.setForeground(Color.WHITE);
    }

    private void refreshPanelFromState() {
        SwingUtilities.invokeLater(() -> {
            syncToggleLabel();
            if (statusLabel != null) statusLabel.setText("Status: " + status);
            if (targetLabel != null) targetLabel.setText("Target: " + targetText);
        });
    }

    private JCheckBox createCheck(String text, boolean value, BoolConsumer consumer) {
        JCheckBox check = new JCheckBox(text, value);
        check.setOpaque(false);
        check.setForeground(new Color(220, 220, 225));
        check.addActionListener(e -> consumer.accept(check.isSelected()));
        return check;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(220, 220, 225));
        return label;
    }

    @FunctionalInterface
    private interface BoolConsumer {
        void accept(boolean value);
    }
}
