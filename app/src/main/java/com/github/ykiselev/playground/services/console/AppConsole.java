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

package com.github.ykiselev.playground.services.console;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.circular.CircularBuffer;
import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.DefaultSpriteBatch;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.sprites.TextBuilder;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.SimpleTexture2d;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.console.ToggleConsoleEvent;
import com.github.ykiselev.services.events.menu.ShowMenuEvent;
import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.services.schedule.Schedule;
import com.github.ykiselev.window.WindowEvents;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConsole implements UiLayer, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Services services;

    private final CircularBuffer<String> buffer;

    private final SpriteBatch spriteBatch;

    private final Wrap<? extends Texture2d> cuddles;

    private final Wrap<SpriteFont> font;

    private final WindowEvents events = new WindowEvents() {
        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            return onKey(key, scanCode, action, mods);
        }
    };

    private final AutoCloseable ac;

    private final String[] snapshot;

    private final TextBuilder textBuilder = new TextBuilder(200);

    private double consoleHeight;

    private double showTime = 3;

    private int backgroundColor = 0xffffffff;

    private int textColor = 0xffffffff;

    private boolean showing;

    private double prevTime;

    private long totalTime;

    private long frames;

    @Override
    public WindowEvents events() {
        return events;
    }

    public AppConsole(Services services, CircularBuffer<String> buffer) {
        this.services = requireNonNull(services);
        this.buffer = requireNonNull(buffer);
        this.snapshot = new String[buffer.capacity()];
        this.ac = new CompositeAutoCloseable(
                services.resolve(Events.class)
                        .subscribe(ToggleConsoleEvent.class, this::onToggleConsole),
                services.resolve(PersistedConfiguration.class)
                        .wire()
                        .withDouble("console.showTime", () -> showTime, v -> showTime = v, true)
                        .withInt("console.textColor", () -> textColor, v -> textColor = v, true)
                        .withInt("console.backgroundColor", () -> backgroundColor, v -> backgroundColor = v, true)
                        .build()
        );
        services.resolve(Schedule.class)
                .schedule(10, TimeUnit.SECONDS, () -> {
                    logger.info("draws: {}, avg {} draws/sec", frames, 1e9 * frames / totalTime);
                    return true;
                });
        final Assets assets = services.resolve(Assets.class);
        spriteBatch = new DefaultSpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        cuddles = assets.load("images/htf-cuddles.jpg", SimpleTexture2d.class);
        font = assets.load("fonts/Liberation Mono.sf", SpriteFont.class);
    }

    private void onToggleConsole() {
        showing = !showing;
        prevTime = glfwGetTime();
    }

    private boolean onKey(int key, int scanCode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_ESCAPE:
                    showing = false;
                    consoleHeight = 0;
                    services.resolve(Events.class)
                            .fire(ShowMenuEvent.INSTANCE);
                    return true;

                case GLFW.GLFW_KEY_GRAVE_ACCENT:
                    onToggleConsole();
                    return true;
            }
        }
        return showing;
    }

    @Override
    public void draw(int width, int height) {
        calculateHeight(height);
        if (consoleHeight <= 0) {
            return;
        }
        drawConsole(0, height - (int) consoleHeight, width, height);
    }

    private void calculateHeight(int viewHeight) {
        final double t = glfwGetTime(), deltaTime = t - prevTime;
        prevTime = t;
        final double deltaHeight = (showing ? 1 : -1) * viewHeight * deltaTime / showTime;
        consoleHeight = max(0, Math.min(viewHeight, consoleHeight + deltaHeight));
    }

    private void drawConsole(int x0, int y0, int width, int height) {
        final int lines = buffer.copyTo(snapshot);
        if (textBuilder.font() == null) {
            textBuilder.font(font.value());
            textBuilder.alignment(TextAlignment.LEFT);
        }
        textBuilder.maxWidth(width);
        spriteBatch.begin(x0, y0, width, (int) consoleHeight, true);
        // todo - it's upside down!
        spriteBatch.draw(cuddles.value(), x0, y0, width, height, backgroundColor);
        final SpriteFont font = this.font.value();
        long t0 = System.nanoTime();
        for (int i = lines - 1, y = y0; i >= 0; i--) {
            final String line = snapshot[i];
            if (true) {
                textBuilder.clear();
                final int lineHeight = textBuilder.draw(line);
                y += lineHeight;
                spriteBatch.draw(textBuilder, x0, y);
            } else {
                final int lineHeight = font.height(line, width);
                y += lineHeight;
                spriteBatch.draw(font, x0, y, width, line, textColor);
            }
            if (y >= height) {
                break;
            }
        }
        spriteBatch.end();
        long t1 = System.nanoTime();
        totalTime += t1 - t0;
        frames++;
    }

    @Override
    public Kind kind() {
        return Kind.CONSOLE;
    }

    @Override
    public void close() {
        services.resolve(UiLayers.class)
                .remove(this);
        Closeables.closeAll(font, cuddles, spriteBatch, ac);
    }
}
