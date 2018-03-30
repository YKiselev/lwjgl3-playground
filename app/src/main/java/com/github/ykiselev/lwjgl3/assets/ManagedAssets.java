package com.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.lifetime.ProxiedRef;
import com.github.ykiselev.lifetime.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ManagedAssets implements Assets, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Assets delegate;

    private final Map<String, Asset> cache = new ConcurrentHashMap<>();

    private final Object loadLock = new Object();

    public ManagedAssets(Assets delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T tryLoad(String resource, Class<T> clazz, Assets assets) throws ResourceException {
        final Asset asset = cache.get(resource);
        if (asset != null) {
            return (T) asset.value();
        }
        return (T) load(resource, clazz, assets);
    }

    private <T> Object load(String resource, Class<T> clazz, Assets assets) {
        synchronized (loadLock) {
            final Asset asset = cache.get(resource);
            if (asset != null) {
                return asset.value();
            }
            final Asset loaded = asset(
                    delegate.tryLoad(resource, clazz, assets),
                    clazz
            );
            cache.put(resource, loaded);
            return loaded.value();
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

    /**
     *
     */
    private interface Asset {

        Object value();

    }

    /**
     *
     */
    private final class RefAsset implements Asset, AutoCloseable {

        private final Ref<?> ref;

        RefAsset(Ref<?> ref) {
            this.ref = requireNonNull(ref);
        }

        @Override
        public Object value() {
            logNewRef();
            return ref.newRef();
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

    /**
     *
     */
    private static final class SimpleAsset implements Asset {

        private final Object value;

        SimpleAsset(Object value) {
            this.value = value;
        }

        @Override
        public Object value() {
            return value;
        }
    }
}
