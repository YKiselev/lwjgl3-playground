package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.lifetime.Manageable;
import com.github.ykiselev.lifetime.ManagedRef;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ManagedAssets implements Assets {

    private static final Asset MISSING_ASSET = new Asset() {
        @Override
        public boolean isAlive() {
            return true;
        }

        @Override
        public <T> T value(Class<T> clazz) {
            return null;
        }
    };

    private final Assets delegate;

    private final Map<String, Asset> cache = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ManagedAssets(Assets delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public <T> Optional<T> tryLoad(String resource, Class<T> clazz, Assets assets) throws ResourceException {
        lock.readLock().lock();
        try {
            final Asset asset = cache.get(resource);
            if (asset != null) {
                final T value = asset.value(clazz);
                if (asset.isAlive()) {
                    return Optional.ofNullable(value);
                }
            }
            return doLoad(resource, clazz, assets);
        } finally {
            lock.readLock().unlock();
        }
    }

    private <T> Optional<T> doLoad(String resource, Class<T> clazz, Assets assets) {
        lock.readLock().unlock();
        lock.writeLock().lock();
        final Optional<T> result;
        try {
            result = delegate.tryLoad(resource, clazz, assets);
            cache.put(
                    resource,
                    result.map(this::asset)
                            .orElse(MISSING_ASSET)
            );
            lock.readLock().lock();
        } finally {
            lock.writeLock().unlock();
        }
        return result;
    }

    private <T> Asset asset(T value) {
        throw new UnsupportedOperationException();
//        if (value instanceof Manageable){
//            return new ManagedAsset<>((Manageable<T>)value);
//        }
//        return null;// todo???????
    }

    @Override
    public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return delegate.resolve(resource, clazz);
    }

    private interface Asset {

        boolean isAlive();

        <T> T value(Class<T> clazz);

    }

    private static final class ManagedAsset<V extends Manageable<V> & AutoCloseable> implements Asset {

        private final ManagedRef<V> ref;

        ManagedAsset(V value) {
            this.ref = new ManagedRef<>(value);
        }

        @Override
        public boolean isAlive() {
            return ref.isAlive();
        }

        @Override
        public <T> T value(Class<T> clazz) {
            return clazz.cast(ref.newRef());
        }
    }
}
