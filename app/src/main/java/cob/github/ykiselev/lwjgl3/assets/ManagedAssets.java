package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.lifetime.ProxiedRef;

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

    private static final CachedValue MISSING_VALUE = new CachedValue(null, false);

    private static final CachedValue NON_EXISTING_VALUE = new CachedValue(null, true);

    private static final Asset NON_EXISTING_ASSET = () -> NON_EXISTING_VALUE;

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
            final CachedValue cached = getCached(resource);
            final Object value = cached.value();
            if (value != null || cached.skipLoading()) {
                return Optional.ofNullable(clazz.cast(value));
            }
            return doLoad(resource, clazz, assets);
        } finally {
            lock.readLock().unlock();
        }
    }

    private CachedValue getCached(String resource) {
        final Asset asset = cache.get(resource);
        if (asset != null) {
            return asset.value();
        }
        return MISSING_VALUE;
    }

    private <T> Optional<T> doLoad(String resource, Class<T> clazz, Assets assets) {
        lock.readLock().unlock();
        lock.writeLock().lock();
        try {
            final CachedValue cached = getCached(resource);
            final Object value = cached.value();
            if (value != null || cached.skipLoading()) {
                return Optional.ofNullable(clazz.cast(value));
            }
            final Optional<T> result = delegate.tryLoad(resource, clazz, assets);
            cache.put(
                    resource,
                    result.map(v -> asset(v, clazz))
                            .orElse(NON_EXISTING_ASSET)
            );
            lock.readLock().lock();
            return result;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private <T> Asset asset(T value, Class<T> clazz) {
        return proxiedAsset(
                (AutoCloseable) value,
                clazz.asSubclass(AutoCloseable.class)
        );
    }

    private <T extends AutoCloseable> Asset proxiedAsset(T value, Class<T> clazz) {
        return new ProxiedAsset(
                new ProxiedRef<>(
                        value,
                        clazz,
                        this::dispose
                )
        );
    }

    private void dispose(AutoCloseable obj) {
        try {
            obj.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return delegate.resolve(resource, clazz);
    }

    private interface Asset {

        CachedValue value();

    }

    private static final class CachedValue {

        private final Object value;

        private final boolean skipLoading;

        boolean skipLoading() {
            return skipLoading;
        }

        public Object value() {
            return value;
        }

        CachedValue(Object value, boolean skipLoading) {
            this.value = value;
            this.skipLoading = skipLoading;
        }

        <T> Optional<T> value(Class<T> clazz) {
            return Optional.ofNullable(
                    clazz.cast(value)
            );
        }
    }

    private static final class ProxiedAsset implements Asset {

        private final ProxiedRef<?> ref;

        public ProxiedAsset(ProxiedRef<?> ref) {
            this.ref = requireNonNull(ref);
        }

        @Override
        public CachedValue value() {
            return new CachedValue(ref.newRef(), false);
        }
    }
}
