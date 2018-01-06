package cob.github.ykiselev.lwjgl3.layers.menu;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Slider implements MenuItem {

    private final String title;

    private final int minValue;

    private final int maxValue;

    private final int step;

    private int value;

    public int value() {
        return value;
    }

    public void value(int value) {
        if (value < minValue) {
            value = minValue;
        }
        if (value > maxValue) {
            value = maxValue;
        }
        this.value = value;
    }

    public Slider(String title, int minValue, int maxValue, int step) {
        this.title = requireNonNull(title);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
        value(0);
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            switch (key) {
                case GLFW_KEY_LEFT:
                    value(value - step);
                    return true;

                case GLFW_KEY_RIGHT:
                    value(value + step);
                    return true;
            }
        }
        return false;
    }

    @Override
    public int draw(int x, int y, int width, MenuDrawingContext ctx) {
        final StringBuilder sb = new StringBuilder(title);
        sb.append(" [");
        for (int k = minValue; k <= maxValue; k += step) {
            if (k < value || k > value) {
                sb.append('-');
            } else {
                sb.append('|');
            }
        }
        sb.append("] ");
        final int start = sb.length();
        sb.append(maxValue);
        final int target = sb.length();
        sb.setLength(start);
        sb.append(value);
        while (sb.length() < target) {
            sb.insert(start, ' ');
        }
        return ctx.draw(x, y, width, sb, 0xffffffff);
    }
}
