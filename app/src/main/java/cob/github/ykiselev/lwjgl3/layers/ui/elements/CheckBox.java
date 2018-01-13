package cob.github.ykiselev.lwjgl3.layers.ui.elements;

import cob.github.ykiselev.lwjgl3.layers.DrawingContext;
import cob.github.ykiselev.lwjgl3.layers.ui.UiElement;
import cob.github.ykiselev.lwjgl3.layers.ui.models.checkbox.CheckBoxModel;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CheckBox implements UiElement {

    private final CheckBoxModel model;

    public CheckBox(CheckBoxModel model) {
        this.model = requireNonNull(model);
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            switch (key) {
                case GLFW_KEY_LEFT:
                case GLFW_KEY_RIGHT:
                case GLFW_KEY_SPACE:
                    model.flip();
                    return true;
            }
        }
        return false;
    }

    @Override
    public int draw(int x, int y, int width, DrawingContext ctx) {
        return ctx.draw(x, y, width, "[" + (model.checked() ? "X" : " ") + "]", 0xffffffff);
    }
}
