package com.github.manolo8.darkbot.gui.astral;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.modules.HakClanTutucuModule;
import com.github.manolo8.darkbot.gui.MainGui;
import com.github.manolo8.darkbot.gui.utils.window.NativeWindowEmbedder;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.JTabbedPane;
import java.awt.Canvas;
import java.awt.Color;
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

    private static volatile AstralControlGui INSTANCE;

    public static AstralControlGui getInstance() {
        return INSTANCE;
    }

    private final Main main;
    private final Canvas gameCanvas = new Canvas();
    private final JLabel usernameValue = new JLabel();
    private final JLabel moduleValue = new JLabel();
    private final JLabel moduleStatusValue = new JLabel();
    private final JLabel sidStatusValue = new JLabel();
    private final Chip runningChip = new Chip();
    private final Chip gameChip = new Chip();
    private final JPanel tutucuPanel = new JPanel();
    private boolean updatingUi;
    private final javax.swing.JToggleButton targetPlayersButton = new javax.swing.JToggleButton("Players");
    private final javax.swing.JToggleButton targetNpcsButton = new javax.swing.JToggleButton("NPCs");
    private final javax.swing.JToggleButton targetBothButton = new javax.swing.JToggleButton("Both");
    private final JCheckBox autoBattleBox = new JCheckBox("Auto battle");
    private final JCheckBox vsModeBox = new JCheckBox("VS mode (PvP: focus threat / low life, macros)");
    private final VsHudPanel vsHud = new VsHudPanel(this::prepareForBattle, this::openVsStrategy,
            this::setPilotMode, this::doPilotSuggestion);
    private final JCheckBox autoNextBox = new JCheckBox("Auto next target");

    private final JButton startStopButton = new JButton();
    private final JButton toggleGameWindowButton = new JButton();
    private final javax.swing.JToggleButton astralBotButton = new javax.swing.JToggleButton("Astral Bot");
    private final javax.swing.JToggleButton tutucuButton = new javax.swing.JToggleButton("HAK Tutucu");
    private final JButton tutucuSettingsButton = new JButton("Settings");
    private final JButton lockNearestButton = new JButton("Lock nearest");
    private final JButton unlockButton = new JButton("Unlock");
    private final JButton pinUserButton = new JButton("Pin");
    private final JButton pinSelectedButton = new JButton("Pin selected player");
    private final JButton pinnedPetButton = new JButton("Target pinned player's pet");
    private final JLabel profileLabel = new JLabel(" ");
    private final JCheckBox lockFriendsBox = new JCheckBox("Lock friendly players too");
    private final JCheckBox autoPinBox = new JCheckBox("Pin whoever I select by hand");
    private final JTextField pinnedUserField = new JTextField(12);
    private final DefaultListModel<String> nearestEnemiesModel = new DefaultListModel<>();
    private final JList<String> nearestEnemiesList = new JList<>(nearestEnemiesModel);

    private final Timer refreshTimer;
    private boolean gameWindowVisible;

    private JTabbedPane mainTabs;
    private int settingsTabIndex;

    public AstralControlGui(Main main) {
        super("HAK Clan Bot");
        INSTANCE = this;
        this.main = main;
        this.gameWindowVisible = Main.API.isInitiallyShown() && !main.params.getAutoHide();

        setIconImage(MainGui.ICON);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(main.config.BOT_SETTINGS.BOT_GUI.ALWAYS_ON_TOP);
        getRootPane().putClientProperty(com.formdev.flatlaf.FlatClientProperties.TITLE_BAR_BACKGROUND, new java.awt.Color(0x0D0F15));
        getRootPane().putClientProperty(com.formdev.flatlaf.FlatClientProperties.TITLE_BAR_FOREGROUND, new java.awt.Color(0xE8EAF2));
        setLayout(new java.awt.BorderLayout());

        gameCanvas.setBackground(Color.BLACK);
        gameCanvas.setPreferredSize(new java.awt.Dimension(900, 700));
        gameCanvas.setMinimumSize(new java.awt.Dimension(320, 240));
        // Always start visible: MigLayout collapses invisible components to zero size,
        // which leaves the Canvas without a native peer for NativeWindowEmbedder to reparent into.
        gameCanvas.setVisible(true);
        this.gameWindowVisible = true;

        JPanel sidebar = buildSidebar();

        JPanel dashboard = new JPanel(new java.awt.BorderLayout());
        // Left column: controls on top, the bot's own live map below, divider draggable by hand.
        javax.swing.JScrollPane sidebarScroll = new javax.swing.JScrollPane(sidebar,
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setBorder(null);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
        sidebarScroll.setMinimumSize(new java.awt.Dimension(340, 200));

        javax.swing.JComponent botMap = main.getGui().getMapDrawer();
        botMap.setPreferredSize(new java.awt.Dimension(340, 320));
        botMap.setMinimumSize(new java.awt.Dimension(200, 150));

        javax.swing.JSplitPane leftColumn = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT, sidebarScroll, botMap);
        leftColumn.setResizeWeight(0.55);
        leftColumn.setContinuousLayout(true);
        leftColumn.setOneTouchExpandable(true);
        leftColumn.setDividerSize(8);
        leftColumn.setPreferredSize(new java.awt.Dimension(360, 700));

        javax.swing.JSplitPane dashboardSplit = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT, leftColumn, gameCanvas);
        dashboardSplit.setResizeWeight(0.0);
        dashboardSplit.setContinuousLayout(true);
        dashboardSplit.setDividerSize(8);
        dashboard.add(dashboardSplit, java.awt.BorderLayout.CENTER);

        mainTabs = new JTabbedPane();
        int dashboardTabIndex = mainTabs.getTabCount();
        mainTabs.addTab("Dashboard", dashboard);
        settingsTabIndex = mainTabs.getTabCount();
        mainTabs.addTab("Settings", main.getGui().getConfigGui().getEmbeddableContent());

        // The embedded game window is a raw native window, not a real Swing component - it doesn't
        // auto-hide when its tab is switched away from, so we have to do that ourselves.
        mainTabs.addChangeListener(e ->
                NativeWindowEmbedder.setEmbeddedVisible(mainTabs.getSelectedIndex() == dashboardTabIndex));

        add(mainTabs, java.awt.BorderLayout.CENTER);

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
        setMinimumSize(new java.awt.Dimension(640, 480));
        setLocationByPlatform(true);
    }

    private static final Color DIM = new Color(0x8A90A8);
    private static final Color ACCENT = new Color(0x8B5CF6);
    private static final Color OK = new Color(0x10B981);
    private static final Color DANGER = new Color(0xF43F5E);
    private static final Color WARN = new Color(0xF59E0B);
    private static final Color INFO = new Color(0x38BDF8);
    private static final Color IDLE = new Color(0x3B4157);
    private static final Color CARD_BORDER = new Color(0x2A2F42);

    private static void style(javax.swing.JComponent c, String css) {
        c.putClientProperty("FlatLaf.style", css);
    }

    private static void cardStyle(JPanel p) {
        p.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new com.formdev.flatlaf.ui.FlatLineBorder(new java.awt.Insets(0, 0, 0, 0), CARD_BORDER, 1f, 14),
                javax.swing.BorderFactory.createEmptyBorder(12, 14, 12, 14)));
    }

    private static JLabel caption(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(DIM);
        style(l, "font: -2");
        return l;
    }

    private JPanel buildSidebar() {
        JPanel root = new JPanel(new MigLayout("wrap 1, fillx, ins 14, gapy 10", "[grow,fill]", ""));

        // --- Brand header ---
        JLabel brandMark = new JLabel("HAK");
        brandMark.setForeground(ACCENT);
        brandMark.setFont(brandMark.getFont().deriveFont(java.awt.Font.BOLD, 22f));
        JLabel brandName = new JLabel("CLAN BOT");
        brandName.setForeground(DIM);
        brandName.setFont(brandName.getFont().deriveFont(java.awt.Font.BOLD, 12f));
        JPanel brand = new JPanel(new MigLayout("ins 0, gapx 8", "", "[bottom]"));
        brand.setOpaque(false);
        brand.add(brandMark);
        brand.add(brandName, "gapbottom 3");
        root.add(brand, "gapbottom 2");

        // --- Pilot card ---
        usernameValue.setFont(usernameValue.getFont().deriveFont(java.awt.Font.BOLD, 20f));
        moduleValue.setForeground(DIM);
        JPanel pilot = new JPanel(new MigLayout("wrap 1, fillx, ins 0, gapy 4", "[grow,fill]", ""));
        cardStyle(pilot);
        pilot.add(usernameValue);
        pilot.add(moduleValue);
        JPanel chips = new JPanel(new MigLayout("ins 0, gapx 6", "", ""));
        chips.setOpaque(false);
        chips.add(runningChip);
        chips.add(gameChip);
        pilot.add(chips, "gaptop 6");
        root.add(pilot);

        // --- Start / Stop ---
        startStopButton.addActionListener(e -> toggleRunning());
        root.add(startStopButton, "h 46!");

        // --- Live status ---
        JPanel status = new JPanel(new MigLayout("wrap 1, fillx, ins 0, gapy 2", "[grow,fill]", ""));
        cardStyle(status);
        moduleStatusValue.setFont(moduleStatusValue.getFont().deriveFont(13f));
        sidStatusValue.setForeground(DIM);
        status.add(caption("STATUS"));
        status.add(moduleStatusValue);
        status.add(sidStatusValue, "gaptop 4");
        root.add(status);

        // --- Module switch ---
        root.add(caption("MODULE"), "gaptop 4");
        javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
        group.add(astralBotButton);
        group.add(tutucuButton);
        JPanel moduleRow = new JPanel(new MigLayout("ins 0, fillx, gapx 8", "[grow,fill][grow,fill]", ""));
        astralBotButton.addActionListener(e -> {
            if (!updatingUi) selectModule(resolveAstralModuleId().orElse(null), "Astral module is not loaded");
        });
        tutucuButton.addActionListener(e -> {
            if (!updatingUi) selectModule(resolveTutucuModuleId().orElse(null), "HAK Clan Tutucu is not loaded");
        });
        moduleRow.add(astralBotButton, "h 36!");
        moduleRow.add(tutucuButton, "h 36!");
        root.add(moduleRow);

        // --- Tutucu panel (only visible when the Tutucu module is active) ---
        tutucuPanel.setLayout(new MigLayout("wrap 2, fillx, ins 0, gapy 8, gapx 8", "[grow,fill][grow,fill]", ""));
        cardStyle(tutucuPanel);
        nearestEnemiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nearestEnemiesList.setVisibleRowCount(5);
        nearestEnemiesList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || updatingUi) return;
            String selected = nearestEnemiesList.getSelectedValue();
            if (selected == null || selected.startsWith("No enemies")) return;
            int sep = selected.lastIndexOf("   -   ");
            String picked = sep > 0 ? selected.substring(0, sep) : selected;
            if (picked.startsWith("[NPC] ")) return; // NPCs can't be pinned by name; they are picked by distance
            pinnedUserField.setText(picked);
            pinTutucuUser();
        });
        lockNearestButton.addActionListener(e -> requestTutucuLock());
        unlockButton.addActionListener(e -> requestTutucuRelease());
        tutucuSettingsButton.addActionListener(e -> openTutucuSettings());
        pinUserButton.addActionListener(e -> pinTutucuUser());
        pinSelectedButton.addActionListener(e -> pinSelectedPlayer());
        pinnedPetButton.addActionListener(e -> {
            if (main.getModule() instanceof HakClanTutucuModule) ((HakClanTutucuModule) main.getModule()).requestPinnedPetTarget();
        });
        lockFriendsBox.addActionListener(e -> {
            if (!updatingUi) main.config.GENERAL.HAK_CLAN_TUTUCU.LOCK_FRIENDS = lockFriendsBox.isSelected();
        });
        autoPinBox.addActionListener(e -> {
            if (!updatingUi) main.config.GENERAL.HAK_CLAN_TUTUCU.AUTO_PIN_MANUAL = autoPinBox.isSelected();
        });
        pinnedUserField.putClientProperty("JTextField.placeholderText", "Pin a player by name");
        tutucuPanel.add(caption("TARGET"), "span 2");
        javax.swing.ButtonGroup targetGroup = new javax.swing.ButtonGroup();
        targetGroup.add(targetPlayersButton);
        targetGroup.add(targetNpcsButton);
        targetGroup.add(targetBothButton);
        JPanel targetRow = new JPanel(new MigLayout("ins 0, fillx, gapx 6", "[grow,fill][grow,fill][grow,fill]", ""));
        targetRow.add(targetPlayersButton, "h 30!");
        targetRow.add(targetNpcsButton, "h 30!");
        targetRow.add(targetBothButton, "h 30!");
        tutucuPanel.add(targetRow, "span 2, growx");
        targetPlayersButton.addActionListener(e -> setTutucuTarget(com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.TargetType.PLAYERS));
        targetNpcsButton.addActionListener(e -> setTutucuTarget(com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.TargetType.NPCS));
        targetBothButton.addActionListener(e -> setTutucuTarget(com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.TargetType.BOTH));
        autoBattleBox.addActionListener(e -> {
            if (!updatingUi) main.config.GENERAL.HAK_CLAN_TUTUCU.AUTO_ATTACK = autoBattleBox.isSelected();
        });
        autoNextBox.addActionListener(e -> {
            if (!updatingUi) main.config.GENERAL.HAK_CLAN_TUTUCU.AUTO_NEXT_TARGET = autoNextBox.isSelected();
        });
        vsModeBox.addActionListener(e -> {
            if (!updatingUi) main.config.GENERAL.HAK_CLAN_TUTUCU.VS_MODE = vsModeBox.isSelected();
        });
        tutucuPanel.add(autoBattleBox, "span 2");
        tutucuPanel.add(autoNextBox, "span 2");
        tutucuPanel.add(vsModeBox, "span 2");
        tutucuPanel.add(vsHud, "span 2, growx, hidemode 3");
        style(targetPlayersButton, "arc: 10");
        style(targetNpcsButton, "arc: 10");
        style(targetBothButton, "arc: 10");
        tutucuPanel.add(caption("NEAREST TARGETS"), "span 2, gaptop 4");
        tutucuPanel.add(new JScrollPane(nearestEnemiesList), "span 2, growx, h 90::140");
        tutucuPanel.add(lockNearestButton, "h 32!");
        tutucuPanel.add(unlockButton, "h 28!");
        tutucuPanel.add(tutucuSettingsButton, "h 32!");
        tutucuPanel.add(pinnedUserField, "h 32!");
        tutucuPanel.add(pinUserButton, "h 32!");
        tutucuPanel.add(pinSelectedButton, "span 2, growx, h 30!");
        tutucuPanel.add(pinnedPetButton, "span 2, growx, h 30!");
        profileLabel.putClientProperty("FlatLaf.style", "foreground: #C4B5FD; font: -1");
        profileLabel.setVisible(false);
        tutucuPanel.add(profileLabel, "span 2, growx, hidemode 3");
        tutucuPanel.add(lockFriendsBox, "span 2");
        tutucuPanel.add(autoPinBox, "span 2");
        root.add(tutucuPanel);

        // --- Footer ---
        toggleGameWindowButton.addActionListener(e -> toggleGameWindow());
        JButton configButton = new JButton("Settings");
        configButton.addActionListener(e -> openConfig());
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> shutdown());
        style(exitButton, "foreground: #F43F5E; arc: 12");
        style(configButton, "arc: 12");
        style(toggleGameWindowButton, "arc: 12");
        root.add(new javax.swing.JSeparator(), "gaptop 6");
        JButton homeButton = new JButton("Homepage");
        homeButtonRef = homeButton;
        homeButton.addActionListener(e -> openHomepage());
        homeButton.setToolTipText("Open the built-in homepage (hangar, shop, auction...)");
        homeProgress.setVisible(false);
        style(homeButton, "arc: 12");
        JPanel footer = new JPanel(new MigLayout("ins 0, fillx, gapx 8", "[grow,fill][grow,fill][grow,fill][]", ""));
        footer.add(toggleGameWindowButton, "h 34!");
        footer.add(configButton, "h 34!");
        footer.add(homeButton, "h 34!");
        footer.add(exitButton, "h 34!, w 70!");
        root.add(footer);
        root.add(homeProgress, "growx, hidemode 3");

        style(astralBotButton, "arc: 12");
        style(tutucuButton, "arc: 12");
        style(lockNearestButton, "arc: 12");
        style(unlockButton, "arc: 12");
        style(tutucuSettingsButton, "arc: 12");
        style(pinUserButton, "arc: 12");
        style(pinSelectedButton, "arc: 12");
        style(pinnedPetButton, "arc: 12");
        return root;
    }

    /** Small rounded status pill. */
    private static class Chip extends JLabel {
        private Color fill = new Color(0x3B4157);

        Chip() {
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(java.awt.Font.BOLD, 11f));
            setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 10, 3, 10));
        }

        void set(String text, Color color) {
            if (!text.equals(getText())) setText(text);
            if (!color.equals(fill)) {
                fill = color;
                repaint();
            }
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void setTutucuTarget(com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.TargetType type) {
        if (updatingUi) return;
        main.config.GENERAL.HAK_CLAN_TUTUCU.TARGET_TYPE = type;
        refreshStatus();
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

    private final JProgressBar homeProgress = new JProgressBar(0, 50);
    private JButton homeButtonRef;

    /**
     * The bot's built-in homepage (the Backpage browser: hangar, shop, auction...), the same thing the old title-bar
     * home icon opened. Needs the game to be loaded (it uses the session id); downloads the browser the first time.
     */
    private void openHomepage() {
        if (homeButtonRef != null) homeButtonRef.setEnabled(false);
        new com.github.manolo8.darkbot.gui.titlebar.BackpageTask(main, new com.github.manolo8.darkbot.gui.titlebar.BackpageTask.Host() {
            @Override
            public void setEnabled(boolean enabled) {
                if (homeButtonRef != null) homeButtonRef.setEnabled(enabled);
            }

            @Override
            public JProgressBar addProgressBar() {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    homeProgress.setValue(0);
                    homeProgress.setIndeterminate(true);
                    homeProgress.setVisible(true);
                });
                return homeProgress;
            }

            @Override
            public void removeProgressBar() {
                javax.swing.SwingUtilities.invokeLater(() -> homeProgress.setVisible(false));
            }
        }).start();
    }

    private void toggleGameWindow() {
        gameWindowVisible = !gameWindowVisible;
        gameCanvas.setVisible(gameWindowVisible);
        NativeWindowEmbedder.setEmbeddedVisible(gameWindowVisible && mainTabs.getSelectedComponent() == gameCanvas.getParent());
        Main.API.setVisible(gameWindowVisible, main.config.BOT_SETTINGS.API_CONFIG.FULLY_HIDE_API);
        pack();
        refreshStatus();
    }

    public Canvas getGameCanvas() {
        return gameCanvas;
    }

    private void openConfig() {
        mainTabs.setSelectedIndex(settingsTabIndex);
        refreshStatus();
    }

    private void refreshStatus() {
        updatingUi = true;
        try {
            refreshStatusInternal();
        } finally {
            updatingUi = false;
        }
    }

    private void refreshStatusInternal() {
        boolean running = main.isRunning();
        startStopButton.setText(running ? "Stop Bot" : "Start Bot");
        style(startStopButton, running
                ? "arc: 14; font: bold +2; borderWidth: 0; focusWidth: 0; background: #F43F5E; foreground: #FFFFFF; hoverBackground: #FB5775; pressedBackground: #E11D48"
                : "arc: 14; font: bold +2; borderWidth: 0; focusWidth: 0; background: #10B981; foreground: #04140E; hoverBackground: #34D399; pressedBackground: #059669");
        toggleGameWindowButton.setText(gameWindowVisible ? "Hide game" : "Show game");

        String user = main.hero.playerInfo.username;
        usernameValue.setText(user == null || user.isBlank() ? "Waiting for pilot..." : user);
        moduleValue.setText(resolveModuleLabel(main.config.GENERAL.CURRENT_MODULE));
        runningChip.set(running ? "RUNNING" : "STOPPED", running ? OK : IDLE);
        boolean apiValid = Main.API.isValid();
        gameChip.set(apiValid ? "GAME CONNECTED" : "GAME LOADING", apiValid ? INFO : WARN);
        sidStatusValue.setText("Session " + orFallback(main.backpage.sidStatus(), "unknown"));

        boolean tutucuActive = TUTUCU_MODULE.equals(main.config.GENERAL.CURRENT_MODULE);
        astralBotButton.setSelected(!tutucuActive);
        tutucuButton.setSelected(tutucuActive);
        tutucuPanel.setVisible(tutucuActive);
        com.github.manolo8.darkbot.config.Config.General.HakClanTutucu tc = main.config.GENERAL.HAK_CLAN_TUTUCU;
        targetPlayersButton.setSelected(tc.TARGET_TYPE == com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.TargetType.PLAYERS);
        targetNpcsButton.setSelected(tc.TARGET_TYPE == com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.TargetType.NPCS);
        targetBothButton.setSelected(tc.TARGET_TYPE == com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.TargetType.BOTH);
        autoBattleBox.setSelected(tc.AUTO_ATTACK);
        autoNextBox.setSelected(tc.AUTO_NEXT_TARGET);
        vsModeBox.setSelected(tc.VS_MODE);
        {
            java.util.List<String> prof = main.getModule() instanceof HakClanTutucuModule && ((HakClanTutucuModule) main.getModule()).getVs() != null
                    ? ((HakClanTutucuModule) main.getModule()).getVs().profiler().lines() : java.util.List.of();
            if (prof.isEmpty()) {
                if (profileLabel.isVisible()) profileLabel.setVisible(false);
            } else {
                StringBuilder html = new StringBuilder("<html>");
                for (String l : prof) {
                    int indent = 0;
                    while (indent < l.length() && l.charAt(indent) == ' ') indent++;
                    // keep the indent but let long lines wrap (non-breaking everywhere made the panel grow sideways)
                    html.append("&nbsp;".repeat(indent)).append(l.substring(indent).replace("&", "&amp;").replace("<", "&lt;")).append("<br>");
                }
                String text = html.append("</html>").toString();
                if (!text.equals(profileLabel.getText())) profileLabel.setText(text);
                if (!profileLabel.isVisible()) profileLabel.setVisible(true);
            }
        }
        lockFriendsBox.setSelected(tc.LOCK_FRIENDS);
        autoPinBox.setSelected(tc.AUTO_PIN_MANUAL);
        HakClanTutucuModule vsModule = main.getModule() instanceof HakClanTutucuModule ? (HakClanTutucuModule) main.getModule() : null;
        boolean vsOn = tc.VS_MODE && vsModule != null && vsModule.getVs() != null;
        vsHud.setVisible(vsOn);
        if (vsOn) {
            vsHud.setPilot(tc.VS_PILOT);
            vsHud.update(vsModule.getVs().hud(), vsModule.getVsAdvice());
        }

        if (main.getModule() instanceof HakClanTutucuModule) {
            HakClanTutucuModule tutucu = (HakClanTutucuModule) main.getModule();
            String pinned = tutucu.getPinnedEnemyName();
            if (!pinnedUserField.hasFocus()) pinnedUserField.setText(pinned == null ? "" : pinned);
            pinSelectedButton.setText(pinned == null ? "Pin selected player" : "Unpin " + pinned);
            List<HakClanTutucuModule.EnemyEntry> enemies = tutucu.getNearestEnemies();
            // Update the list in place: clearing and refilling it twice a second made the whole panel flicker.
            java.util.List<String> rows = new java.util.ArrayList<>();
            int selectIndex = -1;
            if (enemies.isEmpty()) rows.add("No enemies nearby");
            for (HakClanTutucuModule.EnemyEntry enemy : enemies) {
                if (pinned != null && pinned.equalsIgnoreCase(enemy.name)) selectIndex = rows.size();
                rows.add((enemy.npc ? "[NPC] " : "") + enemy.name + "   -   " + enemy.distance);
            }
            while (nearestEnemiesModel.size() > rows.size()) nearestEnemiesModel.removeElementAt(nearestEnemiesModel.size() - 1);
            for (int i = 0; i < rows.size(); i++) {
                if (i >= nearestEnemiesModel.size()) nearestEnemiesModel.addElement(rows.get(i));
                else if (!rows.get(i).equals(nearestEnemiesModel.get(i))) nearestEnemiesModel.set(i, rows.get(i));
            }
            if (selectIndex != nearestEnemiesList.getSelectedIndex()) {
                if (selectIndex >= 0) nearestEnemiesList.setSelectedIndex(selectIndex);
                else nearestEnemiesList.clearSelection();
            }
            String locked = tutucu.getLockedName();
            String lockText = locked != null ? "Next target (" + tutucu.getSelectorKeyLabel() + ")" : "Lock nearest (" + tutucu.getSelectorKeyLabel() + ")";
            if (!lockText.equals(lockNearestButton.getText())) lockNearestButton.setText(lockText);
            unlockButton.setEnabled(locked != null);
        } else {
            nearestEnemiesModel.clear();
            nearestEnemiesList.clearSelection();
            lockNearestButton.setText("Lock nearest");
            unlockButton.setEnabled(false);
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

        JDialog dialog = new JDialog(this, "HAK Clan Tutucu settings", true);
        KeyBinder lockKey = new KeyBinder(dialog, cfg.SELECTOR_KEY);
        KeyBinder cycleKey = new KeyBinder(dialog, cfg.CYCLE_KEY);
        KeyBinder pinKey = new KeyBinder(dialog, cfg.PIN_KEY);
        KeyBinder assistKey = new KeyBinder(dialog, cfg.ASSIST_KEY);
        KeyBinder petKey = new KeyBinder(dialog, cfg.PIN_PET_KEY);

        JPanel panel = new JPanel(new MigLayout("wrap 2, ins 10", "[right]10[grow,fill]", "[]8[]8[]8[]"));
        panel.add(new JLabel("Enable locking")); panel.add(enableLocking);
        panel.add(new JLabel("Auto attack")); panel.add(autoAttack);
        panel.add(new JLabel("Lock nearest")); panel.add(lockNearest);
        panel.add(new JLabel("Max range")); panel.add(maxRange);
        panel.add(new JLabel("Lock / next target")); panel.add(lockKey.component());
        panel.add(new JLabel("Cycle target")); panel.add(cycleKey.component());
        panel.add(new JLabel("Pin selected player")); panel.add(pinKey.component());
        panel.add(new JLabel("Do pilot's suggestion")); panel.add(assistKey.component());
        panel.add(new JLabel("Pinned player's pet")); panel.add(petKey.component());
        JLabel hint = new JLabel("Click a button, then press any key or a middle / side mouse button. Esc cancels.");
        hint.putClientProperty("FlatLaf.style", "foreground: #9AA3B5; font: -2");
        panel.add(hint, "span 2");

        dialog.setLayout(new MigLayout("wrap 1, ins 12", "[grow,fill]", ""));
        dialog.add(panel);

        JPanel actions = new JPanel(new MigLayout("ins 0", "[grow,fill][grow,fill]", ""));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        ok.addActionListener(e -> {
            cfg.ENABLE_LOCKING = enableLocking.isSelected();
            cfg.AUTO_ATTACK = autoAttack.isSelected();
            cfg.LOCK_ON_NEAREST = lockNearest.isSelected();
            cfg.MAX_LOCK_DISTANCE = (int) maxRange.getValue();

            cfg.SELECTOR_KEY = lockKey.value == null || lockKey.value.isBlank() ? "L" : lockKey.value;
            cfg.CYCLE_KEY = cycleKey.value == null ? "" : cycleKey.value;
            cfg.PIN_KEY = pinKey.value == null ? "" : pinKey.value;
            cfg.ASSIST_KEY = assistKey.value == null ? "" : assistKey.value;
            cfg.PIN_PET_KEY = petKey.value == null ? "" : petKey.value;
            dialog.dispose();
        });
        cancel.addActionListener(e -> dialog.dispose());
        actions.add(ok); actions.add(cancel);
        dialog.add(actions);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * A button that captures the next key or middle/side mouse button. Keys are caught with a focus-manager dispatcher
     * (the dialog itself never gets key events while a child holds focus); mouse buttons through an AWT listener.
     */
    private final class KeyBinder {
        private final JButton button = new JButton();
        private final JButton clear = new JButton("x");
        private final JPanel box = new JPanel(new MigLayout("ins 0, gapx 4", "[grow,fill][]", ""));
        String value;
        private Runnable stopCapture;
        private int pendingModifier = -1;

        KeyBinder(JDialog dialog, String initial) {
            value = initial == null ? "" : initial;
            show();
            clear.setToolTipText("Unbind");
            clear.addActionListener(e -> {
                if (stopCapture != null) stopCapture.run();
                value = "";
                show();
            });
            button.addActionListener(e -> startCapture());
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent we) {
                    if (stopCapture != null) stopCapture.run();
                }
            });
            box.add(button);
            box.add(clear, "w 34!");
        }

        JPanel component() {
            return box;
        }

        private void show() {
            button.setText(value.isBlank() ? "Not set" : value);
        }

        private void startCapture() {
            if (stopCapture != null) stopCapture.run();
            button.setText("Press a key or mouse button...");

            java.awt.KeyEventDispatcher keys = ev -> {
                if (ev.getID() == KeyEvent.KEY_RELEASED && pendingModifier == ev.getKeyCode()) {
                    value = resolveKeyDisplayText(ev.getKeyCode(), ev.getKeyChar()); // a modifier pressed and released alone
                    pendingModifier = -1;
                    stopCapture.run();
                    show();
                    return true;
                }
                if (ev.getID() != KeyEvent.KEY_PRESSED) return ev.getID() == KeyEvent.KEY_TYPED || ev.getID() == KeyEvent.KEY_RELEASED;
                if (ev.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stopCapture.run();
                    show();
                    return true;
                }
                boolean modifier = ev.getKeyCode() == KeyEvent.VK_SHIFT || ev.getKeyCode() == KeyEvent.VK_CONTROL
                        || ev.getKeyCode() == KeyEvent.VK_ALT || ev.getKeyCode() == KeyEvent.VK_WINDOWS
                        || ev.getKeyCode() == KeyEvent.VK_META;
                if (modifier) {
                    pendingModifier = ev.getKeyCode(); // bound on release unless a real key follows (Shift+Z etc.)
                    return true;
                }
                pendingModifier = -1;
                value = resolveKeyDisplayText(ev.getKeyCode(), ev.getKeyChar());
                stopCapture.run();
                show();
                return true;
            };
            java.awt.event.AWTEventListener mouse = awt -> {
                if (!(awt instanceof java.awt.event.MouseEvent) || awt.getID() != java.awt.event.MouseEvent.MOUSE_PRESSED) return;
                int b = ((java.awt.event.MouseEvent) awt).getButton();
                if (b != 2 && b != 4 && b != 5) return; // left / right clicks stay normal clicks
                value = "MOUSE" + (b == 2 ? 3 : b);
                stopCapture.run();
                show();
            };
            java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keys);
            java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(mouse, java.awt.AWTEvent.MOUSE_EVENT_MASK);
            stopCapture = () -> {
                java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keys);
                java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(mouse);
                stopCapture = null;
            };
        }
    }

    /** The name the hotkey parser understands for a pressed AWT key. */
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
            case KeyEvent.VK_WINDOWS: case KeyEvent.VK_META: return "WIN";
            case KeyEvent.VK_CONTEXT_MENU: return "MENU";
            case KeyEvent.VK_CAPS_LOCK: return "CAPSLOCK";
            case KeyEvent.VK_NUM_LOCK: return "NUMLOCK";
            case KeyEvent.VK_SCROLL_LOCK: return "SCROLLLOCK";
            case KeyEvent.VK_PAUSE: return "PAUSE";
            case KeyEvent.VK_PRINTSCREEN: return "PRINTSCREEN";
            case KeyEvent.VK_INSERT: return "INSERT";
            case KeyEvent.VK_HOME: return "HOME";
            case KeyEvent.VK_END: return "END";
            case KeyEvent.VK_PAGE_UP: return "PAGEUP";
            case KeyEvent.VK_PAGE_DOWN: return "PAGEDOWN";
            default:
                if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) return "NUMPAD" + (keyCode - KeyEvent.VK_NUMPAD0);
                if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12) return "F" + (keyCode - KeyEvent.VK_F1 + 1);
                if (keyCode >= KeyEvent.VK_F13 && keyCode <= KeyEvent.VK_F24) return "F" + (keyCode - KeyEvent.VK_F13 + 13);
                if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) return String.valueOf((char) keyCode);
                if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9) return String.valueOf((char) keyCode);
                if (keyChar != KeyEvent.CHAR_UNDEFINED && !Character.isISOControl(keyChar)) return String.valueOf(Character.toUpperCase(keyChar));
                return KeyEvent.getKeyText(keyCode);
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
        if (main.isRunning() && !name.isEmpty()) tutucu.requestLockPinned();
        refreshStatus();
    }

    private void pinSelectedPlayer() {
        if (!(main.getModule() instanceof HakClanTutucuModule)) {
            JOptionPane.showMessageDialog(this, "Select HAK Clan Tutucu module first.", "Tutucu Not Active",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        HakClanTutucuModule tutucu = (HakClanTutucuModule) main.getModule();
        String name = tutucu.togglePin();
        if (name == null) {
            JOptionPane.showMessageDialog(this, "Click a player in the game first, then pin.", "Nobody selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        pinnedUserField.setText(tutucu.getPinnedEnemyName() == null ? "" : tutucu.getPinnedEnemyName());
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

        ((HakClanTutucuModule) main.getModule()).requestLockNext();
        refreshStatus();
    }

    private void setPilotMode(com.github.manolo8.darkbot.config.Config.General.HakClanTutucu.VsPilot mode) {
        main.config.GENERAL.HAK_CLAN_TUTUCU.VS_PILOT = mode;
    }

    private void doPilotSuggestion() {
        if (main.getModule() instanceof HakClanTutucuModule) ((HakClanTutucuModule) main.getModule()).doPilotSuggestion();
    }

    private void prepareForBattle() {
        if (!main.isRunning()) {
            JOptionPane.showMessageDialog(this, "Start the bot first - preparing happens while it runs.", "VS", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (main.getModule() instanceof HakClanTutucuModule) ((HakClanTutucuModule) main.getModule()).requestVsPrepare();
        else JOptionPane.showMessageDialog(this, "Select the HAK Tutucu module and start the bot first.",
                "VS", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openVsStrategy() {
        com.github.manolo8.darkbot.modules.vs.VsMode vs = main.getModule() instanceof HakClanTutucuModule
                ? ((HakClanTutucuModule) main.getModule()).getVs() : null;
        if (vs == null) {
            JOptionPane.showMessageDialog(this, "Select the HAK Tutucu module first.", "VS", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new VsStrategyDialog(javax.swing.SwingUtilities.getWindowAncestor(this), main.config.GENERAL.HAK_CLAN_TUTUCU, vs)
                .setVisible(true);
    }

    private void requestTutucuRelease() {
        if (main.getModule() instanceof HakClanTutucuModule) ((HakClanTutucuModule) main.getModule()).requestRelease();
        refreshStatus();
    }

    public void requestTutucuLockFromKeyboard() {
        requestTutucuLock();
    }
}