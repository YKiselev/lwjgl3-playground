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

package com.github.ykiselev.assets;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created by Y.Kiselev on 16.05.2016.
 */
public final class ManagedAssets implements Assets, AutoCloseable {

    private final Assets delegate;

    private final Map<String, Optional<?>> cache;

    public ManagedAssets(Assets delegate, Map<String, Optional<?>> cache) {
        this.delegate = requireNonNull(delegate);
        this.cache = requireNonNull(cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> tryLoad(String resource, Class<T> clazz, Assets assets) throws ResourceException {
        final Optional<?> opt = cache.get(resource);
        if (opt != null) {
            return (Optional<T>) opt;
        }
        cache.putIfAbsent(resource, delegate.tryLoad(resource, clazz, assets));
        return (Optional<T>) cache.get(resource);
    }

    @Override
    public <T> ReadableAsset<T> resolve(String resource, Class<T> clazz) throws ResourceException {
        return delegate.resolve(resource, clazz);
    }

    @Override
    public void close() {
        cache.forEach((key, value) -> value.ifPresent(this::close));
        cache.clear();
    }

    private void close(Object asset) {
        try {
            if (asset instanceof AutoCloseable) {
                ((AutoCloseable) asset).close();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
