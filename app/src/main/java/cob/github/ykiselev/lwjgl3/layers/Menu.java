package cob.github.ykiselev.lwjgl3.layers;

import cob.github.ykiselev.lwjgl3.config.PersistedConfiguration;
import cob.github.ykiselev.lwjgl3.events.Subscriptions;
import cob.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import cob.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import cob.github.ykiselev.lwjgl3.events.game.QuitGameEvent;
import cob.github.ykiselev.lwjgl3.host.Host;
import cob.github.ykiselev.lwjgl3.layers.ui.UiElement;
import cob.github.ykiselev.lwjgl3.layers.ui.elements.CheckBox;
import cob.github.ykiselev.lwjgl3.layers.ui.elements.Link;
import cob.github.ykiselev.lwjgl3.layers.ui.elements.Slider;
import cob.github.ykiselev.lwjgl3.layers.ui.models.checkbox.ConfigurationBoundCheckBoxModel;
import cob.github.ykiselev.lwjgl3.layers.ui.models.slider.ConfigurationBoundSliderModel;
import cob.github.ykiselev.lwjgl3.layers.ui.models.slider.SliderDefinition;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.Colors;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.text.Glyph;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.Texture2d;

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
public final class Menu implements UiLayer, AutoCloseable {

    static class MenuItem {

        private final String title;

        private final UiElement element;

        MenuItem(String title, UiElement element) {
            this.title = title;
            this.element = element;
        }

        MenuItem(UiElement element) {
            this(null, element);
        }
    }

    private final Host host;

    private final Subscriptions group;

    private final SpriteBatch spriteBatch;

    private final Texture2d white;

    private final SpriteFont font;

    private final List<MenuItem> items;

    private final DrawingContext context;

    private int selected = 0;

    public Menu(Host host, Assets assets) {
        this.host = requireNonNull(host);
        group = new SubscriptionsBuilder()
                .build(host.events());
        spriteBatch = new SpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        white = assets.load("images/white.png", Texture2d.class);
        font = assets.load("fonts/Liberation Mono 22.sf", SpriteFont.class);
        final PersistedConfiguration configuration = host.services().resolve(PersistedConfiguration.class);
        items = Arrays.asList(
                new MenuItem(
                        new Link(
                                "New",
                                () -> host.events().send(new NewGameEvent())
                        )
                ),
                new MenuItem(
                        "Flag1",
                        new CheckBox(new ConfigurationBoundCheckBoxModel(configuration, "sound.flag1"))
                ),
                new MenuItem(
                        "Effects",
                        new Slider(
                                new ConfigurationBoundSliderModel(
                                        new SliderDefinition(0, 10, 1),
                                        configuration,
                                        "sound.effects.level"
                                )
                        )
                ),
                new MenuItem(
                        "",
                        new Slider(
                                new ConfigurationBoundSliderModel(
                                        new SliderDefinition(0, 10, 1),
                                        configuration,
                                        "sound.music.level"
                                )
                        )
                ),
                new MenuItem(
                        new Link(
                                "Exit",
                                () -> host.events().send(new QuitGameEvent())
                        )
                )
        );
        context = new DrawingContext() {
            @Override
            public SpriteFont font() {
                return font;
            }

            @Override
            public SpriteBatch batch() {
                return spriteBatch;
            }

            @Override
            public int draw(int x, int y, int width, CharSequence text, int color) {
                return spriteBatch.draw(font, x, y, width, text, color);
            }
        };
    }

    @Override
    public void close() throws Exception {
        spriteBatch.close();
        group.close();
    }

    private UiElement selectedItem() {
        return items.get(selected).element;
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        if (!selectedItem().keyEvent(key, scanCode, action, mods)) {
            if (action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_ESCAPE:
                        host.services()
                                .resolve(UiLayers.class)
                                .pop(this);
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
        items.forEach(
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

    @Override
    public void draw(int width, int height) {
        spriteBatch.begin(0, 0, width, height, true);
        spriteBatch.draw(white, 0, 0, width, height, 0x000030df);

        final int cursorWidth;
        final Glyph glyph = font.findGlyph((char) 0x23f5);
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
                th = spriteBatch.draw(font, x - (cursorWidth + 150), y, 150, item.title, TextAlignment.RIGHT, 0xffffffff);
            }
            if (i == selected) {
                final float brightness = (System.currentTimeMillis() % 255) / 255f;
                spriteBatch.draw(font, x - cursorWidth, y, maxWidth, "\u23F5", Colors.fade(0xffffffff, brightness));
            }
            final int eh = item.element.draw(x + dx, y, maxWidth, context);
            y -= Math.max(th, eh);
            i++;
        }

        int h = font.fontHeight() + font.glyphYBorder();
        h += spriteBatch.draw(font, 0, h, width, "Left\nAlignment", TextAlignment.LEFT, 0xffffffff);
        h += spriteBatch.draw(font, 0, h, width, "Middle\nAlignment", TextAlignment.MIDDLE, 0xffffffff);
        h += spriteBatch.draw(font, 0, h, width, "Right\nAlignment", TextAlignment.RIGHT, 0xffffffff);
        spriteBatch.end();
    }
}
