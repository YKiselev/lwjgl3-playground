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

package com.github.ykiselev.playground.services;

import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.common.cow.CopyOnModify;
import com.github.ykiselev.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MapBasedServices implements Services {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CopyOnModify<Map<Class, ServiceHandle<?>>> cow = new CopyOnModify<>(Collections.emptyMap());

    @Override
    public <T> AutoCloseable add(Class<T> clazz, T instance) {
        final ServiceHandle<T> handle = new ServiceHandle<>(clazz, instance);
        cow.modify(before -> {
            final Map<Class, ServiceHandle<?>> after = new HashMap<>(before);
            final ServiceHandle<?> previous = after.putIfAbsent(clazz, handle);
            if (previous != null) {
                throw new IllegalArgumentException("Instance " + previous + " was already registered as a service type " + clazz);
            }
            return after;
        });
        logger.debug("{} mapped to {}", clazz, instance);
        return handle;
    }

    @Override
    public <T> T tryResolve(Class<T> clazz) {
        final ServiceHandle<?> handle = cow.value().get(clazz);
        if (handle != null) {
            return clazz.cast(handle.get());
        }
        return null;
    }

    @Override
    public void close() {
        final Map<Class, ServiceHandle<?>> before = cow.modify(v -> Collections.emptyMap());
        if (!before.isEmpty()) {
            logger.warn("Found abandoned services: {}", before.keySet());
        }
        before.forEach((k, v) -> Closeables.close(v));
    }

    private final class ServiceHandle<T> implements AutoCloseable {

        private final Class<T> clazz;

        private final T instance;

        ServiceHandle(Class<T> clazz, T instance) {
            this.clazz = requireNonNull(clazz);
            this.instance = requireNonNull(instance);
        }

        public T get() {
            return instance;
        }

        @Override
        public void close() {
            cow.modify(before -> {
                final Map<Class, ServiceHandle<?>> after = new HashMap<>(before);
                if (!after.remove(clazz, this)) {
                    throw new IllegalStateException("Service instance " + instance + " is not mapped to " + clazz);
                }
                return after;
            });
            logger.debug("Service {} unmapped from {}", clazz, instance);
            Closeables.close(instance);
        }
    }
}
