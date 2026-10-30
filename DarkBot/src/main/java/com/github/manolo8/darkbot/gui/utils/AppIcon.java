package com.github.manolo8.darkbot.gui.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/** The application icon: a violet cat face, drawn in code so no image resource is needed. */
public final class AppIcon {

    private AppIcon() {
    }

    public static Image create(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.scale(size / 256.0, size / 256.0);

        // background tile
        g.setPaint(new GradientPaint(0, 0, new Color(0x1B1F2E), 0, 256, new Color(0x0D0F15)));
        g.fill(new RoundRectangle2D.Double(0, 0, 256, 256, 56, 56));

        Color furDark = new Color(0x6D3FE0);

        // ears
        g.setColor(furDark);
        g.fill(triangle(50, 132, 70, 40, 122, 86));
        g.fill(triangle(206, 132, 186, 40, 134, 86));
        g.setColor(new Color(0xF5A3C7));
        g.fill(triangle(66, 116, 76, 62, 106, 88));
        g.fill(triangle(190, 116, 180, 62, 150, 88));

        // head
        g.setPaint(new GradientPaint(0, 70, new Color(0xA78BFA), 0, 220, new Color(0x7C3AED)));
        g.fill(new Ellipse2D.Double(36, 74, 184, 280 - 70 - 70 - 0));

        // eyes
        g.setColor(new Color(0xFDE68A));
        g.fill(new Ellipse2D.Double(80, 122, 38, 50));
        g.fill(new Ellipse2D.Double(138, 122, 38, 50));
        g.setColor(new Color(0x0D0F15));
        g.fill(new Ellipse2D.Double(94, 126, 10, 42));
        g.fill(new Ellipse2D.Double(152, 126, 10, 42));
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(96, 132, 5, 9));
        g.fill(new Ellipse2D.Double(154, 132, 5, 9));

        // nose + mouth
        g.setColor(new Color(0xFB7185));
        g.fill(triangle(118, 182, 138, 182, 128, 194));
        g.setColor(new Color(0x0D0F15));
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D mouth = new Path2D.Double();
        mouth.moveTo(128, 194);
        mouth.curveTo(128, 208, 112, 212, 104, 204);
        mouth.moveTo(128, 194);
        mouth.curveTo(128, 208, 144, 212, 152, 204);
        g.draw(mouth);

        // whiskers
        g.setColor(new Color(255, 255, 255, 190));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(66, 186, 28, 178);
        g.drawLine(66, 198, 26, 202);
        g.drawLine(190, 186, 228, 178);
        g.drawLine(190, 198, 230, 202);

        g.dispose();
        return img;
    }

    private static Path2D triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        Path2D p = new Path2D.Double();
        p.moveTo(x1, y1);
        p.lineTo(x2, y2);
        p.lineTo(x3, y3);
        p.closePath();
        return p;
    }
}
