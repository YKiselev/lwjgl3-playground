package com.github.ykiselev.lwjgl3.layers.ui;

import com.github.ykiselev.services.layers.DrawingContext;
import com.github.ykiselev.window.WindowEvents;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiElement extends WindowEvents {

    /**
     * @param x     the x coordinate to start drawing at
     * @param y     the y coordinate to start drawing at
     * @param width the width of drawing area
     * @param ctx   the drawing context to use
     * @return the actual height of area occupied by ui element
     */
    int draw(int x, int y, int width, DrawingContext ctx);

    boolean isEnabled();

    void enable(boolean value);

    boolean isVisible();

    void visible(boolean value);
}
