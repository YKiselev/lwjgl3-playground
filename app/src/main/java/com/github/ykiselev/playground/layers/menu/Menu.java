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

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.fonts.TrueTypeFont;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.playground.ui.elements.CheckBox;
import com.github.ykiselev.playground.ui.elements.Link;
import com.github.ykiselev.playground.ui.elements.Slider;
import com.github.ykiselev.playground.ui.menus.ListMenu;
import com.github.ykiselev.playground.ui.menus.ListMenu.MenuItem;
import com.github.ykiselev.playground.ui.models.checkbox.ConfigurationBoundCheckBoxModel;
import com.github.ykiselev.playground.ui.models.checkbox.SimpleCheckBoxModel;
import com.github.ykiselev.playground.ui.models.slider.ConfigurationBoundSliderModel;
import com.github.ykiselev.playground.ui.models.slider.SliderDefinition;
import com.github.ykiselev.spi.api.Removable;
import com.github.ykiselev.spi.services.commands.Commands;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.layers.DrawingContext;
import com.github.ykiselev.spi.services.layers.UiLayer;
import com.github.ykiselev.spi.services.layers.UiLayers;
import com.github.ykiselev.spi.window.DelegatingWindowEvents;
import com.github.ykiselev.spi.window.WindowEvents;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Menu implements UiLayer, AutoCloseable, Removable {

    private final ListMenu listMenu;

    private boolean pushed = false;

    private final WindowEvents windowEvents;

    private final TrueTypeFont font;

    private final AutoCloseable closeable;

    @Override
    public boolean canBeRemoved() {
        return !pushed;
    }

    @Override
    public Kind kind() {
        return Kind.POPUP;
    }

    public Menu(Assets assets, SpriteBatch spriteBatch, PersistedConfiguration configuration,
                Commands commands, UiLayers uiLayers) {
        try (var guard = Closeables.newGuard()) {
            var atlas = assets.load("font-atlases/base.conf", OglRecipes.FONT_ATLAS);
            guard.add(atlas);

            font = atlas.value().get("menu");

            closeable = guard.detach();
        }
        final Slider effectsSlider = new Slider(
                new ConfigurationBoundSliderModel(
                        new SliderDefinition(0, 10, 1),
                        configuration.root(),
                        "sound.effects.level"
                )
        );
        this.listMenu = new ListMenu(
                new MenuItem(
                        new Link(
                                "New",
                                () -> commands.execute("new-game")
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
                                () -> commands.execute("quit")
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
                    uiLayers.pop(Menu.this);
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
        closeable.close();
    }

    @Override
    public void draw(int width, int height, DrawingContext context) {
        context.textAttributes().trueTypeFont(font);
        listMenu.draw(width, height, context);
    }
}
