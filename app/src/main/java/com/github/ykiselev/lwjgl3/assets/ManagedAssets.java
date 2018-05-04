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
        for (; ; ) {
            final Asset asset = cache.get(resource);
            if (asset != null) {
                return (T) asset.value();
            }
            synchronized (loadLock) {
                if (!cache.containsKey(resource)) {
                    cache.put(
                            resource,
                            asset(
                                    resource,
                                    delegate.tryLoad(resource, clazz, assets),
                                    clazz
                            )
                    );
                }
            }
        }
    }

    private <T> Asset asset(String resource, T value, Class<T> clazz) {
        Class<?> cls = clazz;
        if (cls == null) {
            cls = value.getClass();
        }
        if (cls.isInterface() && AutoCloseable.class.isAssignableFrom(cls)) {
            return new RefAsset<>(
                    resource,
                    (AutoCloseable) value,
                    cls.asSubclass(AutoCloseable.class)
            );
        }
        return new SimpleAsset(resource, value);
    }

    private void remove(String resource, Asset asset, AutoCloseable object) {
        logger.trace("Removing \"{}\"", resource);
        final Asset removed;
        synchronized (loadLock) {
            removed = cache.remove(resource);
        }
        if (asset != removed) {
            logger.error("Expected {} but removed {}!", asset, removed);
        }
        dispose(object);
    }

    private void dispose(AutoCloseable obj) {
        logger.trace("Disposing {}", obj);
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

        String resource();

        Object value();

    }

    /**
     *
     */
    private final class RefAsset<T extends AutoCloseable> implements Asset, AutoCloseable {

        private final String resource;

        private final Ref<T> ref;

        RefAsset(String resource, T value, Class<T> clazz) {
            this.resource = requireNonNull(resource);
            this.ref = new ProxiedRef<>(value, clazz, this::dispose);
        }

        private void dispose(T value) {
            remove(resource, this, value);
        }

        @Override
        public String resource() {
            return resource;
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

        private final String resource;

        private final Object value;

        SimpleAsset(String resource, Object value) {
            this.resource = resource;
            this.value = value;
        }

        @Override
        public String resource() {
            return resource;
        }

        @Override
        public Object value() {
            return value;
        }
    }
}
