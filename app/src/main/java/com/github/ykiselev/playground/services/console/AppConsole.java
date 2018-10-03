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
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.DefaultSpriteBatch;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.SimpleTexture2d;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.console.ToggleConsoleEvent;
import com.github.ykiselev.services.events.menu.ShowMenuEvent;
import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.window.WindowEvents;
import com.github.ykiselev.wrap.Wrap;
import org.lwjgl.glfw.GLFW;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConsole implements UiLayer, AutoCloseable {

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

    private final String[] snapshot;

    @Override
    public WindowEvents events() {
        return events;
    }

    public AppConsole(Services services, CircularBuffer<String> buffer) {
        this.services = requireNonNull(services);
        this.buffer = requireNonNull(buffer);
        this.snapshot = new String[buffer.capacity()];
        final Assets assets = services.resolve(Assets.class);
        spriteBatch = new DefaultSpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        cuddles = assets.load("images/htf-cuddles.jpg", SimpleTexture2d.class);
        font = assets.load("fonts/Liberation Mono.sf", SpriteFont.class);
    }

    private boolean onKey(int key, int scanCode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_ESCAPE:
                    services.resolve(Events.class)
                            .fire(ShowMenuEvent.INSTANCE);
                    break;

                case GLFW.GLFW_KEY_GRAVE_ACCENT:
                    services.resolve(Events.class)
                            .fire(ToggleConsoleEvent.INSTANCE);
                    break;
            }
        }
        // debug
        return false;
    }

    @Override
    public void draw(int width, int height) {
        final int lines = buffer.copyTo(snapshot);
        spriteBatch.begin(0, 0, width, height, true);
        final SpriteFont font = this.font.value();
        for (int i = lines - 1, y = 1 + font.height(); i >= 0; i--) {
            final String line = snapshot[i];
            final int lineHeight = font.height(line, width);
            y += lineHeight;
            spriteBatch.draw(font, 0, y, width, line, 0xffffffff);
            if (y + 2 * font.height() > height) {
                break;
            }
        }
        spriteBatch.end();
    }

    @Override
    public Kind kind() {
        return Kind.CONSOLE;
    }

    @Override
    public void close() {
        Closeables.close(font);
        Closeables.close(cuddles);
        Closeables.close(spriteBatch);
    }
}
