package com.github.ykiselev.spi;

import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.services.Services;
import com.typesafe.config.ConfigObject;

import java.lang.reflect.InvocationTargetException;
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
        final Class<?> iface, impl;
        try {
            iface = Class.forName(key);
            impl = Class.forName(value);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!iface.isAssignableFrom(impl) && !ServiceFactory.class.isAssignableFrom(impl)) {
            throw new IllegalArgumentException(impl + " does not implement " + iface);
        }
        return new SimpleImmutableEntry<>(iface, impl);
    }

    @SuppressWarnings("unchecked")
    private <T> AutoCloseable add(Class<T> iface, Class<?> impl, Services services) {
        final T instance;
        if (ServiceFactory.class.isAssignableFrom(impl)) {
            final ServiceFactory<T> factory = newInstance((Class<ServiceFactory<T>>) impl);
            instance = factory.create(services);
        } else {
            instance = newInstance((Class<T>) impl);
        }
        if (!iface.isInstance(instance)) {
            throw new IllegalArgumentException("Expected instance of " + iface + " got " + instance);
        }
        return services.add(iface, instance);
    }

    private <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
