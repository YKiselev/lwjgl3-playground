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

package com.github.ykiselev.playground.layers;

import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.spi.services.layers.UiLayer;
import com.github.ykiselev.spi.services.layers.UiLayers;
import com.github.ykiselev.spi.window.WindowEvents;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppUiLayers implements UiLayers {

    private static final Comparator<UiLayer> LAYER_COMPARATOR = Comparator.comparingInt(
            layer -> layer.kind().ordinal()
    );

    private final List<UiLayer> layers = new ArrayList<>();

    private final WindowEvents events = new WindowEvents() {

        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            for (int i = layers.size() - 1; i >= 0; i--) {
                if (layers.get(i).events().keyEvent(key, scanCode, action, mods)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean charEvent(int codePoint) {
            for (int i = layers.size() - 1; i >= 0; i--) {
                if (layers.get(i).events().charEvent(codePoint)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Note: {@link SpriteBatch} uses left lower corner as an coordinate system origin (0,0)
         * while GLFW passes cursor coordinates relative to upper-left corner of the window, so here we translate {@code y} coordinate
         * to be 0 at the bottom of a window and {@code height} at the top.
         *
         * @param x the x coordinate (relative to window left)
         * @param y the y coordinate (relative to window top)
         */
        @Override
        public void cursorEvent(double x, double y) {
            for (int i = layers.size() - 1; i >= 0; i--) {
                layers.get(i).events().cursorEvent(x, height - y);
            }
        }

        @Override
        public boolean mouseButtonEvent(int button, int action, int mods) {
            for (int i = layers.size() - 1; i >= 0; i--) {
                if (layers.get(i).events().mouseButtonEvent(button, action, mods)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        @SuppressWarnings("ForLoopReplaceableByForEach")
        public void frameBufferResized(int width, int height) {
            resize(width, height);
            for (int i = 0; i < layers.size(); i++) {
                layers.get(i).events().frameBufferResized(width, height);
            }
        }

        @Override
        public boolean scrollEvent(double dx, double dy) {
            for (int i = layers.size() - 1; i >= 0; i--) {
                if (layers.get(i).events().scrollEvent(dx, dy)) {
                    return true;
                }
            }
            return false;
        }
    };

    private int width;

    private int height;

    private void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public WindowEvents events() {
        return events;
    }

    @Override
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void draw() {
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).draw(width, height);
        }
    }

    @Override
    public void pop(UiLayer layer) {
        if (layer == null) {
            throw new NullPointerException("Layer can not be null!");
        }
        final int last = layers.size() - 1;
        if (layers.get(last) != layer) {
            throw new IllegalArgumentException("Not a top layer: " + layer);
        }
        layers.remove(last).onPop();
    }

    @Override
    public void removePopups() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            if (!layers.get(i).isPopup()) {
                break;
            }
            layers.remove(i).onPop();
        }
    }

    @Override
    public void add(UiLayer layer) {
        if (layer == null) {
            throw new NullPointerException("Layer can not be null!");
        }
        if (layers.contains(layer)) {
            throw new IllegalArgumentException("Already on stack:" + layer);
        }
        layers.add(layer);
        // Note: sort is expected to be stable!
        layers.sort(LAYER_COMPARATOR);
        layer.onPush();
    }

    @Override
    public void remove(UiLayer layer) {
        if (layers.remove(layer)) {
            layer.onPop();
        }
    }
}
