package com.github.ykiselev.lwjgl3.layers.menu;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.playground.ui.menus.ListMenu;
import com.github.ykiselev.playground.ui.menus.ListMenu.MenuItem;
import com.github.ykiselev.opengl.shaders.ProgramObject;
import com.github.ykiselev.opengl.sprites.DefaultSpriteBatch;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAlignment;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.opengl.textures.SimpleTexture2d;
import com.github.ykiselev.opengl.textures.Texture2d;
import com.github.ykiselev.playground.ui.elements.CheckBox;
import com.github.ykiselev.playground.ui.elements.Link;
import com.github.ykiselev.playground.ui.elements.Slider;
import com.github.ykiselev.playground.ui.models.checkbox.SimpleCheckBoxModel;
import com.github.ykiselev.playground.ui.models.slider.ConfigurationBoundSliderModel;
import com.github.ykiselev.playground.ui.models.slider.SliderDefinition;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Removable;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.game.NewGameEvent;
import com.github.ykiselev.services.events.game.QuitEvent;
import com.github.ykiselev.services.layers.DrawingContext;
import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.window.DelegatingWindowEvents;
import com.github.ykiselev.window.WindowEvents;
import com.github.ykiselev.wrap.Wrap;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Menu implements UiLayer, AutoCloseable, Removable {

    private final ListMenu listMenu;

    private final Services services;

    private boolean pushed = false;

    private final WindowEvents windowEvents;

    private final SpriteBatch spriteBatch;

    private final Wrap<? extends Texture2d> white;

    private final Wrap<SpriteFont> font;

    @Override
    public boolean canBeRemoved() {
        return !pushed;
    }

    public Menu(Services services) {
        this.services = services;
        final Assets assets = services.resolve(Assets.class);
        spriteBatch = new DefaultSpriteBatch(
                assets.load("progs/sprite-batch.conf", ProgramObject.class)
        );
        white = assets.load("images/white.png", SimpleTexture2d.class);
        font = assets.load("fonts/Liberation Mono 22.sf", SpriteFont.class);
        final Events events = services.resolve(Events.class);
        final PersistedConfiguration configuration = services.resolve(PersistedConfiguration.class);
        final Slider effectsSlider = new Slider(
                new ConfigurationBoundSliderModel(
                        new SliderDefinition(0, 10, 1),
                        configuration,
                        "sound.effects.level"
                )
        );
        final DrawingContext context = new DrawingContext() {

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
            public int draw(int x, int y, int width, CharSequence text, int color) {
                return spriteBatch.draw(font.value(), x, y, width, text, color);
            }

            @Override
            public int draw(int x, int y, int width, CharSequence text, TextAlignment alignment, int color) {
                return spriteBatch.draw(font.value(), x, y, width, text, alignment, color);
            }

            @Override
            public StringBuilder stringBuilder() {
                return sb;
            }

            @Override
            public void fill(int x, int y, int width, int height, int color) {
                spriteBatch.draw(white.value(), x, y, width, height, color);
            }
        };
        this.listMenu = new ListMenu(
                context,
                new MenuItem(
                        new Link(
                                "New",
                                () -> events.fire(new NewGameEvent())
                        )
                ),
                new MenuItem(
                        "Flag1",
                        new CheckBox(
                                new SimpleCheckBoxModel(m -> effectsSlider.enable(m.checked()), true)
                                //new ConfigurationBoundCheckBoxModel(configuration, "sound.flag1"),
                        )
                ),
                new MenuItem("Effects", effectsSlider),
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
                                () -> events.fire(new QuitEvent())
                        )
                )
        );
        this.windowEvents = new DelegatingWindowEvents(listMenu.events()) {
            @Override
            public boolean keyEvent(int key, int scanCode, int action, int mods) {
                if (super.keyEvent(key, scanCode, action, mods)) {
                    return true;
                }
                if (action == GLFW_PRESS && key == GLFW_KEY_ESCAPE) {
                    services.resolve(UiLayers.class)
                            .pop(Menu.this);
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public WindowEvents events() {
        return windowEvents;
    }

    @Override
    public void onPush() {
        pushed = true;
    }

    @Override
    public void onPop() {
        pushed = false;
    }

    @Override
    public void close() throws Exception {
        spriteBatch.close();
        white.close();
        font.close();
    }

    @Override
    public void draw(int width, int height) {
        listMenu.draw(width, height);
    }
}
