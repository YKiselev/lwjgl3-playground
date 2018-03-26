package com.github.ykiselev.lwjgl3.layers.ui.elements;


import com.github.ykiselev.lwjgl3.layers.DrawingContext;
import com.github.ykiselev.lwjgl3.layers.ui.UiElement;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * Menu item which is represented on UI as text. When "activated" executes some action (usually shows sub-menu).
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Link implements UiElement {

    private final String name;

    private final Runnable action;

    private double cx, cy;

    public Link(String name, Runnable action) {
        this.name = requireNonNull(name);
        this.action = requireNonNull(action);
    }

    @Override
    public void cursorEvent(double x, double y) {
        cx = x;
        cy = y;
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (key == GLFW_KEY_ENTER) {
                this.action.run();
                return true;
            }
        }
        return false;
    }

    @Override
    public int draw(int x, int y, int width, DrawingContext ctx) {
        int color = 0xffffffff;
        if (cx > x && cx < x + width && cy < y && cy > y - ctx.font().fontHeight()) {
            color = 0xffff00ff;
        }
        return ctx.draw(x, y, width, name, color);
    }
}
