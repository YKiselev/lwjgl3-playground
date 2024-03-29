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
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.fonts.FontAtlas;
import com.github.ykiselev.opengl.fonts.TrueTypeFont;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextDrawingFlags;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.spi.services.commands.Commands;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.layers.DrawingContext;
import com.github.ykiselev.spi.services.layers.UiLayer;
import com.github.ykiselev.spi.services.layers.UiLayers;
import com.github.ykiselev.spi.window.WindowEvents;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConsole implements UiLayer, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Commands commands;

    private final Texture2d background;

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

        @Override
        public boolean scrollEvent(double dx, double dy) {
            if (inputAllowed) {
                // todo
                buffer.scroll((int) dy);
            }
            return showing;
        }
    };

    private final AutoCloseable ac;

    private final ConsoleBuffer buffer;

    private final CommandLine commandLine;

    private final UiLayers uiLayers;

    private final TrueTypeFont ttf;

    private double consoleHeight;

    private double showTime = 3;

    private int backgroundColor = 0xffffffff;

    private int textColor = 0xffffffff;

    private boolean showing;

    private boolean inputAllowed;

    private double prevTime;

    @Override
    public WindowEvents events() {
        return events;
    }

    public AppConsole(Commands commands, PersistedConfiguration configuration, ConsoleBuffer buffer, CommandLine commandLine,
                      Assets assets, UiLayers uiLayers) {
        this.commands = requireNonNull(commands);
        this.buffer = requireNonNull(buffer);
        this.commandLine = requireNonNull(commandLine);
        this.uiLayers = requireNonNull(uiLayers);
        try (var guard = Closeables.newGuard()) {
            guard.add(configuration.wire()
                    .withDouble("console.showTime", () -> showTime, v -> showTime = v, true)
                    .withHexInt("console.textColor", () -> textColor, v -> textColor = v, true)
                    .withHexInt("console.backgroundColor", () -> backgroundColor, v -> backgroundColor = v, true)
                    .build());

            guard.add(commands.add()
                    .with("toggle-console", this::onToggleConsole)
                    .with("echo", this::onEcho)
                    .build());

            background = guard.add(assets.load("images/console.jpg", OglRecipes.SPRITE));
            FontAtlas atlas = guard.add(assets.load("font-atlases/base.conf", OglRecipes.FONT_ATLAS));
            this.ttf = atlas.get("console");

            ac = guard.detach();
        }
    }

    private void onEcho(List<String> args) {
        logger.info("{}", args);
    }

    private void onToggleConsole() {
        showing = !showing;
        prevTime = glfwGetTime();
        // Always disable input to filter out characters from toggle key (i.e. '~'). Would be enabled on next draw call (if showing).
        inputAllowed = false;
        if (showing) {
            uiLayers.add(this);
        }
    }

    private boolean onKey(int key, int scanCode, int action, int mods) {
        if (action != GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_ESCAPE -> {
                    showing = false;
                    consoleHeight = 0;
                    inputAllowed = false;
                    uiLayers.pop(this);
                    commands.execute("show-menu");
                    return true;
                }
                case GLFW.GLFW_KEY_GRAVE_ACCENT -> {
                    onToggleConsole();
                    return true;
                }
                case GLFW.GLFW_KEY_LEFT -> {
                    commandLine.left();
                    return true;
                }
                case GLFW.GLFW_KEY_RIGHT -> {
                    commandLine.right();
                    return true;
                }
                case GLFW.GLFW_KEY_UP -> {
                    commandLine.searchHistoryBackward();
                    return true;
                }
                case GLFW.GLFW_KEY_DOWN -> {
                    commandLine.searchHistory();
                    return true;
                }
                case GLFW.GLFW_KEY_HOME -> {
                    commandLine.begin();
                    return true;
                }
                case GLFW.GLFW_KEY_END -> {
                    commandLine.end();
                    return true;
                }
                case GLFW.GLFW_KEY_ENTER -> {
                    commandLine.execute();
                    return true;
                }
                case GLFW.GLFW_KEY_TAB -> {
                    commandLine.complete();
                    return true;
                }
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    commandLine.removeLeft();
                    return true;
                }
                case GLFW.GLFW_KEY_DELETE -> {
                    commandLine.remove();
                    return true;
                }
                case GLFW.GLFW_KEY_PAGE_UP -> {
                    buffer.pageUp();
                    return true;
                }
                case GLFW.GLFW_KEY_PAGE_DOWN -> {
                    buffer.pageDown();
                    return true;
                }
            }
        }
        return showing;
    }

    @Override
    public void draw(int width, int height, DrawingContext context) {
        calculateHeight(height);
        if (consoleHeight <= 0) {
            uiLayers.remove(this);
            return;
        }
        drawConsole(0, height - (int) consoleHeight, width, height, context);
    }

    private void calculateHeight(int viewHeight) {
        final double t = glfwGetTime(), deltaTime = t - prevTime;
        // well, it's much easier to do with per frame increments than with total toggle delta time,
        // especially when toggle button is pressed again while previous toggle cycle is not yet complete
        prevTime = t;
        final double deltaHeight = (showing ? 1 : -1) * viewHeight * deltaTime / showTime;
        consoleHeight = max(0, Math.min(viewHeight, consoleHeight + deltaHeight));
    }

    private void drawConsole(int x0, int y0, int width, int height, DrawingContext context) {
        if (!inputAllowed && showing) {
            inputAllowed = true;
        }
        var attributes = context.textAttributes();
        attributes.trueTypeFont(ttf);
        attributes.add(TextDrawingFlags.USE_COLOR_CONTROL_SEQUENCES);

        final SpriteBatch spriteBatch = context.batch();
        spriteBatch.begin(x0, y0, width, (int) consoleHeight, true);
        spriteBatch.draw(background, x0, y0, width, height, backgroundColor);
        attributes.color(textColor);
        buffer.draw(context, x0, y0, width, height);
        commandLine.draw(context, x0, y0, width, height);
        spriteBatch.end();
    }

    @Override
    public Kind kind() {
        return Kind.POPUP;
    }

    @Override
    public void close() {
        uiLayers.remove(this);
        Closeables.close(ac);
    }
}
