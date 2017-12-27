package cob.github.ykiselev.lwjgl3.playground;

import cob.github.ykiselev.lwjgl3.events.SubscriberGroup;
import cob.github.ykiselev.lwjgl3.events.SubscriberGroupBuilder;
import cob.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import cob.github.ykiselev.lwjgl3.host.Host;
import cob.github.ykiselev.lwjgl3.layers.UiLayer;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.models.GenericIndexedGeometry;
import com.github.ykiselev.opengl.models.Pyramid;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.opengl.vertices.VertexDefinitions;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
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
import static org.lwjgl.opengl.GL11.glViewport;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Game implements UiLayer, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Host host;

    private final SubscriberGroup group;

    private final SpriteBatch spriteBatch;

    private final Texture2d cuddles;

    private final SpriteFont liberationMono;

    private final FloatBuffer matrix;

    private final GenericIndexedGeometry cubes;

    private double t0 = glfwGetTime();

    private double x = 0;

    private double k = 50;

    private double scale = 1.0;

    private long frames;

    public Game(Host host, Assets assets) {
        this.host = host;
        this.group = new SubscriberGroupBuilder()
                .build(host.events());
        spriteBatch = new SpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        cuddles = assets.load("images/htf-cuddles.jpg", Texture2d.class);
        liberationMono = assets.load("fonts/Liberation Mono.sf", SpriteFont.class);

        //final ObjModel model = assets.load("models/2cubes.obj", ObjModel.class);
        final ProgramObject program = assets.load("progs/colored.conf", ProgramObject.class);
        try (Pyramid p = new Pyramid()) {
            cubes = new GenericIndexedGeometry(program, VertexDefinitions.POSITION_COLOR, p);
        }
        //cubes = new GenericIndexedGeometry(program, model.toIndexedTriangles());
        matrix = MemoryUtil.memAllocFloat(16);
    }


    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
            host.events()
                    .send(new ShowMenuEvent());
        }
        return true;
    }

    @Override
    public void cursorEvent(double x, double y) {
        //todo
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        //todo
        return true;
    }

    @Override
    public void frameBufferResized(int width, int height) {
        //todo
    }

    @Override
    public void close() {
        spriteBatch.close();
        cubes.close();
        MemoryUtil.memFree(matrix);
        group.unsubscribe();
    }

    @Override
    public void draw(int width, int height) {
        glViewport(0, 0, width, height);
        Matrix.perspective((float) Math.toRadians(90), (float) width / height, 0.1f, 100, matrix);

        glFrontFace(GL_CCW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL13.GL_MULTISAMPLE);

        glClearDepth(1.0f);
        glClearColor(0, 0, 0.5f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        cubes.draw(matrix);

        final double t = glfwGetTime();
        final double fps = (double) frames / t;

        if (true) {
            if (x > width) {
                x = width;
                k *= -1.0;
            } else if (x < 0) {
                x = 0;
                k *= -1.0;
            }
            if (scale < 0.1) {
                scale = 1.0;
            }
            final double delta = t - t0;
            t0 = t;
            spriteBatch.begin(0, 0, width, height, true);
            x += k * delta;
            scale *= (1 - 0.25 * delta);
            //spriteBatch.draw(cuddles, (int) x, 4, (int) (400 * scale), (int) (400 * scale), 0xffffffff);

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
        }

        frames++;
    }

    public void update() {
    }
}