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

package com.github.ykiselev.playground.layers.menu;

import com.github.ykiselev.spi.api.Removable;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.opengl.sprites.TextAttributes;
import com.github.ykiselev.opengl.text.SpriteFont;
import com.github.ykiselev.spi.services.Services;
import com.github.ykiselev.playground.ui.elements.CheckBox;
import com.github.ykiselev.playground.ui.elements.Link;
import com.github.ykiselev.playground.ui.elements.Slider;
import com.github.ykiselev.playground.ui.menus.ListMenu;
import com.github.ykiselev.playground.ui.menus.ListMenu.MenuItem;
import com.github.ykiselev.playground.ui.models.checkbox.ConfigurationBoundCheckBoxModel;
import com.github.ykiselev.playground.ui.models.checkbox.SimpleCheckBoxModel;
import com.github.ykiselev.playground.ui.models.slider.ConfigurationBoundSliderModel;
import com.github.ykiselev.playground.ui.models.slider.SliderDefinition;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.layers.DrawingContext;
import com.github.ykiselev.spi.services.layers.UiLayer;
import com.github.ykiselev.spi.window.DelegatingWindowEvents;
import com.github.ykiselev.spi.window.WindowEvents;
import com.github.ykiselev.wrap.Wrap;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Menu implements UiLayer, AutoCloseable, Removable {

    private final ListMenu listMenu;

    private boolean pushed = false;

    private final WindowEvents windowEvents;

    private final SpriteBatch spriteBatch;

    private final Wrap<SpriteFont> font;

    @Override
    public boolean canBeRemoved() {
        return !pushed;
    }

    @Override
    public Kind kind() {
        return Kind.POPUP;
    }

    public Menu(Services services) {
        final Assets assets = services.assets;
        spriteBatch = services.sprites.newBatch();
        font = assets.load("fonts/Liberation Mono 22.sf", OglRecipes.SPRITE_FONT);
        final PersistedConfiguration configuration = services.persistedConfiguration;
        final Slider effectsSlider = new Slider(
                new ConfigurationBoundSliderModel(
                        new SliderDefinition(0, 10, 1),
                        configuration.root(),
                        "sound.effects.level"
                )
        );
        final TextAttributes attributes = new TextAttributes();
        attributes.font(font.value());
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
            public StringBuilder stringBuilder() {
                return sb;
            }

            @Override
            public TextAttributes textAttributes() {
                return attributes;
            }
        };
        this.listMenu = new ListMenu(
                context,
                new MenuItem(
                        new Link(
                                "New",
                                () -> services.commands.execute("new-game")
                        )
                ),
                new MenuItem(
                        "Flag1",
                        new CheckBox(
                                new SimpleCheckBoxModel(m -> effectsSlider.enable(m.checked()), true)
                                //new ConfigurationBoundCheckBoxModel(configuration, "sound.flag1"),
                        )
                ),
                new MenuItem(
                        "Is Game Present?",
                        new CheckBox(
                                //new SimpleCheckBoxModel(m -> effectsSlider.enable(m.checked()), true)
                                new ConfigurationBoundCheckBoxModel(configuration.root(), "game.isPresent")
                        )
                ),
                new MenuItem("Effects", effectsSlider),
                new MenuItem(
                        "",
                        new Slider(
                                new ConfigurationBoundSliderModel(
                                        new SliderDefinition(0, 10, 1),
                                        configuration.root(),
                                        "sound.music.level"
                                )
                        )
                ),
                new MenuItem(
                        new Link(
                                "Exit",
                                () -> services.commands.execute("quit")
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
                    services.uiLayers.pop(Menu.this);
                    return true;
                }
                return true;
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
        font.close();
    }

    @Override
    public void draw(int width, int height) {
        listMenu.draw(width, height);
    }
}
