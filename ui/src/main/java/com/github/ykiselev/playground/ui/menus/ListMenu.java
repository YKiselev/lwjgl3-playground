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

package com.github.ykiselev.playground.ui.menus;

import com.github.ykiselev.opengl.sprites.Colors;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.sprites.TextAttributes;
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.playground.ui.UiElement;
import com.github.ykiselev.spi.services.layers.DrawingContext;
import com.github.ykiselev.spi.services.layers.UiLayer;
import com.github.ykiselev.spi.window.WindowEvents;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ListMenu implements UiLayer {

    public static final class MenuItem {

        private final String title;

        private final UiElement element;

        public MenuItem(String title, UiElement element) {
            this.title = title;
            this.element = element;
        }

        public MenuItem(UiElement element) {
            this(null, element);
        }
    }

    private final List<MenuItem> items;

    private final DrawingContext context;

    private final WindowEvents events = new WindowEvents() {
        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            if (selectedItem().keyEvent(key, scanCode, action, mods)) {
                return true;
            }
            if (action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_UP:
                        selectPrevious();
                        return true;

                    case GLFW_KEY_DOWN:
                        selectNext();
                        return true;
                }
            }
            return false;
        }

        @Override
        public void cursorEvent(double x, double y) {
            for (MenuItem item : items) {
                item.element.cursorEvent(x, y);
            }
        }

        @Override
        public boolean mouseButtonEvent(int button, int action, int mods) {
            if (!selectedItem().mouseButtonEvent(button, action, mods)) {
                // todo
            }
            return true;
        }

        @Override
        public void frameBufferResized(int width, int height) {
            ListMenu.this.items.forEach(
                    item -> item.element.frameBufferResized(width, height)
            );
        }

        @Override
        public boolean scrollEvent(double dx, double dy) {
            if (!selectedItem().scrollEvent(dx, dy)) {
                if (dy > 0) {
                    selectPrevious();
                } else if (dy < 0) {
                    selectNext();
                }
            }
            return true;

        }
    };

    private int selected = 0;

    public ListMenu(DrawingContext context, MenuItem... items) {
        this.context = requireNonNull(context);
        this.items = Arrays.asList(items);
    }

    @Override
    public Kind kind() {
        return Kind.POPUP;
    }

    @Override
    public WindowEvents events() {
        return events;
    }

    private UiElement selectedItem() {
        return items.get(selected).element;
    }

    private void selectNext() {
        selected++;
        if (selected >= items.size()) {
            selected = 0;
        }
    }

    private void selectPrevious() {
        selected--;
        if (selected < 0) {
            selected = items.size() - 1;
        }
    }

    @Override
    public void draw(int width, int height) {
        final SpriteBatch batch = context.batch();
        batch.begin(0, 0, width, height, true);

        context.batch().fill(0, 0, width, height, 0x000030df);

        final SpriteFont font = context.textAttributes().spriteFont();
        final int cursorWidth;
        final Glyph glyph = font.glyph((char) 0x23f5);
        if (glyph != null) {
            cursorWidth = glyph.width();
        } else {
            cursorWidth = 0;
        }

        final int x = 150 + cursorWidth;
        final int maxWidth = width - x;
        int y = height / 2 + items.size() * font.height() / 2 - font.height();
        int i = 0;
        final TextAttributes attributes = context.textAttributes();
        final TextAlignment prevTextAlignment = attributes.alignment();
        for (MenuItem item : items) {
            int dx = 0, th = 0;
            if (item.title != null) {
                attributes.alignment(TextAlignment.RIGHT);
                th = batch.draw(x - (cursorWidth + 150), y, 150, item.title, context.textAttributes());
                attributes.alignment(prevTextAlignment);
            }
            if (i == selected) {
                final int prevColor = attributes.color();
                final float brightness = (System.currentTimeMillis() % 255) / 255f;
                attributes.color(Colors.fade(prevColor, brightness));
                batch.draw(x - cursorWidth, y, maxWidth, "\u23F5", attributes);
                attributes.color(prevColor);
            }
            final int eh = item.element.draw(x + dx, y, maxWidth, context);
            y -= Math.max(th, eh);
            i++;
        }
        batch.end();
    }
}
