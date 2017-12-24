package cob.github.ykiselev.lwjgl3.layers.menu;

import cob.github.ykiselev.lwjgl3.playground.WindowEvents;
import com.github.ykiselev.opengl.sprites.SpriteBatch;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface MenuItem extends WindowEvents {

    String name();

    int draw(int x, int y, int width, SpriteBatch spriteBatch);

    @Override
    default boolean keyEvent(int key, int scanCode, int action, int mods) {
        return false;
    }

    @Override
    default boolean cursorEvent(double x, double y) {
        return false;
    }

    @Override
    default boolean mouseButtonEvent(int button, int action, int mods) {
        return false;
    }
}
