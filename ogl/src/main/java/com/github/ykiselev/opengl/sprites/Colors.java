package com.github.ykiselev.opengl.sprites;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Colors {

    public static final int WHITE = 0xffffffff;

    public static int rgb(int r, int g, int b) {
        return rgba(r, g, b, 0xff);
    }

    public static int rgba(int r, int g, int b, int a) {
        return ((r & 0xff) << 24) | ((g & 0xff) << 16) | ((b & 0xff) << 8) | a & 0xff;
    }

    public static int red(int rgba) {
        return (rgba >>> 24) & 0xff;
    }

    public static int green(int rgba) {
        return (rgba >>> 16) & 0xff;
    }

    public static int blue(int rgba) {
        return (rgba >>> 8) & 0xff;
    }

    public static int alpha(int rgba) {
        return rgba & 0xff;
    }

    /**
     * Calculates brightness of a color.
     *
     * @param color the color
     * @return the brightness level 0..1
     */
    public static float brightness(int color) {
        float max = Math.max(
                Math.max(red(color), green(color)),
                blue(color)
        );
        return Math.min(
                1,
                Math.max(0, max / 255f)
        );
    }

    /**
     * Changes color brightness.
     *
     * @param color      the source color
     * @param brightness the brightness level 0..1
     * @return changed color
     */
    public static int dim(int color, float brightness) {
        final int alpha = alpha(color);
        if (brightness == 0) {
            return rgba(0, 0, 0, alpha);
        }
        if (brightness > 1f) {
            brightness = 1f;
        }
        final float max = brightness(color);
        if (max == 0) {
            return rgba(0, 0, 0, alpha);
        }
        final float oom = 1f / (255f * max);
        float r = red(color) * oom;
        float g = green(color) * oom;
        float b = blue(color) * oom;
        return rgba(
                (int) (brightness * r * 255f),
                (int) (brightness * g * 255f),
                (int) (brightness * b * 255f),
                alpha
        );
    }

    public static int fade(int color, float opacity) {
        if (opacity < 0) {
            opacity = 0;
        }
        if (opacity > 1f) {
            opacity = 1f;
        }
        return color & 0xffffff00 | (int) (opacity * 255f);
    }
}
