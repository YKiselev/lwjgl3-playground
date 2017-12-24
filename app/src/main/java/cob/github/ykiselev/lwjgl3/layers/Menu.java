package cob.github.ykiselev.lwjgl3.layers;

import cob.github.ykiselev.lwjgl3.layers.menu.Link;
import cob.github.ykiselev.lwjgl3.layers.menu.MenuItem;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Menu implements UiLayer {

    private UiLayers layers;

    private final SpriteBatch spriteBatch;

    private final Texture2d white;

    private final SpriteFont font;

    private final List<MenuItem> items;

    private int selected = 0;

    public Menu(Assets assets) {
        spriteBatch = new SpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        white = assets.load("images/white.png", Texture2d.class);
        font = assets.load("fonts/GENUINE.sf", SpriteFont.class);
        items = Arrays.asList(
                new Link("New", font),
                new Link("Exit", font)
        );
    }

    @Override
    public void close() {
        spriteBatch.close();
    }

    @Override
    public void attach(UiLayers layers) {
        this.layers = requireNonNull(layers);
    }

    private MenuItem selectedItem() {
        return items.get(selected);
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (!selectedItem().keyEvent(key, scanCode, action, mods)) {
            if (action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_ESCAPE:
                        layers.pop(this);
                        break;

                    case GLFW_KEY_UP:
                        selected--;
                        if (selected < 0) {
                            selected = items.size() - 1;
                        }
                        break;

                    case GLFW_KEY_DOWN:
                        selected++;
                        if (selected >= items.size()) {
                            selected = 0;
                        }
                        break;
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean cursorEvent(double x, double y) {
        for (MenuItem item : items) {
            item.cursorEvent(x, y);
        }
        return true;
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        if (!selectedItem().mouseButtonEvent(button, action, mods)) {
            // todo
        }
        return true;
    }

    @Override
    public void draw(int width, int height) {
        spriteBatch.begin(0, 0, width, height, true);
        spriteBatch.draw(white, 0, 0, width, height, 0x000030df);

        final int cursorWidth;
        final Glyph glyph = font.findGlyph((char) 0x23f5);
        if (glyph != null) {
            cursorWidth = glyph.width();
        } else {
            cursorWidth = 0;
        }

        final int x = 10 + cursorWidth;
        final int maxWidth = width - x;
        int y = height / 2 + items.size() * font.fontHeight() / 2 - font.fontHeight();
        int i = 0;
        for (MenuItem item : items) {
            int dx = 0;
            if (i == selected) {
                spriteBatch.draw(font, x - cursorWidth, y, "\u23F5", maxWidth, 0xffffffff);
                dx = 4;
            }
            y -= item.draw(x + dx, y, maxWidth, spriteBatch);
            i++;
        }

        spriteBatch.end();
    }
}
