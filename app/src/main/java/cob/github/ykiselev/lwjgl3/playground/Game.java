package cob.github.ykiselev.lwjgl3.playground;

import cob.github.ykiselev.lwjgl3.events.SubscriberGroup;
import cob.github.ykiselev.lwjgl3.events.SubscriberGroupBuilder;
import cob.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import cob.github.ykiselev.lwjgl3.host.Host;
import cob.github.ykiselev.lwjgl3.layers.UiLayer;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.matrices.Vector3f;
import com.github.ykiselev.opengl.models.GenericIndexedGeometry;
import com.github.ykiselev.opengl.models.Pyramid;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.opengl.vertices.VertexDefinitions;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;
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
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
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

    private final FloatBuffer projection;

    private final GenericIndexedGeometry pyramid;

    private final GenericIndexedGeometry cubes;

    private double t0 = glfwGetTime();

    private double x = 0;

    private double k = 50;

    private double scale = 1.0;

    private long frames;

    private final UniformVariable texUniform;

    public Game(Host host, Assets assets) {
        this.host = host;
        this.group = new SubscriberGroupBuilder()
                .build(host.events());
        spriteBatch = new SpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        cuddles = assets.load("images/htf-cuddles.jpg", Texture2d.class);
        liberationMono = assets.load("fonts/Liberation Mono.sf", SpriteFont.class);

        final ProgramObject generic = assets.load("progs/generic.conf", ProgramObject.class);
        final ObjModel model = assets.load("models/2cubes.obj", ObjModel.class);
        cubes = new GenericIndexedGeometry(
                generic,
                VertexDefinitions.POSITION_TEXTURE_NORMAL,
                model.toIndexedTriangles()
        );
        texUniform = generic.lookup("tex");

        final ProgramObject colored = assets.load("progs/colored.conf", ProgramObject.class);
        try (Pyramid p = new Pyramid()) {
            pyramid = new GenericIndexedGeometry(
                    colored,
                    VertexDefinitions.POSITION_COLOR,
                    p
            );
        }

        projection = MemoryUtil.memAllocFloat(16);
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
        pyramid.close();
        cubes.close();
        MemoryUtil.memFree(projection);
        group.unsubscribe();
    }

    private void drawPyramids(){
        final double sec = System.currentTimeMillis() / 1000.0;
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer view = ms.mallocFloat(16);
            final double beta = Math.toRadians(15.0 * sec % 360);
            final float radius = 4;
            final float x = (float) (Math.sin(beta) * radius);
            final float z = (float) (Math.cos(beta) * radius);
            Matrix.lookAt(
                    new Vector3f(0, 0, 0),
                    new Vector3f(x, z, -0.5f),
                    new Vector3f(0, 0, 1),
                    view
            );

            final FloatBuffer vp = ms.mallocFloat(16);
            Matrix.multiply(projection, view, vp);

            final FloatBuffer rm = ms.mallocFloat(16);
            Matrix.rotation(0, 0, Math.toRadians(25 * sec % 360), rm);

            final FloatBuffer mvp = ms.mallocFloat(16);

            // 1
            Matrix.multiply(vp, rm, mvp);
            pyramid.draw(mvp);

            // 2
            Matrix.translate(vp, 1, 1, 0.5f, mvp);
            Matrix.scale(mvp, 2, 2, 2, mvp);
            Matrix.multiply(mvp, rm, mvp);
            pyramid.draw(mvp);

            // 3
            Matrix.translate(vp, -1, -1, -0.5f, mvp);
            Matrix.multiply(mvp, rm, mvp);
            pyramid.draw(mvp);
        }
    }

    @Override
    public void draw(int width, int height) {
        glViewport(0, 0, width, height);
        Matrix.perspective(
                (float) Math.toRadians(90),
                (float) width / height,
                0.1f,
                100,
                projection
        );

        glFrontFace(GL_CCW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glEnable(GL13.GL_MULTISAMPLE);

        glClearDepth(1.0f);
        glClearColor(0, 0, 0.5f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //drawPyramids();

        final double sec = System.currentTimeMillis() / 1000.0;
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer view = ms.mallocFloat(16);
            final double beta = Math.toRadians(15.0 * sec % 360);

            final float radius = 8;
            final float x = (float) (Math.sin(beta) * radius);
            final float z = (float) (Math.cos(beta) * radius);
            Matrix.lookAt(
                    new Vector3f(0, 0, 0),
                    new Vector3f(x, z, -0.5f),
                    new Vector3f(0, 0, 1),
                    view
            );

            final FloatBuffer vp = ms.mallocFloat(16);
            Matrix.multiply(projection, view, vp);

            final FloatBuffer rm = ms.mallocFloat(16);
            Matrix.rotation(0, 0, Math.toRadians(25 * sec % 360), rm);

            final FloatBuffer mvp = ms.mallocFloat(16);

            texUniform.value(0);

            // 1
            Matrix.multiply(vp, rm, mvp);
            cuddles.bind();
            cubes.draw(mvp);

            // 2
//            Matrix.translate(vp, 1, 1, 0.5f, mvp);
//            Matrix.scale(mvp, 2, 2, 2, mvp);
//            Matrix.multiply(mvp, rm, mvp);
//            pyramid.draw(mvp);

            // 3
//            Matrix.translate(vp, -1, -1, -0.5f, mvp);
//            Matrix.multiply(mvp, rm, mvp);
//            pyramid.draw(mvp);

            cuddles.unbind();
        }

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
}