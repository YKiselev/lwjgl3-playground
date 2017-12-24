package cob.github.ykiselev.lwjgl3.layers;

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

    private final Texture2d cuddles;

    private final SpriteFont font;

    private final List<String> items = Arrays.asList("New", "Exit");

    private int selected = 0;

    public Menu(Assets assets) {
        spriteBatch = new SpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        cuddles = assets.load("images/htf-cuddles.jpg", Texture2d.class);
        font = assets.load("fonts/GENUINE.sf", SpriteFont.class);
    }

    @Override
    public void close() {
        spriteBatch.close();
    }

    @Override
    public void attach(UiLayers layers) {
        this.layers = requireNonNull(layers);
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
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
        return false;
    }

    @Override
    public boolean cursorEvent(double x, double y) {
        return true;
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        return true;
    }

    @Override
    public void draw(int width, int height) {
        spriteBatch.begin(0, 0, width, height, true);
        spriteBatch.draw(cuddles, 0, 0, width, height, 0xffffffff);

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
        for (String item : items) {
            if (i == selected) {
                spriteBatch.draw(font, x - cursorWidth, y, "\u23F5", maxWidth, 0xffffffff);
            }
            spriteBatch.draw(font, x, y, item, maxWidth, 0xffffffff);
            i++;
            y -= font.fontHeight();
        }

        spriteBatch.end();
    }
}
