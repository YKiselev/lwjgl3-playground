package com.github.ykiselev.base.game;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.formats.obj.ObjModel;
import com.github.ykiselev.io.FileSystem;
import com.github.ykiselev.opengl.buffers.FrameBuffer;
import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.matrices.Vector3f;
import com.github.ykiselev.opengl.models.GenericIndexedGeometry;
import com.github.ykiselev.opengl.models.Pyramid;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import com.github.ykiselev.opengl.sprites.DefaultSpriteBatch;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.CurrentTexture2dAsBytes;
import com.github.ykiselev.opengl.textures.SimpleTexture2d;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.opengl.vertices.VertexDefinitions;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.menu.ShowMenuEvent;
import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.trigger.Trigger;
import com.github.ykiselev.window.WindowEvents;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.FloatBuffer;
import java.nio.channels.WritableByteChannel;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class BaseGame implements UiLayer, AutoCloseable {

    enum FrameBufferMode {
        COLOR, DEPTH
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Services services;

    private final SpriteBatch spriteBatch;

    private final Wrap<? extends Texture2d> cuddles;

    private final Wrap<SpriteFont> liberationMono;

    private final FloatBuffer pv;

    private final GenericIndexedGeometry pyramid;

    private final GenericIndexedGeometry cubes;

    private float radius = 8;

    private float alpha;

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

    private final WindowEvents events = new WindowEvents() {
        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            return onKey(key, scanCode, action, mods);
        }

        @Override
        public void cursorEvent(double x, double y) {
            onCursor(x, y);
        }

        @Override
        public boolean mouseButtonEvent(int button, int action, int mods) {
            return onMouseButton(button, action, mods);
        }

        @Override
        public void frameBufferResized(int width, int height) {
        }

        @Override
        public boolean scrollEvent(double dx, double dy) {
            return onScroll(dx, dy);
        }
    };

    private FrameBufferMode frameBufferMode = FrameBufferMode.COLOR;

    public BaseGame(Services services) {
        this.services = requireNonNull(services);
        final Assets assets = services.resolve(Assets.class);
        spriteBatch = new DefaultSpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        cuddles = assets.load("images/htf-cuddles.jpg", SimpleTexture2d.class);
        liberationMono = assets.load("fonts/Liberation Mono.sf", SpriteFont.class);
        final Wrap<ProgramObject> generic = assets.load("progs/generic.conf", ProgramObject.class);
        try (Wrap<ObjModel> model = assets.load("models/2cubes.obj", ObjModel.class)) {
            cubes = new GenericIndexedGeometry(
                    generic,
                    VertexDefinitions.POSITION_TEXTURE_NORMAL,
                    model.value().toIndexedTriangles()
            );
        }
        texUniform = generic.value().lookup("tex");
        final Wrap<ProgramObject> colored = assets.load("progs/colored.conf", ProgramObject.class);
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
    public WindowEvents events() {
        return events;
    }

    private boolean onKey(int key, int scanCode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_ESCAPE:
                    services.resolve(Events.class)
                            .fire(new ShowMenuEvent());
                    break;

                case GLFW.GLFW_KEY_PRINT_SCREEN:
                    try {
                        dumpToFile(frameBuffer.color(), "color.png");
                        dumpToFile(frameBuffer.depth(), "depth.png");
                    } catch (IOException e) {
                        logger.error("Unable to save image!", e);
                    }
                    break;

                case GLFW.GLFW_KEY_F1:
                    int i = 0;
                    final FrameBufferMode[] values = FrameBufferMode.values();
                    for (; i < values.length; i++) {
                        if (values[i] == frameBufferMode) {
                            break;
                        }
                    }
                    if (i < values.length - 1) {
                        i++;
                    } else {
                        i = 0;
                    }
                    frameBufferMode = values[i];
                    break;
            }
        }
        return true;
    }

    private void dumpToFile(Texture2d texture, String name) throws IOException {
        texture.bind();
        try {
            final FileSystem fs = services.resolve(FileSystem.class);
            try (WritableByteChannel channel = fs.openForWriting(name, false)) {
                new CurrentTexture2dAsBytes().write(bb -> {
                    try {
                        channel.write(bb);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        } finally {
            texture.unbind();
        }
    }

    private void onCursor(double x, double y) {
        cx = x;
        cy = y;
    }

    private boolean onMouseButton(int button, int action, int mods) {
        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT:
                lmbPressed = (action == GLFW.GLFW_PRESS);
                break;

            case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
                rmbPressed = action == GLFW.GLFW_PRESS;
                rmbTrigger.value(action == GLFW.GLFW_PRESS);
                break;
        }
        return true;
    }

    private boolean onScroll(double dx, double dy) {
        radius += dy;
        return true;
    }

    @Override
    public void close() throws Exception {
        spriteBatch.close();
        liberationMono.close();
        pyramid.close();
        cubes.close();
        cuddles.close();
        MemoryUtil.memFree(pv);
        frameBuffer.close();
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
        cuddles.value().bind();
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer rm = ms.mallocFloat(16);
            Matrix.rotation(0, 0, Math.toRadians(25 * sec % 360), rm);

            final FloatBuffer mvp = ms.mallocFloat(16);
            Matrix.multiply(vp, rm, mvp);

            cubes.draw(mvp);
        }
        cuddles.value().unbind();
    }

    private void setupProjectionViewMatrix(int width, int height) {
        Matrix.perspective(
                (float) Math.toRadians(90),
                (float) width / height,
                0.1f,
                150,
                pv
        );
        if (radius < 1) {
            radius = 1;
        }
        if (rmbPressed) {
            final double maxAngle = Math.toRadians(45.0);
            alpha = (float) Math.max(-maxAngle, Math.min(maxAngle, 0.1 * (cy - cy0)));
        }
        cameraZ = (float) (radius * Math.sin(alpha));
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

        GL11.glViewport(0, 0, width, height);
        GL11.glFrontFace(GL11.GL_CCW);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDepthFunc(GL11.GL_LESS);
        GL11.glDepthMask(true);

        setupProjectionViewMatrix(width, height);

        frameBuffer.bind();

        GL11.glClearDepth(100.0f);
        GL11.glClearColor(0, 0, 0.5f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        drawPyramids(pv);
        drawModel(pv);
        frameBuffer.unbind();

        final double t = GLFW.glfwGetTime();
        final double fps = (double) frames / t;

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        spriteBatch.begin(0, 0, width, height, true);
        switch (frameBufferMode) {
            case COLOR:
                spriteBatch.draw(frameBuffer.color(), 0, 0, width, height, 0xffffffff);
                break;

            case DEPTH:
                spriteBatch.draw(frameBuffer.depth(), 0, 0, width, height, 0xffffffff);
                break;
        }
        spriteBatch.draw(
                liberationMono.value(),
                0,
                height,
                width, String.format("avg. fps: %.2f, frame buffer mode: %s", fps, frameBufferMode),
                0xffffffff
        );
        spriteBatch.end();

        frames++;
    }
}