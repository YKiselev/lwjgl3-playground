/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.playground.services.assets;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.assets.ReadableAsset;
import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.lifetime.CountedRef;
import com.github.ykiselev.lifetime.Ref;
import com.github.ykiselev.wrap.Wrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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
                // We can't use  Map#computeIfAbsent because asset can be composite and call this method recursively
                if (!cache.containsKey(resource)) {
                    final Wrap<T> wrap = delegate.tryLoad(resource, clazz, assets);
                    if (wrap != null) {
                        cache.put(
                                resource,
                                new RefAsset(resource, wrap)
                        );
                    } else {
                        return null;
                    }
                }
            }
        }
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

    @Override
    public Optional<ReadableByteChannel> open(String resource) throws ResourceException {
        return delegate.open(resource);
    }

    @Override
    public Stream<ReadableByteChannel> openAll(String resource) throws ResourceException {
        return delegate.openAll(resource);
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

        private final Ref<Wrap<?>> ref;

        RefAsset(String resource, Wrap<?> value) {
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

}
