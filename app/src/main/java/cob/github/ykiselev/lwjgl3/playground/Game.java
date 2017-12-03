package cob.github.ykiselev.lwjgl3.playground;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.SpriteBatch;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Game implements WindowCallbacks, AutoCloseable {

    private final SpriteBatch spriteBatch;

    // frame buffer width
    private int width;

    // frame buffer height
    private int height;

    public Game(Assets assets) {
        spriteBatch = new SpriteBatch(
                assets.load("/progs/sprite-batch.conf", ProgramObject.class)
        );
    }

    @Override
    public void keyEvent(int key, int scanCode, int action, int mods) {
        //todo
    }

    @Override
    public void cursorEvent(double x, double y) {
        //todo
    }

    @Override
    public void mouseButtonEvent(int button, int action, int mods) {
        //todo
    }

    @Override
    public void frameBufferEvent(int width, int height) {
        this.width = width;
        this.height = height;
        // todo
    }

    @Override
    public void close() {
        spriteBatch.close();
    }

    public void update() {

    }
}
