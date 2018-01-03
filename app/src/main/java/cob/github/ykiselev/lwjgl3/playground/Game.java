package cob.github.ykiselev.lwjgl3.playground;

import cob.github.ykiselev.lwjgl3.events.SubscriberGroup;
import cob.github.ykiselev.lwjgl3.events.SubscriberGroupBuilder;
import cob.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import cob.github.ykiselev.lwjgl3.host.Host;
import cob.github.ykiselev.lwjgl3.layers.UiLayer;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.io.FileSystem;
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
import com.github.ykiselev.trigger.Trigger;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.channels.WritableByteChannel;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PRINT_SCREEN;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
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

    private final FloatBuffer pv;

    private final GenericIndexedGeometry pyramid;

    private final GenericIndexedGeometry cubes;


    private float radius = 8;

    private float cameraZ = -0.5f;

    private boolean lmbPressed, rmbPressed;

    private double cx, cy, cx0, cy0;

    private long frames;

    private final UniformVariable texUniform;

    private final FrameBuffer frameBuffer;

    private final Trigger rmbTrigger = new Trigger(
            () -> {
                cx0 = cx;
                cy0 = cy;
            },
            null
    );

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

        pv = MemoryUtil.memAllocFloat(16);

        frameBuffer = new FrameBuffer();
    }


    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (action == GLFW_PRESS) {
            switch (key) {
                case GLFW_KEY_ESCAPE:
                    host.events()
                            .send(new ShowMenuEvent());
                    break;

                case GLFW_KEY_PRINT_SCREEN:
                    try {
                        dumpToFile(frameBuffer.color(), "color.png");
                        dumpToFile(frameBuffer.depth(), "depth.png");
                    } catch (IOException e) {
                        logger.error("Unable to save image!", e);
                    }
                    break;
            }
        }
        return true;
    }

    private void dumpToFile(Texture2d texture, String name) throws IOException {
        final FileSystem fs = host.services().resolve(FileSystem.class);
        try (WritableByteChannel channel = fs.openForWriting(name, false)) {
            texture.save(channel);
        }
    }

    @Override
    public void cursorEvent(double x, double y) {
        cx = x;
        cy = y;
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        switch (button) {
            case GLFW_MOUSE_BUTTON_LEFT:
                lmbPressed = (action == GLFW_PRESS);
                break;

            case GLFW_MOUSE_BUTTON_RIGHT:
                rmbPressed = action == GLFW_PRESS;
                rmbTrigger.value(action == GLFW_PRESS);
                break;
        }
        return true;
    }

    @Override
    public void frameBufferResized(int width, int height) {
        //todo
    }

    @Override
    public boolean scrollEvent(double dx, double dy) {
        radius += dy;
        return true;
    }

    @Override
    public void close() {
        spriteBatch.close();
        pyramid.close();
        cubes.close();
        MemoryUtil.memFree(pv);
        frameBuffer.close();
        group.close();
    }

    private void drawPyramids(FloatBuffer vp) {
        final double sec = System.currentTimeMillis() / 1000.0;
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer rm = ms.mallocFloat(16);
            Matrix.rotation(0, 0, Math.toRadians(25 * sec % 360), rm);

            final FloatBuffer mvp = ms.mallocFloat(16);

            // 1
            Matrix.multiply(vp, rm, mvp);
            pyramid.draw(mvp);

            // 2
            Matrix.translate(vp, 2, 0, 0, mvp);
            Matrix.scale(mvp, 3, 3, 3, mvp);
            Matrix.multiply(mvp, rm, mvp);
            pyramid.draw(mvp);

            // 3
            Matrix.translate(vp, -2, 0, 0, mvp);
            Matrix.multiply(mvp, rm, mvp);
            pyramid.draw(mvp);
        }
    }

    private void drawModel(FloatBuffer vp) {
        final double sec = System.currentTimeMillis() / 1000.0;
        texUniform.value(0);
        cuddles.bind();
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer rm = ms.mallocFloat(16);
            Matrix.rotation(0, 0, Math.toRadians(25 * sec % 360), rm);

            final FloatBuffer mvp = ms.mallocFloat(16);
            Matrix.multiply(vp, rm, mvp);

            cubes.draw(mvp);
        }
        cuddles.unbind();
    }

    private void setupProjectionViewMatrix(int width, int height) {
        Matrix.perspective(
                (float) Math.toRadians(90),
                (float) width / height,
                0.1f,
                15,
                pv
        );
        if (radius < 1) {
            radius = 1;
        }
        if (rmbPressed) {
            cameraZ = (float) Math.max(-4, Math.min(4, 0.1 * (cy - cy0)));
        }
        final double sec = System.currentTimeMillis() / 1000.0;
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer view = ms.mallocFloat(16);
            final double beta = Math.toRadians(15.0 * sec % 360);
            final float x = (float) (Math.sin(beta) * radius);
            final float y = (float) (Math.cos(beta) * radius);
            Matrix.lookAt(
                    new Vector3f(0, 0, 0),
                    new Vector3f(x, y, cameraZ),
                    new Vector3f(0, 0, 1),
                    view
            );
            Matrix.multiply(pv, view, pv);
        }
    }

    @Override
    public void draw(int width, int height) {
        frameBuffer.size(width, height);

        glViewport(0, 0, width, height);
        glFrontFace(GL_CCW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_SCISSOR_TEST);
        glDisable(GL_STENCIL_TEST);
        glDepthFunc(GL_LESS);
        glDepthMask(true);

        setupProjectionViewMatrix(width, height);

        frameBuffer.bind();

        glClearDepth(100.0f);
        glClearColor(0, 0, 0.5f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        drawPyramids(pv);
        drawModel(pv);
        frameBuffer.unbind();

        final double t = glfwGetTime();
        final double fps = (double) frames / t;

        glClearColor(0.5f, 0.5f, 0.5f, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        spriteBatch.begin(0, 0, width, height, true);
        // show color buffer
        spriteBatch.draw(frameBuffer.color(), 0, 0, width / 2, height, 0xffffffff);
        // show depth buffer
        spriteBatch.draw(frameBuffer.depth(), width / 2, 0, width / 2, height, 0xffffffff);

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