package com.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.lifetime.CountedRef;
import com.github.ykiselev.lifetime.Ref;
import com.github.ykiselev.wrap.Wrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public <T> Wrap<T> tryLoad(String resource, Class<T> clazz, Assets assets) throws ResourceException {
        for (; ; ) {
            final Asset asset = cache.get(resource);
            if (asset != null) {
                return (Wrap<T>) asset.value();
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

    private Asset asset(String resource, Wrap<?> value, Class<?> clazz) {
        Class<?> cls = clazz;
        if (cls == null) {
            cls = value.getClass();
        }
        if (cls.isInterface() && AutoCloseable.class.isAssignableFrom(cls)) {
            return new RefAsset(resource, value, cls);
        }
        return new SimpleAsset(value);
    }

    private void remove(String resource, Asset asset, Object object) {
        logger.trace("Removing \"{}\"", resource);
        final Asset removed;
        synchronized (loadLock) {
            removed = cache.remove(resource);
        }
        if (asset != removed) {
            logger.error("Expected {} but removed {}!", asset, removed);
        }
        if (object instanceof AutoCloseable) {
            dispose((AutoCloseable) object);
        }
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

        Wrap<?> value();

    }

    /**
     *
     */
    private final class RefAsset implements Asset, AutoCloseable {

        private final Class<?> clazz;

        private final Ref<Wrap<?>> ref;

        RefAsset(String resource, Wrap<?> value, Class<?> clazz) {
            this.clazz = requireNonNull(clazz);
            this.ref = new CountedRef<>(
                    value,
                    v -> remove(resource, this, value)
            );
        }

        @Override
        public Wrap<?> value() {
            final Wrap<?> wrap = ref.newRef();
            if (wrap != null) {
                return new Wrap<Object>(wrap.value()) {
                    @Override
                    public void close() {
                        ref.release();
                    }
                };
            }
            return null;
        }

        @Override
        public void close() {
            ref.close();
        }
    }

    /**
     *
     */
    private static final class SimpleAsset implements Asset {

        private final Wrap<?> value;

        SimpleAsset(Wrap<?> value) {
            this.value = value;
        }

        @Override
        public Wrap<?> value() {
            return value;
        }
    }
}
