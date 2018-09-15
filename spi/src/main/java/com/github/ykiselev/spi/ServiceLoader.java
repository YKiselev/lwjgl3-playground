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

package com.github.ykiselev.spi;

import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.services.Services;
import com.typesafe.config.ConfigObject;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ServiceLoader {

    private final List<? extends ConfigObject> config;

    public ServiceLoader(List<? extends ConfigObject> config) {
        this.config = requireNonNull(config);
    }

    public Stream<Map.Entry<Class<?>, Class<?>>> loadAll() {
        return config.stream()
                .flatMap(this::load);
    }

    public AutoCloseable loadAll(Services services) {
        return new CompositeAutoCloseable(
                loadAll()
                        .map(e -> add(e.getKey(), e.getValue(), services))
                        .toArray(AutoCloseable[]::new)
        ).reverse();
    }

    private Stream<Map.Entry<Class<?>, Class<?>>> load(ConfigObject object) {
        return object.entrySet()
                .stream()
                .map(e -> load(e.getKey(), (String) e.getValue().unwrapped()));
    }

    private Map.Entry<Class<?>, Class<?>> load(String key, String value) {
        final Class<?> iface = ClassFromName.get(key);
        final Class<?> impl = ClassFromName.get(value);
        if (!iface.isAssignableFrom(impl) && !Factory.class.isAssignableFrom(impl)) {
            throw new IllegalArgumentException(impl + " does not implement " + iface);
        }
        return new SimpleImmutableEntry<>(iface, impl);
    }

    private <T> AutoCloseable add(Class<T> iface, Class<?> impl, Services services) {
        final T instance = new InstanceFromClass<T>(impl, services).get();
        if (!iface.isInstance(instance)) {
            throw new IllegalArgumentException("Expected instance of " + iface + " got " + instance);
        }
        return services.add(iface, instance);
    }
}
