package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.lifetime.ProxiedRef;
import com.github.ykiselev.lifetime.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ManagedAssets implements Assets, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            for (int i = 0; i < 2; i++) {
                final CachedValue cached = getCached(resource);
                final Object value = cached.value();
                if (value != null || cached.skipLoading()) {
                    return Optional.ofNullable((T) value);
                }
                doLoad(resource, clazz, assets);
            }
        } finally {
            lock.readLock().unlock();
        }
        throw new IllegalStateException("Should not be here!");
    }

    private CachedValue getCached(String resource) {
        final Asset asset = cache.get(resource);
        if (asset != null) {
            return asset.value();
        }
        return MISSING_VALUE;
    }

    private <T> void doLoad(String resource, Class<T> clazz, Assets assets) {
        lock.readLock().unlock();
        lock.writeLock().lock();
        try {
            lock.readLock().lock();
            // After write lock acquired we should check if resource was already loaded by another thread
            final CachedValue cached = getCached(resource);
            final Object value = cached.value();
            if (value != null || cached.skipLoading()) {
                return;
            }
            // Not loaded yet, proceed
            final Optional<T> result = delegate.tryLoad(resource, clazz, assets);
            cache.put(
                    resource,
                    result.map(v -> asset(v, clazz))
                            .orElse(NON_EXISTING_ASSET)
            );
        } finally {
            lock.writeLock().unlock();
        }
    }

    private <T> Asset asset(T value, Class<T> clazz) {
        Class<?> cls = clazz;
        if (cls == null) {
            cls = value.getClass();
        }
        if (cls.isInterface() && AutoCloseable.class.isAssignableFrom(cls)) {
            return proxiedAsset(
                    (AutoCloseable) value,
                    cls.asSubclass(AutoCloseable.class)
            );
        }
        return new SimpleAsset(value);
    }

    private <T extends AutoCloseable> Asset proxiedAsset(T value, Class<T> clazz) {
        return new RefAsset(
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

    @Override
    public void close() {
        Closeables.close(delegate);
        cache.values().forEach(Closeables::close);
        cache.clear();
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

        Object value() {
            return value;
        }

        CachedValue(Object value, boolean skipLoading) {
            this.value = value;
            this.skipLoading = skipLoading;
        }
    }

    private final class RefAsset implements Asset, AutoCloseable {

        private final Ref<?> ref;

        RefAsset(Ref<?> ref) {
            this.ref = requireNonNull(ref);
        }

        @Override
        public CachedValue value() {
            logNewRef();
            return new CachedValue(ref.newRef(), false);
        }

        @Override
        public void close() {
            try {
                ref.close();
            } catch (Exception e) {
                logger.error("Unable to close reference!", e);
            }
        }

        private void logNewRef() {
            if (!logger.isTraceEnabled()) {
                return;
            }
            logger.trace(
                    "{}:\n{}",
                    ref,
                    Arrays.stream(
                            new RuntimeException("").getStackTrace()
                    ).map(Object::toString)
                            .map(s -> "  " + s)
                            .collect(Collectors.joining("\n"))
            );
        }
    }

    private static final class SimpleAsset implements Asset {

        private final Object value;

        SimpleAsset(Object value) {
            this.value = value;
        }

        @Override
        public CachedValue value() {
            return new CachedValue(value, true);
        }
    }
}
