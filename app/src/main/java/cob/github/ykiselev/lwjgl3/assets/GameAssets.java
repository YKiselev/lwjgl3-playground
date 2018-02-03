package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.CompositeReadableAssets;
import com.github.ykiselev.assets.ManagedAssets;
import com.github.ykiselev.assets.ReadableAsset;
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

    private final Assets delegate;

    private GameAssets(Assets delegate) {
        this.delegate = delegate;
    }

    public GameAssets(Collection<Path> paths) {
        this(
                new ManagedAssets(
                        new SimpleAssets(
                                new GameResources(paths),
                                new CompositeReadableAssets(
                                        new ResourceByClass(),
                                        new ResourceByExtension()
                                )
                        ),
                        new ConcurrentHashMap<>()
                )
        );
    }

    @Override
    public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return delegate.resolve(resource, clazz);
    }

    @Override
    public <T> Optional<T> tryLoad(String resource, Class<T> clazz, Assets assets) throws ResourceException {
        return delegate.tryLoad(resource, clazz, assets);
    }

    @Override
    public void close() throws Exception {
        if (delegate instanceof AutoCloseable) {
            ((AutoCloseable) delegate).close();
        }
    }
}
