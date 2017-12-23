package cob.github.ykiselev.lwjgl3.layers;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Menu implements UiLayer {

    private UiLayers layers;

    private final SpriteBatch spriteBatch;

    private final Texture2d cuddles;

    private final SpriteFont liberationMono;

    public Menu(Assets assets) {
        spriteBatch = new SpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        cuddles = assets.load("images/htf-cuddles.jpg", Texture2d.class);
        liberationMono = assets.load("fonts/Liberation Mono.sf", SpriteFont.class);
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
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
            layers.pop(this);
        }
        return true;
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
        spriteBatch.end();
    }
}
