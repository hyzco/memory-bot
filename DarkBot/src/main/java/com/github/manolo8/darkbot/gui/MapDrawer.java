package com.github.manolo8.darkbot.gui;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.backpage.FlashResManager;
import com.github.manolo8.darkbot.config.ColorScheme;
import com.github.manolo8.darkbot.config.types.suppliers.DisplayFlag;
import com.github.manolo8.darkbot.extensions.features.handlers.DrawableHandler;
import com.github.manolo8.darkbot.gui.titlebar.RefreshButton;
import com.github.manolo8.darkbot.modules.TemporalPortalJumper;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.extensions.Drawable;
import eu.darkbot.api.extensions.MapGraphics;
import eu.darkbot.api.game.entities.Portal;
import eu.darkbot.api.game.other.Area;
import eu.darkbot.api.game.other.GameMap;
import eu.darkbot.api.game.other.Locatable;
import eu.darkbot.api.game.other.Point;
import eu.darkbot.api.managers.ConfigAPI;
import eu.darkbot.api.managers.StarSystemAPI;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MapDrawer extends JPanel {

    private static final RenderingHints RENDERING_HINTS =
            new RenderingHints(Map.of(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON,
                RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));

    static {
        // tests needed - may increase CPU usage for some users
        /*Toolkit toolkit = Toolkit.getDefaultToolkit();
        RenderingHints hints = (RenderingHints) toolkit.getDesktopProperty("awt.font.desktophints");

        if (hints != null) {
            RENDERING_HINTS.add(hints);
            toolkit.addPropertyChangeListener("awt.font.desktophints",
                    evt -> RENDERING_HINTS.add((RenderingHints) evt.getNewValue()));
        }*/
    }

    public MapGraphicsImpl mapGraphics;
    public boolean hovering;

    protected Main main;
    protected DrawableHandler drawableHandler;

    private FlashResManager flashResManager;

    private CompletableFuture<Image> minimapFuture;
    private GameMap lastMap;
    private Image backgroundImage;

    @Getter private long lastMapClick;

    public MapDrawer() {
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                hovering = true;
                if (main.config.BOT_SETTINGS.MAP_DISPLAY.MAP_START_STOP)
                    repaint();
            }

            public void mouseExited(MouseEvent evt) {
                hovering = false;
                if (main.config.BOT_SETTINGS.MAP_DISPLAY.MAP_START_STOP)
                    repaint();
            }
        });
    }

    public MapDrawer(Main main) {
        this();
        setup(main);

        this.drawableHandler = main.pluginAPI.requireInstance(DrawableHandler.class);
        this.flashResManager = main.pluginAPI.requireInstance(FlashResManager.class);

        JPopupMenu portalMenu = new JPopupMenu();
        portalMenu.setBorder(BorderFactory.createEmptyBorder());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (main.config.BOT_SETTINGS.MAP_DISPLAY.MAP_START_STOP && SwingUtilities.isLeftMouseButton(e)) {
                    main.setRunning(!main.isRunning());
                    repaint();

                } else {
                    Locatable loc = mapGraphics.toGameLocation(e);

                    // move ship only on left button click
                    if (SwingUtilities.isRightMouseButton(e)) {
                        List<? extends Portal> portals;
                        synchronized (Main.UPDATE_LOCKER) {
                             portals = main.mapManager.entities.getPortals().stream()
                                    .filter(p -> p.distanceTo(loc) < 1000)
                                    .collect(Collectors.toList());
                        }

                        if (!portals.isEmpty()) {
                            portalMenu.removeAll();

                            // https://github.com/JFormDesigner/FlatLaf/issues/328
                            // compensate `MenuItem.textNoAcceleratorGap` which is 6 by default
                            Object iconSize = UIManager.put("MenuItem.minimumIconSize", new Dimension(6, 0));
                            Object itemWidth = UIManager.put("MenuItem.minimumWidth", 0);
                            for (Portal portal : portals) {
                                String portalText = portal.getTargetMap()
                                        .map(GameMap::getShortName)
                                        .orElse("(" + portal.getLocationInfo().getLast().toString() + ")");

                                JMenuItem item = new JMenuItem(portalText);
                                item.addActionListener(l -> main.setModule(new TemporalPortalJumper(main, portal)));
                                portalMenu.add(item);
                            }
                            UIManager.put("MenuItem.minimumIconSize", iconSize);
                            UIManager.put("MenuItem.minimumWidth", itemWidth);
                            portalMenu.show(MapDrawer.this, e.getX(), e.getY());
                        }
                    } else {
                        System.out.println("Map click -> move to " + (int) loc.getX() + "," + (int) loc.getY()
                                + " (bot running=" + main.isRunning() + ")");
                        main.hero.drive.move(loc);
                        lastMapClick = System.currentTimeMillis();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((main.config.BOT_SETTINGS.MAP_DISPLAY.MAP_START_STOP && SwingUtilities.isLeftMouseButton(e))
                        || SwingUtilities.isRightMouseButton(e)) return;

                main.hero.drive.move(mapGraphics.toGameLocation(e));
                lastMapClick = System.currentTimeMillis();
            }
        });

        setLayout(new MigLayout("insets 0px"));
        add(new RefreshButton(), "gapx 7px");
    }

    public void setup(Main main) {
        this.main = main;
        this.mapGraphics = main.pluginAPI.requireInstance(MapGraphicsImpl.class);
    }

    protected void onPaint() {
        drawBackgroundImage();

        for (Drawable drawable : drawableHandler.getDrawables()) {
            drawable.onDraw(mapGraphics);
        }

        // just ensure that is drawn always last
        if (hovering && main.config.BOT_SETTINGS.MAP_DISPLAY.MAP_START_STOP)
            drawActionButton();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (main == null) return;

        mapGraphics.setup(g, getWidth(), getHeight());

        synchronized (Main.UPDATE_LOCKER) {
            onPaint();
        }

        mapGraphics.dispose();
    }

    private void drawBackgroundImage() {
        if (!main.config.BOT_SETTINGS.CUSTOM_BACKGROUND.ENABLED) return;

        Image bgImg;
        if (main.config.BOT_SETTINGS.CUSTOM_BACKGROUND.USE_GAME_BACKGROUND) {
            GameMap currentMap = main.hero.getMap();

            if (currentMap != lastMap) {
                Future<?> f = minimapFuture; //prevent race condition
                if (f != null) f.cancel(true);

                lastMap = currentMap;
                backgroundImage = null;

                minimapFuture = currentMap.getId() <= 0 ? null : flashResManager.getBackgroundImage(currentMap);
                if (minimapFuture != null)
                    minimapFuture
                            .thenApply(r -> backgroundImage = r)
                            .whenComplete((r, t) -> minimapFuture = null);
            }
            bgImg = backgroundImage;

        } else {
            if (backgroundImage != null || lastMap != null) {
                backgroundImage = null;
                lastMap = null;
            }

            bgImg = main.config.BOT_SETTINGS.CUSTOM_BACKGROUND.IMAGE.getImage();
        }

        if (bgImg != null) {
            mapGraphics.g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) main.config.BOT_SETTINGS.CUSTOM_BACKGROUND.OPACITY));
            mapGraphics.g2.drawImage(bgImg, 0, 0, mapGraphics.width, mapGraphics.height, this);
            mapGraphics.g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private void drawActionButton() {
        mapGraphics.setColor("darken_back");
        mapGraphics.getGraphics2D().fillRect(0, 0, this.getWidth(), this.getHeight());

        mapGraphics.setColor("action_button");
        int height2 = this.getHeight() / 2, height3 = this.getHeight() / 3,
                width3 = this.getWidth() / 3, width9 = this.getWidth() / 9;

        if (main.isRunning()) {
            mapGraphics.g2.fillRect(width3, height3, width9, height3); // Two vertical parallel lines
            mapGraphics.g2.fillRect((width3 * 2) - width9, height3, width9, height3);
        } else { // A "play" triangle
            mapGraphics.g2.fillPolygon(new int[]{width3, width3 * 2, width3},
                    new int[]{height3, height2, height3 * 2}, 3);
        }
    }

    public static class MapGraphicsImpl implements MapGraphics {

        protected final ConfigAPI config;
        protected final Area.Rectangle mapBounds;

        protected final ConfigSetting<ColorScheme> cs;
        protected final ConfigSetting<ColorScheme.Fonts> fonts;
        protected final ConfigSetting<Set<DisplayFlag>> displayFlags;
        protected final ConfigSetting<Double> mapZoom;
        protected final ConfigSetting<Boolean> neonTheme;

        private java.awt.image.BufferedImage neonBackground;

        protected final Rectangle2D rect = new Rectangle2D.Double();
        protected final Ellipse2D ellipse = new Ellipse2D.Double();
        protected final Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        protected final Line2D line = new Line2D.Double();

        protected Graphics2D g2;
        protected int width, widthMid, height, heightMid, offsetX, offsetY;
        protected double scaleX, scaleY, invertedScaleX, invertedScaleY;

        protected boolean accuracyEnabled;

        public MapGraphicsImpl(StarSystemAPI star, ConfigAPI config) {
            this.config = config;
            this.mapBounds = star.getCurrentMapBounds();

            this.cs = config.requireConfig("bot_settings.map_display.cs");
            this.fonts = config.requireConfig("bot_settings.map_display.cs.fonts");
            this.displayFlags = config.requireConfig("bot_settings.map_display.toggle");
            this.mapZoom = config.requireConfig("bot_settings.map_display.map_zoom");
            this.neonTheme = config.requireConfig("bot_settings.map_display.neon_theme");
        }

        public void setup(Graphics graphics, int width, int height) {
            this.width = width;
            this.height = height;
            this.widthMid = width / 2;
            this.heightMid = height / 2;

            this.accuracyEnabled = false;

            this.g2 = (Graphics2D) graphics;
            this.g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));

            double mapZoom = getMapZoom();
            if (mapZoom < 1) {
                int w = (int) (width * mapZoom);
                int h = (int) (height * mapZoom);

                offsetX = (width - w) / 2;
                offsetY = (height - h) / 2;

                scale(w, h);

                setColor("radiation");
                getGraphics2D().fillRect(0, 0, width, height);
                setColor("background");
                getGraphics2D().fillRect(offsetX, offsetY, w, h);
                paintNeonBackground(offsetX, offsetY, w, h);
                setColor("unknown");
                getGraphics2D().drawRect(offsetX, offsetY, w, h);
            } else {
                offsetX = offsetY = 0;
                scale(width, height);

                setColor("background");
                getGraphics2D().fillRect(0, 0, width, height);
                paintNeonBackground(0, 0, width, height);
            }

            setSubPixelAccuracy(true);
            getGraphics2D().addRenderingHints(RENDERING_HINTS);
            getGraphics2D().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        }

        public void dispose() {
            g2 = null;
        }

        // 0-1 -> 0-100%
        protected double getMapZoom() {
            return mapZoom.getValue();
        }

        protected Locatable toGameLocation(MouseEvent e) {
            return toGameLocation(e.getX(), e.getY());
        }

        private void scale(int width, int height) {
            scaleX = mapBounds.getWidth() / width;
            scaleY = mapBounds.getHeight() / height;
            invertedScaleX = 1.0 / scaleX;
            invertedScaleY = 1.0 / scaleY;
        }

        private void drawShape(Shape shape, boolean fill) {
            if (fill) g2.fill(shape);
            else g2.draw(shape);
        }

        private void setSubPixelAccuracy(boolean enable) {
            if (accuracyEnabled == enable) return;
            accuracyEnabled = enable;

            // in jdk17, java internally checks rendering hint instance but not sure about other versions
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    enable ? RenderingHints.VALUE_STROKE_PURE : RenderingHints.VALUE_STROKE_DEFAULT);
        }

        @Override
        public Graphics2D getGraphics2D() {
            return g2;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public int getWidthMiddle() {
            return widthMid;
        }

        @Override
        public int getHeightMiddle() {
            return heightMid;
        }

        @Override
        public boolean hasDisplayFlag(eu.darkbot.api.config.types.DisplayFlag displayFlag) {
            DisplayFlag legacyFlag = DisplayFlag.values()[displayFlag.ordinal()];
            return displayFlags.getValue().contains(legacyFlag);
        }

        private static final java.util.Map<String, Color> NEON = new java.util.HashMap<>();
        static {
            NEON.put("background", new Color(0x05060F));
            NEON.put("radiation", new Color(0x14082B));
            NEON.put("unknown", new Color(0x4B3A9A));
            NEON.put("hero", new Color(0x00F5D4));
            NEON.put("going", new Color(0x00E5FF));
            NEON.put("enemies", new Color(0xFF2E88));
            NEON.put("allies", new Color(0x39FF14));
            NEON.put("npcs", new Color(0xFFB300));
            NEON.put("boxes", new Color(0xFFEA00));
            NEON.put("portals", new Color(0xB388FF));
            NEON.put("target", new Color(0xFF1744));
            NEON.put("text", new Color(0xE3E8FF));
            NEON.put("text_dark", new Color(0x9AA8E8));
            NEON.put("pet", new Color(0x00E5FF));
            NEON.put("pet_in", new Color(0x00B8D4));
            NEON.put("space_balls", new Color(0xB388FF));
            NEON.put("mines", new Color(0xFF5252));
            NEON.put("barrier_border", new Color(0xFF2E88));
            NEON.put("ping", new Color(0x00E5FF));
            NEON.put("ping_border", new Color(0x7C4DFF));
            NEON.put("health", new Color(0x00E676));
            NEON.put("shield", new Color(0x40C4FF));
            NEON.put("nano_hull", new Color(0x7C4DFF));
            NEON.put("fuel", new Color(0xFFAB00));
            NEON.put("safety", new Color(0x00B8D4));
            NEON.put("low_relays", new Color(0xB388FF));
            NEON.put("other_entities", new Color(0x9FA8DA));
            NEON.put("bases", new Color(0x7C4DFF));
            NEON.put("base_spots", new Color(0x5E35B1));
        }

        @Override
        public Color getColor(String color) {
            if (neonTheme.getValue()) {
                Color neon = NEON.get(color);
                if (neon != null) return neon;
            }
            return config.getConfigValue(cs, color);
        }

        /** Deep-space backdrop (gradient, nebula glow, stars, neon grid), rendered once per size and reused. */
        private void paintNeonBackground(int x, int y, int w, int h) {
            if (!neonTheme.getValue() || w < 8 || h < 8) return;

            if (neonBackground == null || neonBackground.getWidth() != w || neonBackground.getHeight() != h)
                neonBackground = buildNeonBackground(w, h);

            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g2.drawImage(neonBackground, x, y, null);
            g2.setComposite(old);
        }

        private static java.awt.image.BufferedImage buildNeonBackground(int w, int h) {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setPaint(new GradientPaint(0, 0, new Color(0x04050C), 0, h, new Color(0x0A0D26)));
            g.fillRect(0, 0, w, h);

            // nebula glows: violet bottom-left, cyan top-right
            g.setPaint(new java.awt.RadialGradientPaint(w * 0.12f, h * 0.95f, Math.max(w, h) * 0.65f,
                    new float[]{0f, 1f}, new Color[]{new Color(124, 77, 255, 85), new Color(124, 77, 255, 0)}));
            g.fillRect(0, 0, w, h);
            g.setPaint(new java.awt.RadialGradientPaint(w * 0.92f, h * 0.08f, Math.max(w, h) * 0.55f,
                    new float[]{0f, 1f}, new Color[]{new Color(0, 229, 255, 55), new Color(0, 229, 255, 0)}));
            g.fillRect(0, 0, w, h);

            // faint neon grid
            for (int gx = 0; gx < w; gx += 40) {
                g.setColor(new Color(108, 92, 231, gx % 200 == 0 ? 34 : 14));
                g.drawLine(gx, 0, gx, h);
            }
            for (int gy = 0; gy < h; gy += 40) {
                g.setColor(new Color(108, 92, 231, gy % 200 == 0 ? 34 : 14));
                g.drawLine(0, gy, w, gy);
            }

            // stars (deterministic so the field doesn't flicker between resizes)
            java.util.Random rnd = new java.util.Random(7L);
            int stars = Math.max(40, (w * h) / 2200);
            Color[] tint = {new Color(255, 255, 255), new Color(170, 220, 255), new Color(200, 170, 255)};
            for (int i = 0; i < stars; i++) {
                int sx = rnd.nextInt(w), sy = rnd.nextInt(h);
                int alpha = 70 + rnd.nextInt(185);
                Color t = tint[rnd.nextInt(tint.length)];
                g.setColor(new Color(t.getRed(), t.getGreen(), t.getBlue(), alpha));
                if (rnd.nextInt(14) == 0) {
                    g.fillOval(sx - 1, sy - 1, 3, 3);
                    g.setColor(new Color(t.getRed(), t.getGreen(), t.getBlue(), 40));
                    g.fillOval(sx - 3, sy - 3, 7, 7);
                } else {
                    g.fillRect(sx, sy, 1, 1);
                }
            }

            // soft vignette
            g.setPaint(new java.awt.RadialGradientPaint(w / 2f, h / 2f, Math.max(w, h) * 0.75f,
                    new float[]{0.55f, 1f}, new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 150)}));
            g.fillRect(0, 0, w, h);

            // neon frame
            g.setColor(new Color(124, 77, 255, 120));
            g.drawRect(0, 0, w - 1, h - 1);
            g.dispose();
            return img;
        }

        @Override
        public Font getFont(String font) {
            return config.getConfigValue(fonts, font);
        }

        @Override
        public double getScaleX() {
            return scaleX;
        }

        @Override
        public double getScaleY() {
            return scaleY;
        }

        @Override
        public double toScreenPointX(double gameX) {
            return offsetX + gameX * invertedScaleX;
        }

        @Override
        public double toScreenPointY(double gameY) {
            return offsetY + gameY * invertedScaleY;
        }

        @Override
        public double toScreenSizeW(double gameW) {
            return gameW * invertedScaleX;
        }

        @Override
        public double toScreenSizeH(double gameH) {
            return gameH * invertedScaleY;
        }

        @Override
        public double toGameLocationX(double screenX) {
            return (screenX - offsetX) * scaleX;
        }

        @Override
        public double toGameLocationY(double screenY) {
            return (screenY - offsetY) * scaleY;
        }

        @Override
        public void drawRect(double x, double y, double width, double height, boolean fill) {
            setSubPixelAccuracy(fill || (width > 10 && height > 10));
            rect.setRect(x, y, width, height);
            drawShape(rect, fill);
        }

        @Override
        public void drawOval(double x, double y, double width, double height, boolean fill) {
            setSubPixelAccuracy(true);
            ellipse.setFrame(x, y, width, height);
            drawShape(ellipse, fill);
        }

        @Override
        public void drawPoly(PolyType type, @NotNull Point... points) {
            setSubPixelAccuracy(true);
            if (points.length < 2) return;

            path.reset();
            boolean first = true;
            for (Point point : points) {
                if (first) {
                    path.moveTo(point.getX(), point.getY());
                    first = false;
                } else path.lineTo(point.getX(), point.getY());
            }
            if (type != PolyType.DRAW_POLYLINE)
                path.closePath();

            drawShape(path, type == PolyType.FILL_POLYGON);
        }

        @Override
        public void drawLine(double x1, double y1, double x2, double y2) {
            setSubPixelAccuracy(true);
            line.setLine(x1, y1, x2, y2);
            g2.draw(line);
        }
    }
}
