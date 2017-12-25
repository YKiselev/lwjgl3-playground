package cob.github.ykiselev.lwjgl3.layers.menu;


import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * Menu item which is represented on UI as text. When "activated" executes some action (usually shows sub-menu).
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Link implements MenuItem {

    private final String name;

    private final SpriteFont font;

    private final Runnable action;

    private double cx, cy;

    public Link(String name, Runnable action, SpriteFont font) {
        this.name = requireNonNull(name);
        this.action = requireNonNull(action);
        this.font = requireNonNull(font);
    }

    @Override
    public String name() {
        return name;
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
    public void frameBufferResized(int width, int height) {
    }

    @Override
    public int draw(int x, int y, int width, SpriteBatch sb) {
        int color = 0xffffffff;
        if (cx > x && cx < x + width && cy > y && cy < y + font.fontHeight()) {
            color = 0xffff00ff;
        }
        return sb.draw(font, x, y, name, width, color);
    }
}
