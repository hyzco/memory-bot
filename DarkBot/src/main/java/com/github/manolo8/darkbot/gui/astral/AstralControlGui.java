package com.github.manolo8.darkbot.gui.astral;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.modules.HakClanTutucuModule;
import com.github.manolo8.darkbot.gui.MainGui;
import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Optional;

public class AstralControlGui extends JFrame {
    public static final String ASTRAL_MODULE = "com.deeme.modules.astral.AstralGate";
    public static final String FALLBACK_ASTRAL_MODULE = "com.deemeplus.modules.astral.AstralPlus";
    public static final String TUTUCU_MODULE = HakClanTutucuModule.class.getCanonicalName();

    private final Main main;
    private final JLabel usernameValue = new JLabel();
    private final JLabel configValue = new JLabel();
    private final JLabel moduleValue = new JLabel();
    private final JLabel runningValue = new JLabel();
    private final JLabel moduleStatusValue = new JLabel();
    private final JLabel apiStatusValue = new JLabel();
    private final JLabel sidStatusValue = new JLabel();

    private final JButton startStopButton = new JButton();
    private final JButton toggleDarkBotUiButton = new JButton();
    private final JButton toggleGameWindowButton = new JButton();
    private final JButton astralBotButton = new JButton("Astral Bot");
    private final JButton tutucuButton = new JButton("HAK Clan Tutucu");
    private final JButton tutucuSettingsButton = new JButton("Tutucu Settings");
    private final JButton lockNearestButton = new JButton("Lock Nearest");
    private final JButton pinUserButton = new JButton("Pin User");
    private final JTextField pinnedUserField = new JTextField(12);
    private final DefaultListModel<String> nearestEnemiesModel = new DefaultListModel<>();
    private final JList<String> nearestEnemiesList = new JList<>(nearestEnemiesModel);

    private final Timer refreshTimer;
    private boolean gameWindowVisible;

    public AstralControlGui(Main main) {
        super("HAK Clan Bot");
        this.main = main;
        this.gameWindowVisible = Main.API.isInitiallyShown() && !main.params.getAutoHide();

        setIconImage(MainGui.ICON);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(main.config.BOT_SETTINGS.BOT_GUI.ALWAYS_ON_TOP);
        setLayout(new MigLayout("wrap 2, ins 16 20 16 20, fillx, gapy 6", "[right]16[grow,fill]", ""));

        // Style the info labels
        java.awt.Font valueFont = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
        java.awt.Font labelFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);
        java.awt.Color dimLabel = new java.awt.Color(0x90A4AE);
        java.awt.Color brightValue = new java.awt.Color(0xECEFF1);

        JLabel[] labels = {usernameValue, configValue, moduleValue, runningValue, moduleStatusValue, apiStatusValue, sidStatusValue};
        for (JLabel lbl : labels) {
            lbl.setFont(valueFont);
            lbl.setForeground(brightValue);
        }

        addRow("Pilot", usernameValue, labelFont, dimLabel);
        addRow("Config", configValue, labelFont, dimLabel);
        addRow("Module", moduleValue, labelFont, dimLabel);
        addRow("Bot", runningValue, labelFont, dimLabel);
        addRow("Status", moduleStatusValue, labelFont, dimLabel);
        addRow("Game", apiStatusValue, labelFont, dimLabel);
        addRow("Session", sidStatusValue, labelFont, dimLabel);

        // Separator
        javax.swing.JSeparator sep = new javax.swing.JSeparator();
        sep.setForeground(new java.awt.Color(0x37474F));
        add(sep, "span 2, growx, gaptop 4, gapbottom 4");

        JPanel buttons = new JPanel(new MigLayout("ins 0, fillx, gapy 6", "[grow,fill][grow,fill]", ""));

        startStopButton.addActionListener(e -> toggleRunning());
        toggleDarkBotUiButton.addActionListener(e -> toggleDarkBotUi());
        toggleGameWindowButton.addActionListener(e -> toggleGameWindow());
        astralBotButton.addActionListener(e -> selectModule(resolveAstralModuleId().orElse(null), "Astral module is not loaded"));
        tutucuButton.addActionListener(e -> selectModule(resolveTutucuModuleId().orElse(null), "HAK Clan Tutucu is not loaded"));
        tutucuSettingsButton.addActionListener(e -> openTutucuSettings());
        lockNearestButton.addActionListener(e -> requestTutucuLock());
        pinUserButton.addActionListener(e -> pinTutucuUser());
        nearestEnemiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nearestEnemiesList.setVisibleRowCount(5);
        nearestEnemiesList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            String selected = nearestEnemiesList.getSelectedValue();
            if (selected == null) return;
            pinnedUserField.setText(selected);
            pinTutucuUser();
        });

        JButton configButton = new JButton("Open Config");
        configButton.addActionListener(e -> openConfig());

        JButton exitButton = new JButton("Exit");
        exitButton.setForeground(new java.awt.Color(0xEF5350));
        exitButton.addActionListener(e -> shutdown());

        buttons.add(startStopButton);
        buttons.add(toggleDarkBotUiButton, "wrap");
        buttons.add(astralBotButton);
        buttons.add(tutucuButton, "wrap");
        buttons.add(lockNearestButton);
        buttons.add(tutucuSettingsButton, "wrap");
        buttons.add(pinUserButton);
        buttons.add(pinnedUserField, "wrap");
        buttons.add(toggleGameWindowButton, "wrap");
        buttons.add(configButton, "wrap");
        buttons.add(exitButton, "span 2");

        JPanel enemyPanel = new JPanel(new MigLayout("wrap 1, ins 6", "[grow,fill]", ""));
        enemyPanel.add(new JLabel("Nearest enemies:"));
        enemyPanel.add(new JScrollPane(nearestEnemiesList), "growx, h 90::140");
        add(enemyPanel, "span 2, growx");

        add(buttons, "span 2, growx");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });

        ensureAstralModuleSelected();
        refreshStatus();

        refreshTimer = new Timer(500, e -> refreshStatus());
        refreshTimer.start();

        pack();
        setLocationByPlatform(true);
    }

    private void addRow(String label, JLabel value, java.awt.Font labelFont, java.awt.Color labelColor) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(labelFont);
        lbl.setForeground(labelColor);
        add(lbl);
        add(value, "growx");
    }

    private void ensureAstralModuleSelected() {
        if (main.config.GENERAL.CURRENT_MODULE == null || main.config.GENERAL.CURRENT_MODULE.isBlank()) {
            resolveAstralModuleId().ifPresent(moduleId -> main.config.GENERAL.CURRENT_MODULE = moduleId);
        }
    }

    private boolean hasAstralModule() {
        return resolveAstralModuleId().isPresent();
    }

    public static Optional<String> resolveAstralModuleId(Main main) {
        if (main.featureRegistry.getFeatureDefinition(ASTRAL_MODULE) != null) return Optional.of(ASTRAL_MODULE);
        if (main.featureRegistry.getFeatureDefinition(FALLBACK_ASTRAL_MODULE) != null) return Optional.of(FALLBACK_ASTRAL_MODULE);

        return main.featureRegistry.getFeatures(eu.darkbot.api.extensions.Module.class)
                .map(fd -> fd.getId())
                .filter(id -> id != null && id.toLowerCase().contains("astral"))
                .findFirst();
    }

    private Optional<String> resolveAstralModuleId() {
        return resolveAstralModuleId(main);
    }

    private Optional<String> resolveTutucuModuleId() {
        if (main.featureRegistry.getFeatureDefinition(TUTUCU_MODULE) != null) return Optional.of(TUTUCU_MODULE);
        return main.featureRegistry.getFeatures(eu.darkbot.api.extensions.Module.class)
                .map(fd -> fd.getId())
                .filter(id -> id != null && id.toLowerCase().contains("tutucu"))
                .findFirst();
    }

    private void selectModule(String moduleId, String missingMessage) {
        if (moduleId == null) {
            JOptionPane.showMessageDialog(this, missingMessage, "Module Missing", JOptionPane.ERROR_MESSAGE);
            return;
        }

        main.config.GENERAL.CURRENT_MODULE = moduleId;
        refreshStatus();
    }

    private void toggleRunning() {
        if (!main.isRunning()) {
            if (!hasAstralModule()) {
                JOptionPane.showMessageDialog(this,
                        "Astral module is not loaded. Ensure AstralPlugin.jar is in the plugins folder.",
                        "Astral Module Missing",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            ensureAstralModuleSelected();
        }

        main.setRunning(!main.isRunning());
        refreshStatus();
    }

    private void toggleDarkBotUi() {
        boolean show = main.getGui().isHidden();
        main.getGui().setVisible(show);
        if (show) {
            main.getGui().setState(JFrame.NORMAL);
            main.getGui().toFront();
            main.getGui().requestFocus();
        } else {
            main.getGui().getConfigGui().setVisible(false);
        }
        refreshStatus();
    }

    private void toggleGameWindow() {
        gameWindowVisible = !gameWindowVisible;
        Main.API.setVisible(gameWindowVisible, main.config.BOT_SETTINGS.API_CONFIG.FULLY_HIDE_API);
        refreshStatus();
    }

    private void openConfig() {
        if (main.getGui().isHidden()) {
            main.getGui().setVisible(true);
            main.getGui().setState(JFrame.NORMAL);
        }
        if (!main.getGui().getConfigGui().isVisible()) {
            main.getGui().toggleConfig();
        } else {
            main.getGui().getConfigGui().toFront();
        }
        refreshStatus();
    }

    private void refreshStatus() {
        startStopButton.setText(main.isRunning() ? "Stop Bot" : "Start Bot");
        toggleDarkBotUiButton.setText(main.getGui().isHidden() ? "Show Main UI" : "Hide Main UI");
        toggleGameWindowButton.setText(gameWindowVisible ? "Hide Game Window" : "Show Game Window");

        usernameValue.setText(orFallback(main.hero.playerInfo.username, "Not available yet"));
        configValue.setText(orFallback(main.configManager.getConfigName(), "config"));
        moduleValue.setText(resolveModuleLabel(main.config.GENERAL.CURRENT_MODULE));
        runningValue.setText(main.isRunning() ? "Running" : "Stopped");
        apiStatusValue.setText(Main.API.isValid()
                ? (gameWindowVisible ? "Connected and visible" : "Connected and hidden")
                : "Loading");
        sidStatusValue.setText(orFallback(main.backpage.sidStatus(), "Unknown"));

        if (main.getModule() instanceof HakClanTutucuModule) {
            HakClanTutucuModule tutucu = (HakClanTutucuModule) main.getModule();
            String pinned = tutucu.getPinnedEnemyName();
            pinnedUserField.setText(pinned == null ? "" : pinned);
            List<String> enemyNames = tutucu.getNearestEnemyNames();
            nearestEnemiesModel.clear();
            if (enemyNames.isEmpty()) {
                nearestEnemiesModel.addElement("No enemies nearby");
                nearestEnemiesList.clearSelection();
            } else {
                for (String enemyName : enemyNames) {
                    nearestEnemiesModel.addElement(enemyName);
                }
                if (pinned != null && !pinned.isBlank()) {
                    nearestEnemiesList.setSelectedValue(pinned, false);
                } else {
                    nearestEnemiesList.clearSelection();
                }
            }
        } else {
            pinnedUserField.setText("");
            nearestEnemiesModel.clear();
            nearestEnemiesModel.addElement("HAK Clan Tutucu not active");
            nearestEnemiesList.clearSelection();
        }

        eu.darkbot.api.extensions.Module module = main.getModule();
        String moduleStatus = module == null
                ? "Waiting for module"
                : (main.isRunning() && main.tickingModule ? module.getStatus() : module.getStoppedStatus());
        moduleStatusValue.setText(orFallback(moduleStatus, main.isRunning() ? "Running" : "Paused"));
    }

    private void shutdown() {
        refreshTimer.stop();
        System.exit(0);
    }

    private String orFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String resolveModuleLabel(String moduleId) {
        if (moduleId == null || moduleId.isBlank()) return "No module selected";
        if (moduleId.equals(TUTUCU_MODULE)) return "HAK Clan Tutucu";
        if (moduleId.equals(ASTRAL_MODULE) || moduleId.equals(FALLBACK_ASTRAL_MODULE) || moduleId.toLowerCase().contains("astral")) {
            return "Astral Bot";
        }
        return moduleId;
    }

    private void openTutucuSettings() {
        var cfg = main.config.GENERAL.HAK_CLAN_TUTUCU;
        JCheckBox enableLocking = new JCheckBox("Enable locking", cfg.ENABLE_LOCKING);
        JCheckBox autoAttack = new JCheckBox("Auto attack", cfg.AUTO_ATTACK);
        JCheckBox lockNearest = new JCheckBox("Lock nearest enemy", cfg.LOCK_ON_NEAREST);
        JSpinner maxRange = new JSpinner(new SpinnerNumberModel(cfg.MAX_LOCK_DISTANCE, 200, 3000, 50));

        JButton selectKeyButton = new JButton(cfg.SELECTOR_KEY == null || cfg.SELECTOR_KEY.isBlank() ? "Select key" : cfg.SELECTOR_KEY);
        final int[] selectedKeyCode = {-1};
        final String[] selectedKeyText = {cfg.SELECTOR_KEY == null ? "" : cfg.SELECTOR_KEY};

        JPanel panel = new JPanel(new MigLayout("wrap 2, ins 10", "[right]10[grow,fill]", "[]8[]8[]8[]"));
        panel.add(new JLabel("Enable locking")); panel.add(enableLocking);
        panel.add(new JLabel("Auto attack")); panel.add(autoAttack);
        panel.add(new JLabel("Lock nearest")); panel.add(lockNearest);
        panel.add(new JLabel("Max range")); panel.add(maxRange);
        panel.add(new JLabel("Selector key")); panel.add(selectKeyButton);

        JDialog dialog = new JDialog(this, "HAK Clan Tutucu settings", true);
        dialog.setLayout(new MigLayout("wrap 1, ins 12", "[grow,fill]", ""));
        dialog.add(panel);

        selectKeyButton.addActionListener(e -> {
            selectKeyButton.setEnabled(false);
            selectKeyButton.setText("Press any key...");
            dialog.getRootPane().setDefaultButton(null);
            dialog.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    int keyCode = event.getKeyCode();
                    String keyText = resolveKeyDisplayText(keyCode, event.getKeyChar());
                    selectedKeyCode[0] = keyCode;
                    selectedKeyText[0] = keyText;
                    selectKeyButton.setText(keyText);
                    selectKeyButton.setEnabled(true);
                    dialog.removeKeyListener(this);
                    dialog.getRootPane().setDefaultButton(null);
                }
            });
            dialog.requestFocus();
        });

        JPanel actions = new JPanel(new MigLayout("ins 0", "[grow,fill][grow,fill]", ""));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        ok.addActionListener(e -> {
            cfg.ENABLE_LOCKING = enableLocking.isSelected();
            cfg.AUTO_ATTACK = autoAttack.isSelected();
            cfg.LOCK_ON_NEAREST = lockNearest.isSelected();
            cfg.MAX_LOCK_DISTANCE = (int) maxRange.getValue();

            cfg.SELECTOR_KEY = selectedKeyCode[0] >= 0 ? selectedKeyText[0] : (cfg.SELECTOR_KEY == null ? "L" : cfg.SELECTOR_KEY);
            dialog.dispose();
        });
        cancel.addActionListener(e -> dialog.dispose());
        actions.add(ok); actions.add(cancel);
        dialog.add(actions);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private String resolveKeyDisplayText(int keyCode, char keyChar) {
        switch (keyCode) {
            case KeyEvent.VK_SPACE: return "SPACE";
            case KeyEvent.VK_TAB: return "TAB";
            case KeyEvent.VK_ENTER: return "ENTER";
            case KeyEvent.VK_ESCAPE: return "ESC";
            case KeyEvent.VK_BACK_SPACE: return "BACKSPACE";
            case KeyEvent.VK_DELETE: return "DELETE";
            case KeyEvent.VK_UP: return "UP";
            case KeyEvent.VK_DOWN: return "DOWN";
            case KeyEvent.VK_LEFT: return "LEFT";
            case KeyEvent.VK_RIGHT: return "RIGHT";
            case KeyEvent.VK_SHIFT: return "SHIFT";
            case KeyEvent.VK_CONTROL: return "CTRL";
            case KeyEvent.VK_ALT: return "ALT";
            case KeyEvent.VK_CAPS_LOCK: return "CAPSLOCK";
            case KeyEvent.VK_PAUSE: return "PAUSE";
            case KeyEvent.VK_PRINTSCREEN: return "PRINTSCREEN";
            case KeyEvent.VK_INSERT: return "INSERT";
            case KeyEvent.VK_HOME: return "HOME";
            case KeyEvent.VK_END: return "END";
            case KeyEvent.VK_PAGE_UP: return "PAGEUP";
            case KeyEvent.VK_PAGE_DOWN: return "PAGEDOWN";
            default:
                if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12) return "F" + (keyCode - KeyEvent.VK_F1 + 1);
                if (keyChar != KeyEvent.CHAR_UNDEFINED && !Character.isISOControl(keyChar)) return String.valueOf(Character.toUpperCase(keyChar));
                return String.valueOf(keyCode);
        }
    }

    private void pinTutucuUser() {
        if (!(main.getModule() instanceof HakClanTutucuModule)) {
            JOptionPane.showMessageDialog(this,
                    "Select HAK Clan Tutucu module first.",
                    "Tutucu Not Active",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        HakClanTutucuModule tutucu = (HakClanTutucuModule) main.getModule();
        String name = pinnedUserField.getText() == null ? "" : pinnedUserField.getText().trim();
        tutucu.setPinnedEnemyName(name);
        if (main.isRunning() && !name.isEmpty()) {
            tutucu.requestToggleLock();
        }
        refreshStatus();
    }

    private void requestTutucuLock() {
        if (!main.isRunning()) {
            JOptionPane.showMessageDialog(this,
                    "Start bot first. Tutucu lock only runs while bot active.",
                    "Tutucu Locked",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!(main.getModule() instanceof HakClanTutucuModule)) {
            JOptionPane.showMessageDialog(this,
                    "Select HAK Clan Tutucu module first.",
                    "Tutucu Not Active",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ((HakClanTutucuModule) main.getModule()).requestToggleLock();
        refreshStatus();
    }

    public void requestTutucuLockFromKeyboard() {
        requestTutucuLock();
    }
}