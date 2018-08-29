package com.github.ykiselev.playground.ui.menus;

import com.github.ykiselev.opengl.sprites.Colors;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.playground.ui.UiElement;
import com.github.ykiselev.services.layers.DrawingContext;
import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.window.WindowEvents;

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
        final SpriteBatch spriteBatch = context.batch();
        spriteBatch.begin(0, 0, width, height, true);
        context.fill(0, 0, width, height, 0x000030df);

        final SpriteFont font = context.font();
        final int cursorWidth;
        final Glyph glyph = font.glyph((char) 0x23f5);
        if (glyph != null) {
            cursorWidth = glyph.width();
        } else {
            cursorWidth = 0;
        }

        final int x = 150 + cursorWidth;
        final int maxWidth = width - x;
        int y = height / 2 + items.size() * font.fontHeight() / 2 - font.fontHeight();
        int i = 0;
        for (MenuItem item : items) {
            int dx = 0, th = 0;
            if (item.title != null) {
                th = context.draw(x - (cursorWidth + 150), y, 150, item.title, TextAlignment.RIGHT, 0xffffffff);
            }
            if (i == selected) {
                final float brightness = (System.currentTimeMillis() % 255) / 255f;
                context.draw(x - cursorWidth, y, maxWidth, "\u23F5", Colors.fade(0xffffffff, brightness));
            }
            final int eh = item.element.draw(x + dx, y, maxWidth, context);
            y -= Math.max(th, eh);
            i++;
        }
        spriteBatch.end();
    }
}
