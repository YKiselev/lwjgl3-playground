package cob.github.ykiselev.lwjgl3.host;

import cob.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import cob.github.ykiselev.lwjgl3.layers.Menu;
import cob.github.ykiselev.lwjgl3.layers.UiLayers;
import cob.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.assets.Assets;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class OnShowMenuEvent implements Consumer<ShowMenuEvent> {

    private final Host host;

    public OnShowMenuEvent(Host host) {
        this.host = requireNonNull(host);
    }

    @Override
    public void accept(ShowMenuEvent showMenuEvent) {
        final Services services = host.services();
        final Menu menu = services.tryResolve(Menu.class)
                .orElseGet(() -> {
                    final Menu m = new Menu(host, services.resolve(Assets.class));
                    services.add(Menu.class, m);
                    return m;
                });
        services.resolve(UiLayers.class)
                .push(menu);
    }
}
