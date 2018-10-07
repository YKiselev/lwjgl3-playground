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
import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.DefaultSpriteBatch;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.SimpleTexture2d;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.console.ToggleConsoleEvent;
import com.github.ykiselev.services.events.menu.ShowMenuEvent;
import com.github.ykiselev.services.layers.DrawingContext;
import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.services.schedule.Schedule;
import com.github.ykiselev.window.WindowEvents;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
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

    private final SpriteBatch spriteBatch;

    private final Wrap<? extends Texture2d> cuddles;

    private final Wrap<SpriteFont> font;

    private final WindowEvents events = new WindowEvents() {
        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            return onKey(key, scanCode, action, mods);
        }

        @Override
        public boolean charEvent(int codePoint) {
            if (inputAllowed) {
                commandLine.add(codePoint);
            }
            return true;
        }
    };

    private final AutoCloseable ac;

    private final ConsoleBuffer buffer;

    private final CommandLine commandLine;

    private final DrawingContext drawingContext = new DrawingContext() {

        private final StringBuilder sb = new StringBuilder();

        @Override
        public SpriteFont font() {
            return font.value();
        }

        @Override
        public SpriteBatch batch() {
            return spriteBatch;
        }

        @Override
        public StringBuilder stringBuilder() {
            return sb;
        }

        @Override
        public void fill(int x, int y, int width, int height, int color) {
            // todo - make it work without additional textures!
            throw new UnsupportedOperationException("not implemented");
        }
    };

    private double consoleHeight;

    private double showTime = 3;

    private int backgroundColor = 0xffffffff;

    private int textColor = 0xffffffff;

    private boolean showing;

    private boolean inputAllowed;

    private double prevTime;

    private long totalTime;

    private long frames;

    @Override
    public WindowEvents events() {
        return events;
    }

    public AppConsole(Services services, ConsoleBuffer buffer, CommandLine commandLine) {
        this.services = requireNonNull(services);
        this.buffer = requireNonNull(buffer);
        this.commandLine = requireNonNull(commandLine);
        this.ac = new CompositeAutoCloseable(
                services.resolve(Events.class)
                        .subscribe(ToggleConsoleEvent.class, this::onToggleConsole),
                services.resolve(PersistedConfiguration.class)
                        .wire()
                        .withDouble("console.showTime", () -> showTime, v -> showTime = v, true)
                        .withInt("console.textColor", () -> textColor, v -> textColor = v, true)
                        .withInt("console.backgroundColor", () -> backgroundColor, v -> backgroundColor = v, true)
                        .build(),
                services.resolve(Commands.class)
                        .add()
                        .with("toggle-console", this::onToggleConsole)
                        .with("echo", this::onEcho)
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

    private void onEcho(Collection<String> args) {
        logger.info("{}", args);
    }

    private void onToggleConsole() {
        showing = !showing;
        prevTime = glfwGetTime();
        // Always disable input to filter out characters from toggle key (i.e. '~'). Would be enabled on next draw call (if showing).
        inputAllowed = false;
    }

    private boolean onKey(int key, int scanCode, int action, int mods) {
        if (action != GLFW.GLFW_RELEASE) {
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

                case GLFW.GLFW_KEY_LEFT:
                    commandLine.left();
                    return true;

                case GLFW.GLFW_KEY_RIGHT:
                    commandLine.right();
                    return true;

                case GLFW.GLFW_KEY_UP:
                    commandLine.searchHistoryBackward();
                    return true;

                case GLFW.GLFW_KEY_DOWN:
                    commandLine.searchHistory();
                    return true;

                case GLFW.GLFW_KEY_HOME:
                    commandLine.begin();
                    return true;

                case GLFW.GLFW_KEY_END:
                    commandLine.end();
                    return true;

                case GLFW.GLFW_KEY_ENTER:
                    commandLine.execute();
                    return true;

                case GLFW.GLFW_KEY_TAB:
                    commandLine.complete();
                    return true;

                case GLFW.GLFW_KEY_BACKSPACE:
                    commandLine.removeLeft();
                    return true;

                case GLFW.GLFW_KEY_DELETE:
                    commandLine.remove();
                    return true;

                case GLFW.GLFW_KEY_PAGE_UP:
                    // todo
                    return true;

                case GLFW.GLFW_KEY_PAGE_DOWN:
                    // todo
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
        if (!inputAllowed && showing) {
            inputAllowed = true;
        }
        spriteBatch.begin(x0, y0, width, (int) consoleHeight, true);
        spriteBatch.draw(cuddles.value(), x0, y0, width, height, backgroundColor);
        buffer.draw(drawingContext, x0, y0, width, height, textColor);
        commandLine.draw(drawingContext, x0, y0, width, height, textColor);
        spriteBatch.end();
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
