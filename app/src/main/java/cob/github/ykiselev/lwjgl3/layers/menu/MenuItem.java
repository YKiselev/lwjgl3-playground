package cob.github.ykiselev.lwjgl3.layers.menu;

import cob.github.ykiselev.lwjgl3.playground.WindowEvents;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface MenuItem extends WindowEvents {

    int draw(int x, int y, int width, MenuDrawingContext ctx);

    @Override
    default boolean keyEvent(int key, int scanCode, int action, int mods) {
        return false;
    }

    @Override
    default void cursorEvent(double x, double y) {
    }

    @Override
    default boolean mouseButtonEvent(int button, int action, int mods) {
        return false;
    }

    @Override
    default boolean scrollEvent(double dx, double dy) {
        return false;
    }

    default void frameBufferResized(int width, int height) {
    }
}
