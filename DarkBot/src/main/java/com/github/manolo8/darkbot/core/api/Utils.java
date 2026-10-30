package com.github.manolo8.darkbot.core.api;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.BotInstaller;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.core.utils.ByteUtils;
import com.github.manolo8.darkbot.core.utils.pathfinder.RectangleImpl;
import com.github.manolo8.darkbot.utils.MathUtils;

import java.util.Set;

public class Utils {
    public static final String SELECT_MAP_ASSET = "MapAssetNotificationTRY_TO_SELECT_MAPASSET";

    /** Map-units per screen pixel when the game's view bounds can't be read (2D view at default zoom is ~1:1). */
    public static final double FALLBACK_UNITS_PER_PIXEL = 1.0;
    /** Roughly the delay between the bot reading a position and the game processing the click. */
    private static final double CLICK_LEAD_SECONDS = 0.1;

    /**
     * The map area currently visible in the game window as {x, y, width, height}. Normally read from game memory;
     * some game builds keep it elsewhere (reads come back as zeros), so then it is estimated as the window area
     * centered on the hero. Null if neither is usable.
     */
    public static double[] viewRect() {
        RectangleImpl vb = Main.INSTANCE.mapManager.viewBounds.polygon.getBounds();
        double bw = vb.getWidth(), bh = vb.getHeight();
        if (bw > 100 && bw < 100_000 && bh > 100 && bh < 100_000) return new double[]{vb.getX(), vb.getY(), bw, bh};

        int cw = MapManager.clientWidth, ch = MapManager.clientHeight;
        if (cw < 100 || cw > 16384 || ch < 100 || ch > 16384) return null;
        double hx = Main.INSTANCE.hero.locationInfo.now.x, hy = Main.INSTANCE.hero.locationInfo.now.y;
        if (Double.isNaN(hx) || Double.isNaN(hy)) return null;
        double w = cw * FALLBACK_UNITS_PER_PIXEL, h = ch * FALLBACK_UNITS_PER_PIXEL;
        return new double[]{hx - w / 2, hy - h / 2, w, h};
    }

    public static SelectEntityClick createSelectEntityClick(Entity entity) {
        double[] view = viewRect();
        if (view == null) throw new IllegalStateException("Game view size unknown");
        double bx = view[0], by = view[1], bw = view[2], bh = view[3];
        int cw = MapManager.clientWidth, ch = MapManager.clientHeight;

        // Aim where the ship will be when the click lands (click + game round trip), not where it was last seen.
        double ex = entity.getX(), ey = entity.getY();
        if (entity instanceof com.github.manolo8.darkbot.core.entities.Ship) {
            com.github.manolo8.darkbot.core.entities.Ship ship = (com.github.manolo8.darkbot.core.entities.Ship) entity;
            java.util.Optional<eu.darkbot.api.game.other.Location> dest = ship.getDestination();
            if (dest.isPresent()) {
                double dx = dest.get().getX() - ex, dy = dest.get().getY() - ey, len = Math.sqrt(dx * dx + dy * dy);
                if (len > 1) {
                    double lead = Math.min(len, ship.getSpeed() * CLICK_LEAD_SECONDS);
                    ex += dx / len * lead;
                    ey += dy / len * lead;
                }
            }
        }

        int entityScreenX = softClamp(translate(ex - bx, bw, cw), cw);
        int entityScreenY = softClamp(translate(ey - by, bh, ch), ch);

        // Near-center aim with a little jitter: a miss costs a whole retry, which is far slower than precision.
        double angle = Math.random() * MathUtils.TAU;
        double dist = Math.random() * 0.25 * entity.clickable.defRadius;

        int diffX = translate(Math.cos(angle) * dist, bw, cw);
        int diffY = translate(Math.sin(angle) * dist, bh, ch);

        return new SelectEntityClick(
                entityScreenX + diffX,
                entityScreenY + diffY,
                entityScreenX,
                entityScreenY);
    }

    public static long[] createSelectEntityArgs(Entity entity) {
        SelectEntityClick click = createSelectEntityClick(entity);

        return tagIntegers(
                entity.getId(),
                (int) entity.getX(), (int) entity.getY(),
                click.clickX, click.clickY,
                click.entityScreenX, click.entityScreenY,
                entity.clickable.defRadius);
    }

    public static class SelectEntityClick {
        public final int clickX;
        public final int clickY;
        public final int entityScreenX;
        public final int entityScreenY;

        public SelectEntityClick(int clickX, int clickY, int entityScreenX, int entityScreenY) {
            this.clickX = clickX;
            this.clickY = clickY;
            this.entityScreenX = entityScreenX;
            this.entityScreenY = entityScreenY;
        }
    }

    private static long[] tagIntegers(int... args) {
        long[] tagged = new long[args.length];
        for (int i = 0; i < args.length; i++) {
            tagged[i] = ByteUtils.tagInteger(args[i]);
        }

        return tagged;
    }

    /**
     * Translate between coordinate systems
     *
     * @param value The value to transform, in map coordinates
     * @param mapSize In-game map width/height, in map coordinates
     * @param clientSize Window width/height, in pixels
     * @return value converted to
     */
    private static int translate(double value, double mapSize, int clientSize) {
        return (int) (value / mapSize * (double) clientSize);
    }

    /**
     * Ensure val is between 0 and max. If it isn't, soft clamp to 0-5% of appropriate corner
     * @param val The value to clamp
     * @param max The window in which to clamp the value
     * @return value if between 0 and max, otherwise 0 + 5% of max or max - 5% of max, randomly.
     */
    private static int softClamp(int val, int max) {
        if (val >= 0 && val <= max) return val;
        int offset = (int) (Math.random() * max * 0.05);
        return val < 0 ? offset : max - offset;
    }

    public interface SignatureChecker extends GameAPI.DirectInteraction {

        Set<String> signatureCache();

        default boolean checkGotoMethod(GameAPI.Memory mem, BotInstaller installer) {
            String signature = "26(267726?2?)42407911700";
            if (signatureCache().contains(signature)) return true;

            long eventManager = mem.readLong(installer.screenManagerAddress.get(), 200);
            return checkSignature(false, signature, 10, eventManager);
        }

        default boolean checkSignature(boolean checkName, String signature, int index, long object) {
            if (index <= 2 || !ByteUtils.isValidPtr(object)) return false;
            if (!ByteUtils.isScriptableObjectValid(object)) return false;
            if (!signatureCache().contains(signature)) {
                // native check crashes if called on still-settling objects right after reload, wait it out
                if (!BotInstaller.isSettled()) return false;

                // -1 or -2 == memory read error, 0 == invalid signature, 1 == valid
                int ret = checkMethodSignature(object, index, checkName, signature);

                if (ret == 1) signatureCache().add(signature);
                else if (ret == 0) {
                    throw new InvalidNativeSignature("Invalid flash method signature! " + signature);
                } else return false;
            }

            return true;
        }
    }

}
