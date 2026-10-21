package com.github.manolo8.darkbot.gui.titlebar;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.gui.MainGui;
import com.github.manolo8.darkbot.gui.utils.SimpleMouseListener;
import com.github.manolo8.darkbot.gui.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.Font;

public class MainTitleBar extends JMenuBar implements SimpleMouseListener {

    private final TitleFiller titleFiller = new TitleFiller();

    public MainTitleBar(Main main, MainGui frame) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder());

        add(new ExtraButton(main, frame));
        add(new ConfigButton(frame));
        add(new StatsButton(main, frame));
        add(new StartButton(main, frame));
        add(new BackpageButton(main, frame));

        add(titleFiller);
        add(DiagnosticBar.create(main, frame));

        add(new HookButton(frame));
        add(new VisibilityButton(main, frame));
        add(new PinButton(frame));
        add(new TrayButton(main, frame));
    }

    public void setInfo(String info) {
        this.titleFiller.title.setText(info);
    }

    private static class TitleFiller extends Box.Filler {
        private final JLabel title = new JLabel();

        private TitleFiller() {
            super(new Dimension(20, 0), new Dimension(140, 0), new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            title.setForeground(new Color(0x00B8D4));
            title.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
            add(title, BorderLayout.CENTER);
            title.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x37474F));
            g2.drawLine(0, 6, 0, getHeight() - 7);
            g2.drawLine(getWidth() - 1, 6, getWidth() - 1, getHeight() - 7);
        }
    }
}
