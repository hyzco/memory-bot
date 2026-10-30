package com.github.manolo8.darkbot.gui.astral;

import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.modules.vs.VsMode;
import eu.darkbot.api.game.enums.PetGear;
import eu.darkbot.api.game.items.SelectableItem;
import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/** Editor for the VS strategy: target priority, battle preparation, the opening macro, pet and defences. */
public class VsStrategyDialog extends JDialog {

    /** Combo entry: an item (or "Auto"/"None" when null) with the text shown for it. */
    private static final class Opt {
        final SelectableItem item;
        final String text;

        Opt(SelectableItem item, String text) {
            this.item = item;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private final Config.General.HakClanTutucu cfg;
    private final VsMode vs;

    private final JComboBox<Config.General.HakClanTutucu.VsPriority> priority = new JComboBox<>(Config.General.HakClanTutucu.VsPriority.values());
    private final JSpinner switchDelay = new JSpinner(new SpinnerNumberModel(1500, 300, 6000, 100));
    private final JComboBox<Config.General.HakClanTutucu.MacroTrigger> trigger = new JComboBox<>(Config.General.HakClanTutucu.MacroTrigger.values());
    private final JSpinner triggerDistance = new JSpinner(new SpinnerNumberModel(900, 300, 2500, 50));
    private final JCheckBox macroEnabled = new JCheckBox("Run the macro");
    private final JCheckBox onPlayers = new JCheckBox("Enemy players");
    private final JCheckBox onNpcs = new JCheckBox("NPCs");
    private final JCheckBox onFriends = new JCheckBox("A friendly player I locked");
    private final JSpinner macroBelowHp = new JSpinner(new SpinnerNumberModel(100, 10, 100, 5));
    private final JComboBox<Opt> formation = new JComboBox<>();
    private final JComboBox<Opt> laser = new JComboBox<>();
    private final JComboBox<Opt> rocket = new JComboBox<>();
    private final JComboBox<Config.General.HakClanTutucu.VsPilot> pilotMode = new JComboBox<>(Config.General.HakClanTutucu.VsPilot.values());
    private final JCheckBox fly = new JCheckBox("AI pilot flies the ship (chase, keep range, retreat)");
    private final JComboBox<Config.General.HakClanTutucu.FlyStyle> flyStyle = new JComboBox<>(Config.General.HakClanTutucu.FlyStyle.values());
    private final JSpinner fightRange = new JSpinner(new SpinnerNumberModel(560, 300, 900, 10));
    private final JCheckBox flee = new JCheckBox("Retreat when the fight is lost");
    private final JCheckBox learnAmmo = new JCheckBox("Learn which laser ammo hurts most (shield / hull)");
    private final JCheckBox abandon = new JCheckBox("Drop a target it can't catch and take another");
    private final JCheckBox rockets = new JCheckBox("AI pilot fires rockets");
    private final JCheckBox saveAmmo = new JCheckBox("Save ammo: hold fire while the target is invulnerable");
    private final JCheckBox empCloak = new JCheckBox("EMP on a cloaked enemy close to me");
    private final JCheckBox empIsh = new JCheckBox("EMP on an insta-shielded target");
    private final JCheckBox assistSafety = new JCheckBox("Assisted: auto insta-shield / heal only when about to die");
    private final JCheckBox mixAmmo = new JCheckBox("Mix laser ammo during the fight");
    private final JComboBox<Config.General.HakClanTutucu.AmmoMixMode> mixMode = new JComboBox<>(Config.General.HakClanTutucu.AmmoMixMode.values());
    private final JComboBox<Opt> ammoA = new JComboBox<>();
    private final JComboBox<Opt> ammoB = new JComboBox<>();
    private final JSpinner secondsA = new JSpinner(new SpinnerNumberModel(4, 1, 30, 1));
    private final JSpinner secondsB = new JSpinner(new SpinnerNumberModel(2, 1, 30, 1));
    private final JSpinner shieldBelow = new JSpinner(new SpinnerNumberModel(30, 5, 95, 5));
    private final JCheckBox autoPrepare = new JCheckBox("Prepare automatically when an enemy shows up");
    private final JCheckBox petEnabled = new JCheckBox("Use pet in fights");
    private final JComboBox<PetGear> petGear = new JComboBox<>(PetGear.values());
    private final JCheckBox defence = new JCheckBox("Auto heal / defence ability below");
    private final JSpinner defenceBelow = new JSpinner(new SpinnerNumberModel(45, 10, 90, 5));
    private final DefaultTableModel macroModel = new DefaultTableModel(new Object[]{"Item", "Delay (ms)"}, 0);
    private final JComboBox<Opt> macroItems = new JComboBox<>();

    public VsStrategyDialog(Window owner, Config.General.HakClanTutucu cfg, VsMode vs) {
        super(owner, "VS strategy", ModalityType.APPLICATION_MODAL);
        this.cfg = cfg;
        this.vs = vs;

        fill(formation, SelectableItem.Formation.values(), "Auto (advisor picks)");
        fill(laser, SelectableItem.Laser.values(), "Auto (advisor picks)");
        fill(rocket, SelectableItem.Rocket.values(), "Keep current");
        fillAmmo(ammoA);
        fillAmmo(ammoB);
        fillMacroItems();

        JPanel root = new JPanel(new MigLayout("wrap 2, ins 14, gapy 6", "[right]10[grow,fill]", ""));

        JPanel presets = new JPanel(new MigLayout("ins 0, gapx 6", "[grow,fill][grow,fill][grow,fill]", ""));
        JButton duel = new JButton("1:1 Duel");
        JButton outnumbered = new JButton("Outnumbered");
        JButton burst = new JButton("Burst opener");
        duel.addActionListener(e -> preset(Config.General.HakClanTutucu.VsPriority.BALANCED, SelectableItem.Formation.DOUBLE_ARROW,
                "ISH_01@0; EMP_01@3000", false, 40, false));
        outnumbered.addActionListener(e -> preset(Config.General.HakClanTutucu.VsPriority.THREAT, SelectableItem.Formation.DOME,
                "ISH_01@0", true, 55, true));
        burst.addActionListener(e -> preset(Config.General.HakClanTutucu.VsPriority.LEAST_LIFE, SelectableItem.Formation.BAT,
                "SMB_01@0; EMP_01@1500; ISH_01@3000", false, 40, false));
        presets.add(duel);
        presets.add(outnumbered);
        presets.add(burst);
        root.add(new JLabel("Presets"));
        root.add(presets);

        root.add(section("PILOT"), "span 2");
        root.add(new JLabel("Mode"));
        root.add(pilotMode);
        JLabel pilotHint = new JLabel("<html>AI: measures the fight, simulates it and acts.  Assisted: you fly; it advises and protects.<br>"
                + "Classic: the macro and defence rules below.</html>");
        pilotHint.putClientProperty("FlatLaf.style", "foreground: #9AA3B5; font: -2");
        root.add(pilotHint, "span 2");
        root.add(fly, "span 2");
        root.add(new JLabel("Flying style"));
        root.add(flyStyle);
        root.add(new JLabel("Fight distance"));
        root.add(fightRange);
        root.add(flee, "span 2");
        root.add(rockets, "span 2");
        root.add(learnAmmo, "span 2");
        root.add(abandon, "span 2");
        root.add(saveAmmo, "span 2");
        root.add(empCloak, "span 2");
        root.add(empIsh, "span 2");
        root.add(assistSafety, "span 2");

        root.add(section("TARGETING"), "span 2");
        root.add(new JLabel("Focus"));
        root.add(priority);
        root.add(new JLabel("Switch target no sooner than (ms)"));
        root.add(switchDelay);

        root.add(section("BATTLE PREPARATION  (button: Prepare for battle)"), "span 2");
        root.add(new JLabel("Formation"));
        root.add(formation);
        root.add(new JLabel("Laser ammo"));
        root.add(laser);
        root.add(new JLabel("Rocket"));
        root.add(rocket);
        root.add(autoPrepare, "span 2");
        root.add(petEnabled, "span 2, split 2");
        root.add(petGear);

        root.add(section("AMMO MIX  (e.g. x4 = UCB-100 + RSB-75)"), "span 2");
        root.add(mixAmmo, "span 2");
        root.add(new JLabel("Ammo A (main)"));
        root.add(ammoA);
        root.add(new JLabel("Ammo B"));
        root.add(ammoB);
        root.add(new JLabel("Mode"));
        root.add(mixMode);
        root.add(new JLabel("Seconds on A / on B"));
        JPanel secs = new JPanel(new MigLayout("ins 0, gapx 8"));
        secs.add(secondsA, "w 70!");
        secs.add(secondsB, "w 70!");
        root.add(secs);
        root.add(new JLabel("B when enemy shield below (%)"));
        root.add(shieldBelow);

        root.add(section("OPENING MACRO"), "span 2");
        root.add(macroEnabled, "span 2");
        root.add(new JLabel("Fires when"));
        root.add(trigger);
        root.add(new JLabel("Enemy closer than"));
        root.add(triggerDistance);
        root.add(new JLabel("Reacts to"));
        JPanel who = new JPanel(new MigLayout("ins 0, gapx 10"));
        who.add(onPlayers);
        who.add(onNpcs);
        who.add(onFriends);
        root.add(who);
        root.add(new JLabel("Only when my life is below (%)"));
        root.add(macroBelowHp);

        JTable table = new JTable(macroModel);
        table.setRowHeight(24);
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(macroItems));
        table.getColumnModel().getColumn(1).setMaxWidth(110);
        root.add(new JScrollPane(table), "span 2, growx, h 110!");
        JButton add = new JButton("Add step");
        JButton remove = new JButton("Remove step");
        add.addActionListener(e -> macroModel.addRow(new Object[]{macroItems.getItemAt(0), 0}));
        remove.addActionListener(e -> {
            if (table.isEditing()) table.getCellEditor().stopCellEditing();
            int row = table.getSelectedRow();
            if (row >= 0) macroModel.removeRow(row);
        });
        root.add(add, "span 2, split 2, growx");
        root.add(remove, "growx");

        root.add(section("DEFENCE"), "span 2");
        root.add(defence, "span 2, split 2");
        root.add(defenceBelow);

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        save.putClientProperty("FlatLaf.style", "background: #7C3AED; foreground: #FFFFFF");
        save.addActionListener(e -> {
            if (table.isEditing()) table.getCellEditor().stopCellEditing();
            save();
            dispose();
        });
        cancel.addActionListener(e -> dispose());
        root.add(save, "span 2, split 2, growx, gaptop 10");
        root.add(cancel, "growx");

        load();
        setAlwaysOnTop(true); // the docked game window must never cover the editor
        setContentPane(root);
        pack();
        setLocationRelativeTo(owner);
    }

    private static JLabel section(String text) {
        JLabel label = new JLabel(text);
        label.putClientProperty("FlatLaf.style", "foreground: #8B5CF6; font: -1 bold");
        return label;
    }

    private boolean owned(SelectableItem item) {
        try {
            return vs.owns(item);
        } catch (RuntimeException e) {
            return true; // can't tell right now: don't hide it
        }
    }

    private void fill(JComboBox<Opt> box, SelectableItem[] all, String none) {
        box.addItem(new Opt(null, none));
        List<Opt> missing = new ArrayList<>();
        for (SelectableItem it : all) {
            if (owned(it)) box.addItem(new Opt(it, VsMode.pretty(it)));
            else missing.add(new Opt(it, VsMode.pretty(it) + "  (not owned)"));
        }
        missing.forEach(box::addItem);
    }

    /** Laser ammo only, with the familiar xN names next to the item names. */
    private void fillAmmo(JComboBox<Opt> box) {
        for (SelectableItem.Laser l : SelectableItem.Laser.values()) {
            String x = l == SelectableItem.Laser.LCB_10 ? "  (x1)" : l == SelectableItem.Laser.MCB_25 ? "  (x2)"
                    : l == SelectableItem.Laser.MCB_50 ? "  (x3)" : l == SelectableItem.Laser.UCB_100 ? "  (x4)" : "";
            box.addItem(new Opt(l, VsMode.pretty(l) + x + (owned(l) ? "" : "  (not owned)")));
        }
    }

    private void fillMacroItems() {
        List<SelectableItem> pool = new ArrayList<>();
        for (Class<? extends SelectableItem> c : List.<Class<? extends SelectableItem>>of(SelectableItem.Special.class,
                SelectableItem.Rocket.class, SelectableItem.Tech.class, SelectableItem.Ability.class,
                SelectableItem.Cpu.class, SelectableItem.Mine.class)) {
            for (SelectableItem it : c.getEnumConstants()) if (owned(it)) pool.add(it);
        }
        if (pool.isEmpty()) {
            pool.add(SelectableItem.Special.ISH_01);
            pool.add(SelectableItem.Special.EMP_01);
            pool.add(SelectableItem.Special.SMB_01);
        }
        for (SelectableItem it : pool) macroItems.addItem(new Opt(it, enumName(it)));
    }

    /** The exact name the macro text uses, so what you pick here is what gets saved. */
    private static String enumName(SelectableItem it) {
        return ((Enum<?>) it).name();
    }

    private static void select(JComboBox<Opt> box, SelectableItem item) {
        for (int i = 0; i < box.getItemCount(); i++) {
            if (box.getItemAt(i).item == item) {
                box.setSelectedIndex(i);
                return;
            }
        }
        box.setSelectedIndex(0);
    }

    private void setMacroText(String text) {
        macroModel.setRowCount(0);
        for (String part : text.split("[;,]")) {
            part = part.trim();
            if (part.isEmpty()) continue;
            int at = part.indexOf('@');
            String name = (at >= 0 ? part.substring(0, at) : part).trim().toUpperCase().replace('-', '_');
            int delay = 0;
            if (at >= 0) {
                try {
                    delay = Integer.parseInt(part.substring(at + 1).replaceAll("[^0-9]", ""));
                } catch (NumberFormatException ignored) {
                    // 0
                }
            }
            SelectableItem item = VsMode.resolveItem(name);
            Opt opt = null;
            for (int i = 0; i < macroItems.getItemCount() && item != null; i++)
                if (macroItems.getItemAt(i).item == item) opt = macroItems.getItemAt(i);
            if (opt == null && item != null) opt = new Opt(item, enumName(item));
            if (opt != null) macroModel.addRow(new Object[]{opt, delay});
        }
    }

    private String macroText() {
        List<String> parts = new ArrayList<>();
        for (int r = 0; r < macroModel.getRowCount(); r++) {
            Object item = macroModel.getValueAt(r, 0);
            Object delay = macroModel.getValueAt(r, 1);
            String name = item instanceof Opt ? ((Opt) item).text : String.valueOf(item);
            int ms = 0;
            try {
                ms = Integer.parseInt(String.valueOf(delay).trim());
            } catch (NumberFormatException ignored) {
                // 0
            }
            parts.add(name + "@" + ms);
        }
        return String.join("; ", parts);
    }

    private void preset(Config.General.HakClanTutucu.VsPriority prio, SelectableItem.Formation form, String macro,
                        boolean pet, int defenceAt, boolean useDefence) {
        priority.setSelectedItem(prio);
        select(formation, form);
        macroEnabled.setSelected(true);
        setMacroText(macro);
        petEnabled.setSelected(pet);
        defence.setSelected(useDefence);
        defenceBelow.setValue(defenceAt);
    }

    private void load() {
        priority.setSelectedItem(cfg.VS_PRIORITY);
        switchDelay.setValue(cfg.VS_SWITCH_DELAY);
        trigger.setSelectedItem(cfg.VS_MACRO_TRIGGER);
        triggerDistance.setValue(cfg.VS_TRIGGER_DISTANCE);
        macroEnabled.setSelected(cfg.VS_MACRO_ENABLED);
        onPlayers.setSelected(cfg.VS_MACRO_ON_PLAYERS);
        onNpcs.setSelected(cfg.VS_MACRO_ON_NPCS);
        onFriends.setSelected(cfg.VS_MACRO_ON_FRIENDS);
        macroBelowHp.setValue(cfg.VS_MACRO_BELOW_HP);
        setMacroText(cfg.VS_MACRO == null ? "" : cfg.VS_MACRO);
        select(formation, cfg.VS_PREP_FORMATION);
        select(laser, cfg.VS_PREP_LASER);
        select(rocket, cfg.VS_PREP_ROCKET);
        autoPrepare.setSelected(cfg.VS_AUTO_SETUP);
        pilotMode.setSelectedItem(cfg.VS_PILOT);
        fly.setSelected(cfg.VS_FLY);
        flyStyle.setSelectedItem(cfg.VS_FLY_STYLE);
        fightRange.setValue(cfg.VS_FIGHT_RANGE);
        flee.setSelected(cfg.VS_FLEE);
        rockets.setSelected(cfg.VS_ROCKETS);
        learnAmmo.setSelected(cfg.VS_AMMO_LEARN);
        abandon.setSelected(cfg.VS_ABANDON_RUNNERS);
        saveAmmo.setSelected(cfg.VS_SAVE_AMMO);
        empCloak.setSelected(cfg.VS_EMP_ON_CLOAK);
        empIsh.setSelected(cfg.VS_EMP_ON_ISH);
        assistSafety.setSelected(cfg.VS_ASSIST_SAFETY);
        mixAmmo.setSelected(cfg.VS_AMMO_MIX);
        mixMode.setSelectedItem(cfg.VS_AMMO_MIX_MODE);
        select(ammoA, cfg.VS_AMMO_A);
        select(ammoB, cfg.VS_AMMO_B);
        secondsA.setValue(cfg.VS_AMMO_A_SECONDS);
        secondsB.setValue(cfg.VS_AMMO_B_SECONDS);
        shieldBelow.setValue(cfg.VS_AMMO_SHIELD_BELOW);
        petEnabled.setSelected(cfg.VS_PET_ENABLED);
        petGear.setSelectedItem(cfg.VS_PET_GEAR);
        defence.setSelected(cfg.VS_AUTO_DEFENSE);
        defenceBelow.setValue(cfg.VS_DEFENCE_BELOW);
    }

    private void save() {
        cfg.VS_PRIORITY = (Config.General.HakClanTutucu.VsPriority) priority.getSelectedItem();
        cfg.VS_SWITCH_DELAY = (int) switchDelay.getValue();
        cfg.VS_MACRO_TRIGGER = (Config.General.HakClanTutucu.MacroTrigger) trigger.getSelectedItem();
        cfg.VS_TRIGGER_DISTANCE = (int) triggerDistance.getValue();
        cfg.VS_MACRO_ENABLED = macroEnabled.isSelected();
        cfg.VS_MACRO_ON_PLAYERS = onPlayers.isSelected();
        cfg.VS_MACRO_ON_NPCS = onNpcs.isSelected();
        cfg.VS_MACRO_ON_FRIENDS = onFriends.isSelected();
        cfg.VS_MACRO_BELOW_HP = (int) macroBelowHp.getValue();
        cfg.VS_MACRO = macroText();
        cfg.VS_PREP_FORMATION = (SelectableItem.Formation) ((Opt) formation.getSelectedItem()).item;
        cfg.VS_PREP_LASER = (SelectableItem.Laser) ((Opt) laser.getSelectedItem()).item;
        cfg.VS_PREP_ROCKET = (SelectableItem.Rocket) ((Opt) rocket.getSelectedItem()).item;
        cfg.VS_AUTO_SETUP = autoPrepare.isSelected();
        cfg.VS_PILOT = (Config.General.HakClanTutucu.VsPilot) pilotMode.getSelectedItem();
        cfg.VS_FLY = fly.isSelected();
        cfg.VS_FLY_STYLE = (Config.General.HakClanTutucu.FlyStyle) flyStyle.getSelectedItem();
        cfg.VS_FIGHT_RANGE = (int) fightRange.getValue();
        cfg.VS_FLEE = flee.isSelected();
        cfg.VS_ROCKETS = rockets.isSelected();
        cfg.VS_AMMO_LEARN = learnAmmo.isSelected();
        cfg.VS_ABANDON_RUNNERS = abandon.isSelected();
        cfg.VS_SAVE_AMMO = saveAmmo.isSelected();
        cfg.VS_EMP_ON_CLOAK = empCloak.isSelected();
        cfg.VS_EMP_ON_ISH = empIsh.isSelected();
        cfg.VS_ASSIST_SAFETY = assistSafety.isSelected();
        cfg.VS_AMMO_MIX = mixAmmo.isSelected();
        cfg.VS_AMMO_MIX_MODE = (Config.General.HakClanTutucu.AmmoMixMode) mixMode.getSelectedItem();
        cfg.VS_AMMO_A = (SelectableItem.Laser) ((Opt) ammoA.getSelectedItem()).item;
        cfg.VS_AMMO_B = (SelectableItem.Laser) ((Opt) ammoB.getSelectedItem()).item;
        cfg.VS_AMMO_A_SECONDS = (int) secondsA.getValue();
        cfg.VS_AMMO_B_SECONDS = (int) secondsB.getValue();
        cfg.VS_AMMO_SHIELD_BELOW = (int) shieldBelow.getValue();
        cfg.VS_PET_ENABLED = petEnabled.isSelected();
        cfg.VS_PET_GEAR = (PetGear) petGear.getSelectedItem();
        cfg.VS_AUTO_DEFENSE = defence.isSelected();
        cfg.VS_DEFENCE_BELOW = (int) defenceBelow.getValue();
    }
}
