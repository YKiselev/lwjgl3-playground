package cob.github.ykiselev.lwjgl3.playground;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;
import org.lwjgl.opengl.GL13;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Game implements WindowCallbacks, AutoCloseable {

    private final SpriteBatch spriteBatch;

    private final Texture2d cuddles;

    private final SpriteFont liberationMono;

    // frame buffer width
    private int width;

    // frame buffer height
    private int height;

    private double t0 = glfwGetTime();

    private double x = 0;

    private double k = 50;

    private double scale = 1.0;

    private long frames;

    public Game(Assets assets) {
        spriteBatch = new SpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        cuddles = assets.load("images/htf-cuddles.jpg", Texture2d.class);
        liberationMono = assets.load("fonts/Liberation Mono.sf", SpriteFont.class);
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
        glFrontFace(GL_CCW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL13.GL_MULTISAMPLE);

        glClearDepth(1.0f);
        glClearColor(0, 0, 0.5f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (x > width) {
            x = width;
            k *= -1.0;
        } else if (x < 0) {
            x = 0;
            k *= -1.0;
        }
        if (scale < 0.1){
            scale = 1.0;
        }
        final double t = glfwGetTime();
        final double fps = (double) frames / t;
        final double delta = t - t0;
        t0 = t;
        spriteBatch.begin(0, 0, width, height, true);
        x += k * delta;
        scale *= (1 - 0.25*delta);
        spriteBatch.draw(cuddles, (int) x, 4, (int)(400 * scale), (int)(400 * scale), 0xffffffff);

        //spriteBatch.draw(liberationMono.texture(), 0, 0, 256, 128, 0xffffffff);
//        spriteBatch.draw(liberationMono.texture(), 0, -200, 400, 400, 0xffffffff);
//        spriteBatch.draw(liberationMono.texture(), -100, -50, 400, 400, 0xffffffff);
//        spriteBatch.draw(liberationMono.texture(), -100, 50, 400, 400, 0xffffffff);
//        spriteBatch.draw(liberationMono.texture(), 100, -350, 400, 400, 0xffffffff);
        //spriteBatch.draw(liberationMono, 250, 200, "ABCD EFGH IJKL", 200, 0xff00ffff);
        //spriteBatch.draw(liberationMono, 250, 230, "Ерунда какая-то получается, правда? 01234567890-=+./ёЁ", 400, 0xff00ffff);
        //spriteBatch.draw(liberationMono, 10, 400, "Hello, world!\nПривет!", 200, 0xffffffff);

        spriteBatch.draw(
                liberationMono,
                0,
                height - liberationMono.fontHeight(),
                String.format("avg. fps: %.2f", fps),
                width,
                0xffffffff
        );

        spriteBatch.end();
        frames++;
    }
}
