package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ManagedAssets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.SimpleAssets;

import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameAssets implements Assets {

    private final Assets assets;

    private GameAssets(Assets assets) {
        this.assets = assets;
    }

    public GameAssets() {
        this(
                new ManagedAssets(
                        new SimpleAssets(
                                new GameResources(),
                                new ResourceByClass(),
                                new ResourceByExtension()
                        ),
                        new HashMap<>()
                )
        );
    }

    @Override
    public <T> ReadableResource<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return assets.resolve(resource, clazz);
    }

    @Override
    public ReadableByteChannel open(String resource) throws ResourceException {
        return assets.open(resource);
    }
}
