package cob.github.ykiselev.lwjgl3.layers.menu;

import cob.github.ykiselev.lwjgl3.config.PersistedConfiguration;
import cob.github.ykiselev.lwjgl3.events.Events;
import cob.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import cob.github.ykiselev.lwjgl3.events.game.QuitGameEvent;
import cob.github.ykiselev.lwjgl3.layers.UiLayer;
import cob.github.ykiselev.lwjgl3.layers.menu.ListMenu.MenuItem;
import cob.github.ykiselev.lwjgl3.layers.ui.elements.CheckBox;
import cob.github.ykiselev.lwjgl3.layers.ui.elements.Link;
import cob.github.ykiselev.lwjgl3.layers.ui.elements.Slider;
import cob.github.ykiselev.lwjgl3.layers.ui.models.checkbox.ConfigurationBoundCheckBoxModel;
import cob.github.ykiselev.lwjgl3.layers.ui.models.slider.ConfigurationBoundSliderModel;
import cob.github.ykiselev.lwjgl3.layers.ui.models.slider.SliderDefinition;
import cob.github.ykiselev.lwjgl3.playground.WindowEvents;
import cob.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.assets.Assets;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Menu implements UiLayer, AutoCloseable {

    private final ListMenu listMenu;

    private boolean pushed = false;

    public Menu(Services services) {
        final Events events = services.resolve(Events.class);
        final Assets assets = services.resolve(Assets.class);
        final PersistedConfiguration configuration = services.resolve(PersistedConfiguration.class);
        this.listMenu = new ListMenu(
                services,
                assets,
                new MenuItem(
                        new Link(
                                "New",
                                () -> events.send(new NewGameEvent())
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
    }

    @Override
    public void close() {
        listMenu.close();
    }

    @Override
    public void draw(int width, int height) {
        listMenu.draw(width, height);
    }
}
