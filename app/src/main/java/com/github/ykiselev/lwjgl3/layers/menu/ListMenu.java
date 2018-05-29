package com.github.ykiselev.lwjgl3.layers.menu;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.lwjgl3.layers.DrawingContext;
import com.github.ykiselev.lwjgl3.layers.UiLayer;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.layers.ui.UiElement;
import com.github.ykiselev.lwjgl3.playground.WindowEvents;
import com.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.Colors;
import com.github.ykiselev.opengl.sprites.DefaultSpriteBatch;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.SimpleTexture2d;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.wrap.Wrap;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ListMenu implements UiLayer, AutoCloseable {

    public static final class MenuItem {

        private final String title;

        private final UiElement element;

        public MenuItem(String title, UiElement element) {
            this.title = title;
            this.element = element;
        }

        MenuItem(UiElement element) {
            this(null, element);
        }
    }

    private final Services services;

    private final SpriteBatch spriteBatch;

    private final Wrap<? extends Texture2d> white;

    private final Wrap<SpriteFont> font;

    private final List<MenuItem> items;

    private final UiLayer owner;

    private final DrawingContext context = new DrawingContext() {
        @Override
        public SpriteFont font() {
            return font.value();
        }

        @Override
        public SpriteBatch batch() {
            return spriteBatch;
        }

        @Override
        public int draw(int x, int y, int width, CharSequence text, int color) {
            return spriteBatch.draw(font.value(), x, y, width, text, color);
        }
    };

    private final WindowEvents events = new WindowEvents() {
        @Override
        public boolean keyEvent(int key, int scanCode, int action, int mods) {
            if (!selectedItem().keyEvent(key, scanCode, action, mods)) {
                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_ESCAPE:
                            services.resolve(UiLayers.class)
                                    .pop(owner);
                            break;

                        case GLFW_KEY_UP:
                            selectPrevious();
                            break;

                        case GLFW_KEY_DOWN:
                            selectNext();
                            break;
                    }
                    return true;
                }
            }
            return true;
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

    public ListMenu(Services services, Assets assets, UiLayer owner, MenuItem... items) {
        this.services = requireNonNull(services);
        this.owner = requireNonNull(owner);
        spriteBatch = new DefaultSpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        white = assets.load("images/white.png", SimpleTexture2d.class);
        font = assets.load("fonts/Liberation Mono 22.sf", SpriteFont.class);
        this.items = Arrays.asList(items);
    }

    @Override
    public WindowEvents events() {
        return events;
    }

    @Override
    public void close() throws Exception {
        spriteBatch.close();
        white.close();
        font.close();
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
        spriteBatch.begin(0, 0, width, height, true);
        spriteBatch.draw(white.value(), 0, 0, width, height, 0x000030df);

        final int cursorWidth;
        final Glyph glyph = font.value().findGlyph((char) 0x23f5);
        if (glyph != null) {
            cursorWidth = glyph.width();
        } else {
            cursorWidth = 0;
        }

        final int x = 150 + cursorWidth;
        final int maxWidth = width - x;
        int y = height / 2 + items.size() * font.value().fontHeight() / 2 - font.value().fontHeight();
        int i = 0;
        for (MenuItem item : items) {
            int dx = 0, th = 0;
            if (item.title != null) {
                th = spriteBatch.draw(font.value(), x - (cursorWidth + 150), y, 150, item.title, TextAlignment.RIGHT, 0xffffffff);
            }
            if (i == selected) {
                final float brightness = (System.currentTimeMillis() % 255) / 255f;
                spriteBatch.draw(font.value(), x - cursorWidth, y, maxWidth, "\u23F5", Colors.fade(0xffffffff, brightness));
            }
            final int eh = item.element.draw(x + dx, y, maxWidth, context);
            y -= Math.max(th, eh);
            i++;
        }
        spriteBatch.end();
    }
}
