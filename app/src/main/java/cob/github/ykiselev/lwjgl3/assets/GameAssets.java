package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.CompositeReadableResources;
import com.github.ykiselev.assets.ManagedAssets;
import com.github.ykiselev.assets.ReadableResource;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.SimpleAssets;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameAssets implements Assets, AutoCloseable {

    private final Assets assets;

    private GameAssets(Assets assets) {
        this.assets = assets;
    }

    public GameAssets(Collection<Path> paths) {
        this(
                new ManagedAssets(
                        new SimpleAssets(
                                new GameResources(paths),
                                new CompositeReadableResources(
                                        new ResourceByClass(),
                                        new ResourceByExtension()
                                )
                        ),
                        new ConcurrentHashMap<>()
                )
        );
    }

    @Override
    public <T> ReadableResource<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return assets.resolve(resource, clazz);
    }

    @Override
    public <T> Optional<T> tryLoad(String resource, Class<T> clazz) throws ResourceException {
        return assets.tryLoad(resource, clazz);
    }

    @Override
    public void close() throws Exception {
        if (assets instanceof AutoCloseable) {
            ((AutoCloseable) assets).close();
        }
    }
}
