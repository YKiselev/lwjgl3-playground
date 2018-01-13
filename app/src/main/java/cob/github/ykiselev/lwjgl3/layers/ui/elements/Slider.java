package cob.github.ykiselev.lwjgl3.layers.ui.elements;

import cob.github.ykiselev.lwjgl3.layers.DrawingContext;
import cob.github.ykiselev.lwjgl3.layers.ui.UiElement;
import cob.github.ykiselev.lwjgl3.layers.ui.models.SliderDefinition;
import cob.github.ykiselev.lwjgl3.layers.ui.models.SliderModel;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Slider implements UiElement {

    private final SliderModel model;

    public Slider(SliderModel model) {
        this.model = requireNonNull(model);
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            switch (key) {
                case GLFW_KEY_LEFT:
                    model.decrease();
                    return true;

                case GLFW_KEY_RIGHT:
                    model.increase();
                    return true;
            }
        }
        return false;
    }

    @Override
    public int draw(int x, int y, int width, DrawingContext ctx) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final int value = model.value();
        final SliderDefinition def = model.definition();
        for (int k = def.minValue(); k <= def.maxValue(); k += def.step()) {
            if (k < value || k > value) {
                sb.append('-');
            } else {
                sb.append('|');
            }
        }
        sb.append("]");
        final int start = sb.length();
        sb.append(def.maxValue());
        final int target = sb.length();
        sb.setLength(start);
        sb.append(value);
        while (sb.length() < target) {
            sb.insert(start, ' ');
        }
        return ctx.draw(x, y, width, sb, 0xffffffff);
    }
}
