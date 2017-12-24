package cob.github.ykiselev.lwjgl3.layers.menu;


import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;

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

    private double cx, cy;

    public Link(String name, SpriteFont font) {
        this.name = name;
        this.font = font;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean cursorEvent(double x, double y) {
        cx = x;
        cy = y;
        return false;
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (key == GLFW_KEY_ENTER) {
                // todo got ot link
                return true;
            }
        }
        return false;
    }

    @Override
    public int draw(int x, int y, int width, SpriteBatch sb) {
        int color = 0xffffffff;
        if (cx > x && cx < x + width && cy > y && cy < y + font.fontHeight()){
            color = 0xffff00ff;
        }
        return sb.draw(font, x, y, name, width, color);
    }
}
