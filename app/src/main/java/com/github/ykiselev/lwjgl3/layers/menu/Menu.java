package com.github.ykiselev.lwjgl3.layers.menu;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.lwjgl3.config.PersistedConfiguration;
import com.github.ykiselev.lwjgl3.events.Events;
import com.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import com.github.ykiselev.lwjgl3.events.game.QuitGameEvent;
import com.github.ykiselev.lwjgl3.layers.UiLayer;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.layers.menu.ListMenu.MenuItem;
import com.github.ykiselev.lwjgl3.layers.ui.elements.CheckBox;
import com.github.ykiselev.lwjgl3.layers.ui.elements.Link;
import com.github.ykiselev.lwjgl3.layers.ui.elements.Slider;
import com.github.ykiselev.lwjgl3.layers.ui.models.checkbox.SimpleCheckBoxModel;
import com.github.ykiselev.lwjgl3.layers.ui.models.slider.ConfigurationBoundSliderModel;
import com.github.ykiselev.lwjgl3.layers.ui.models.slider.SliderDefinition;
import com.github.ykiselev.lwjgl3.playground.DelegatingWindowEvents;
import com.github.ykiselev.lwjgl3.playground.WindowEvents;
import com.github.ykiselev.lwjgl3.services.Removable;
import com.github.ykiselev.lwjgl3.services.Services;

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

    @Override
    public boolean canBeRemoved() {
        return !pushed;
    }

    public Menu(Services services) {
        this.services = services;
        final Events events = services.resolve(Events.class);
        final Assets assets = services.resolve(Assets.class);
        final PersistedConfiguration configuration = services.resolve(PersistedConfiguration.class);
        final Slider effectsSlider = new Slider(
                new ConfigurationBoundSliderModel(
                        new SliderDefinition(0, 10, 1),
                        configuration,
                        "sound.effects.level"
                )
        );
        this.listMenu = new ListMenu(
                assets,
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
                                () -> events.fire(new QuitGameEvent())
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
        listMenu.close();
    }

    @Override
    public void draw(int width, int height) {
        listMenu.draw(width, height);
    }
}
