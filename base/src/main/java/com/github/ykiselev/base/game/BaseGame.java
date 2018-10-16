/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.base.game;

import com.github.ykiselev.FrameInfo;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.components.Game;
import com.github.ykiselev.opengl.buffers.FrameBuffer;
import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.matrices.Vector3f;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.sprites.TextAttributes;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.CurrentTexture2dAsBytes;
import com.github.ykiselev.opengl.textures.Sprite;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.MenuFactory;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.layers.Sprites;
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
import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class BaseGame implements Game {

    enum FrameBufferMode {
        COLOR, DEPTH
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Services services;

    private final FrameInfo frameInfo;

    private final SpriteBatch spriteBatch;

    private final Wrap<? extends Texture2d> cuddles;

    private final Wrap<SpriteFont> liberationMono;

    private final TextAttributes textAttributes = new TextAttributes();

    private final FloatBuffer pv;

    private float radius = 8;

    private float alpha;

    private float cameraZ = -0.5f;

    private boolean lmbPressed, rmbPressed;

    private double cx, cy, cx0, cy0;

    private final FrameBuffer frameBuffer;

    private final Cubes cubes;

    private final Pyramids pyramids;

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
        public boolean scrollEvent(double dx, double dy) {
            return onScroll(dx, dy);
        }
    };

    private FrameBufferMode frameBufferMode = FrameBufferMode.COLOR;

    public BaseGame(Services services) {
        this.services = requireNonNull(services);
        this.frameInfo = services.resolve(FrameInfo.class);
        final Assets assets = services.resolve(Assets.class);
        spriteBatch = services.resolve(Sprites.class).newBatch();
        cuddles = assets.load("images/htf-cuddles.jpg", Sprite.class);
        liberationMono = assets.load("fonts/Liberation Mono.sf", SpriteFont.class);
        cubes = new Cubes(assets);
        pyramids = new Pyramids(assets);
        pv = MemoryUtil.memAllocFloat(16);
        frameBuffer = new FrameBuffer();

        textAttributes.font(liberationMono.value());
        textAttributes.alignment(TextAlignment.LEFT);
    }

    @Override
    public WindowEvents events() {
        return events;
    }

    private boolean onKey(int key, int scanCode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_ESCAPE:
                    services.resolve(MenuFactory.class)
                            .showMenu();
                    break;

                case GLFW.GLFW_KEY_GRAVE_ACCENT:
                    services.resolve(Commands.class)
                            .execute("toggle-console");
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
    public void update() {
        // todo
    }

    @Override
    public void close() throws Exception {
        spriteBatch.close();
        liberationMono.close();
        pyramids.close();
        cubes.close();
        cuddles.close();
        MemoryUtil.memFree(pv);
        frameBuffer.close();
    }

    private void drawModel(FloatBuffer vp) {
        final double sec = glfwGetTime();
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
        final double sec = glfwGetTime();
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

        pyramids.draw(pv);
        drawModel(pv);
        frameBuffer.unbind();

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        spriteBatch.begin(0, 0, width, height, true);
        switch (frameBufferMode) {
            case COLOR:
                spriteBatch.draw(frameBuffer.color(), 0, 0, width, height, 0, 0, 1, 1, 0xffffffff);
                break;

            case DEPTH:
                spriteBatch.draw(frameBuffer.depth(), 0, 0, width, height, 0, 0, 1, 1, 0xffffffff);
                break;
        }
        spriteBatch.draw(0, height, width,
                String.format("time (ms): min: %.1f, max: %.1f, avg: %.1f, fps: %.2f, frame buffer mode: %s",
                        frameInfo.min(), frameInfo.max(), frameInfo.avg(), frameInfo.fps(), frameBufferMode),
                textAttributes
        );
        spriteBatch.end();
    }
}