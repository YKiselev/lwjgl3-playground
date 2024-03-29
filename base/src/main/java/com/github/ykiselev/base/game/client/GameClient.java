package com.github.ykiselev.base.game.client;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.base.game.Pyramids;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.common.fps.FrameInfo;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.buffers.FrameBuffer;
import com.github.ykiselev.opengl.fonts.TrueTypeFont;
import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.sprites.Colors;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.sprites.TextAttributes;
import com.github.ykiselev.opengl.textures.CurrentTexture2dAsBytes;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.spi.GameFactoryArgs;
import com.github.ykiselev.spi.api.Updatable;
import com.github.ykiselev.spi.services.FileSystem;
import com.github.ykiselev.spi.services.commands.Commands;
import com.github.ykiselev.spi.services.layers.DrawingContext;
import com.github.ykiselev.spi.window.Window;
import com.github.ykiselev.spi.window.WindowEvents;
import com.github.ykiselev.spi.world.World;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.FloatBuffer;
import java.nio.channels.WritableByteChannel;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.*;

public final class GameClient implements Updatable, AutoCloseable, WindowEvents {

    enum FrameBufferMode {
        COLOR, DEPTH, NORMAL
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Commands commands;

    private final Window window;

    private final FrameInfo frameInfo;

    private final FileSystem fileSystem;

    private final Texture2d cuddles;

    private final TrueTypeFont ttf;

    private final FloatBuffer vp;

    private boolean lmbPressed, rmbPressed, active;

    private double mx, my, dx, dy;

    private final FrameBuffer frameBuffer;

    private final Pyramids pyramids;

    private final WorldRenderer renderer;

    private final AutoCloseable closeable;

    private final Camera camera = new Camera();

    private FrameBufferMode frameBufferMode = FrameBufferMode.COLOR;

    public GameClient(GameFactoryArgs host) {
        this.commands = requireNonNull(host.commands());
        this.fileSystem = requireNonNull(host.fileSystem());
        this.frameInfo = requireNonNull(host.frameInfo());
        this.window = requireNonNull(host.window());

        final Assets assets = host.assets();

        try (var guard = Closeables.newGuard()) {
            var tex = assets.load("images/console.jpg", OglRecipes.SPRITE);
            guard.add(tex);
            cuddles = tex.value();

            var atlas = assets.load("font-atlases/base.conf", OglRecipes.FONT_ATLAS);
            guard.add(atlas);

            ttf = atlas.value().get("console");

            pyramids = guard.add(new Pyramids(assets));

            renderer = guard.add(new WorldRenderer(assets));

            vp = MemoryUtil.memAllocFloat(16);
            frameBuffer = guard.add(new FrameBuffer());

            closeable = guard.detach();
        }
        camera.set(0, -10, 0);
    }

    @Override
    public void update() {
    }

    @Override
    public void close() throws Exception {
        MemoryUtil.memFree(vp);
        closeable.close();
    }

    private void setupProjectionViewMatrix(int width, int height) {
        Matrix.identity(vp);
        camera.apply((float) width / height, vp);
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS || action == GLFW_REPEAT) {
            switch (key) {
                case GLFW.GLFW_KEY_ESCAPE -> commands.execute("show-menu");
                case GLFW.GLFW_KEY_GRAVE_ACCENT -> commands.execute("toggle-console");
                case GLFW.GLFW_KEY_PRINT_SCREEN -> {
                    try {
                        dumpToFile(frameBuffer.color(), "color.png");
                        dumpToFile(frameBuffer.depth(), "depth.png");
                        dumpToFile(frameBuffer.normal(), "normal.png");
                    } catch (IOException e) {
                        logger.error("Unable to save image!", e);
                    }
                }
                case GLFW.GLFW_KEY_F1 -> frameBufferMode = switch (frameBufferMode) {
                    case COLOR -> FrameBufferMode.DEPTH;
                    case DEPTH -> FrameBufferMode.NORMAL;
                    case NORMAL -> FrameBufferMode.COLOR;
                };
                case GLFW.GLFW_KEY_W -> camera.move(0.5f);
                case GLFW_KEY_S -> camera.move(-0.5f);
                case GLFW_KEY_A -> camera.strafe(-0.5f);
                case GLFW_KEY_D -> camera.strafe(0.5f);
                case GLFW_KEY_SPACE -> camera.moveUp(0.5f);
                case GLFW_KEY_C -> camera.moveUp(-0.5f);
            }
        }
        return true;
    }

    private void dumpToFile(Texture2d texture, String name) throws IOException {
        texture.bind();
        try {
            try (WritableByteChannel channel = fileSystem.truncate(name)) {
                CurrentTexture2dAsBytes.write(bb -> {
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

    @Override
    public boolean charEvent(int codePoint) {
        return WindowEvents.super.charEvent(codePoint);
    }

    @Override
    public void cursorEvent(double x, double y) {
        if (!active) {
            return;
        }
        // Skip first move for uninitialized mx, my to avoid sudden camera rotation
        if (mx == 0 && my == 0) {
            mx = x;
            my = y;
            return;
        }

        dx = x - mx;
        dy = y - my;
        mx = x;
        my = y;

        camera.rotate(dx, dy);
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        if (!active) {
            return false;
        }
        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT -> lmbPressed = (action == GLFW.GLFW_PRESS);
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                rmbPressed = action == GLFW.GLFW_PRESS;
                //rmbTrigger.value(action == GLFW.GLFW_PRESS);
            }
        }
        return true;
    }

    @Override
    public void frameBufferResized(int width, int height) {
        // no-op
    }

    @Override
    public boolean scrollEvent(double dx, double dy) {
        return true;
    }


    public void draw(int width, int height, DrawingContext context, World world) {
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
        //GL20.glDrawBuffers(new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1});

        pyramids.draw(vp);

        renderer.draw(world, vp);

        //drawModel(vp);
        frameBuffer.unbind();

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        final TextAttributes textAttributes = context.textAttributes();
        textAttributes.trueTypeFont(ttf);
        textAttributes.alignment(TextAlignment.LEFT);
        textAttributes.color(Colors.WHITE);

        final SpriteBatch spriteBatch = context.batch();

        spriteBatch.begin(0, 0, width, height, true);
        switch (frameBufferMode) {
            case COLOR -> spriteBatch.draw(frameBuffer.color(), 0, 0, width, height, 0, 0, 1, 1, 0xffffffff);
            case DEPTH -> spriteBatch.draw(frameBuffer.depth(), 0, 0, width, height, 0, 0, 1, 1, 0xffffffff);
            case NORMAL -> spriteBatch.draw(frameBuffer.normal(), 0, 0, width, height, 0, 0, 1, 1, 0xffffffff);
        }

        spriteBatch.draw(0, height, width,
                String.format("time (ms): min: %.1f, max: %.1f, avg: %.1f, fps: %.2f, frame buffer mode: %s, %s",
                        frameInfo.min(), frameInfo.max(), frameInfo.avg(), frameInfo.fps(), frameBufferMode,
                        renderer.formatStats()),
                textAttributes
        );

        // debug
        textAttributes.color(Colors.rgb(255, 255, 0));
        textAttributes.spriteFont(null);
        textAttributes.trueTypeFont(ttf);

        //spriteBatch.draw(10, height - 30, width, "This is the test! 0123456789.\nSecond line of text ~?!:#@$%^&*()_+", textAttributes);

        spriteBatch.end();
    }

    public void activate(boolean active) {
        mx = my = 0;
        this.active = active;
    }
}
