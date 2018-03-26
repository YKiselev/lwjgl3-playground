package com.github.ykiselev.lwjgl3.layers.menu;

import com.github.ykiselev.lwjgl3.config.PersistedConfiguration;
import com.github.ykiselev.lwjgl3.events.Events;
import com.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import com.github.ykiselev.lwjgl3.events.game.QuitGameEvent;
import com.github.ykiselev.lwjgl3.layers.UiLayer;
import com.github.ykiselev.lwjgl3.layers.menu.ListMenu.MenuItem;
import com.github.ykiselev.lwjgl3.layers.ui.elements.CheckBox;
import com.github.ykiselev.lwjgl3.layers.ui.elements.Link;
import com.github.ykiselev.lwjgl3.layers.ui.elements.Slider;
import com.github.ykiselev.lwjgl3.layers.ui.models.checkbox.ConfigurationBoundCheckBoxModel;
import com.github.ykiselev.lwjgl3.layers.ui.models.slider.ConfigurationBoundSliderModel;
import com.github.ykiselev.lwjgl3.layers.ui.models.slider.SliderDefinition;
import com.github.ykiselev.lwjgl3.playground.WindowEvents;
import com.github.ykiselev.lwjgl3.services.Removable;
import com.github.ykiselev.lwjgl3.services.Schedule;
import com.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.closeables.Closeables;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Menu implements UiLayer, AutoCloseable, Removable {

    private final ListMenu listMenu;

    private final Services services;

    private boolean pushed = false;

    @Override
    public boolean canBeRemoved() {
        return !pushed;
    }

    public Menu(Services services) {
        this.services = services;
        final Events events = services.resolve(Events.class);
        // todo -create menu assets and dispose on menu recycling
        final Assets assets = services.resolve(Assets.class);
        final PersistedConfiguration configuration = services.resolve(PersistedConfiguration.class);
        this.listMenu = new ListMenu(
                services,
                assets,
                this,
                new MenuItem(
                        new Link(
                                "New",
                                () -> events.send(new NewGameEvent())
                        )
                ),
                new MenuItem(
                        "Flag1",
                        new CheckBox(
                                new ConfigurationBoundCheckBoxModel(configuration, "sound.flag1")
                        )
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
                                () -> events.send(new QuitGameEvent())
                        )
                )
        );
    }

    @Override
    public WindowEvents events() {
        return listMenu.events();
    }

    @Override
    public void onPush() {
        pushed = true;
    }

    @Override
    public void onPop() {
        pushed = false;
        services.resolve(Schedule.class)
                .schedule(
                        10,
                        TimeUnit.SECONDS,
                        () -> services.tryRemove(Menu.class)
                                .ifPresent(Closeables::close)
                );
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
