package com.github.ykiselev.lwjgl3.services;

import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MapBasedServices implements Services {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<Class, ServiceHandle<?>> services = new HashMap<>();

    private final Object lock = new Object();

    @Override
    public <T> AutoCloseable add(Class<T> clazz, T instance) {
        final ServiceHandle<T> handle = new ServiceHandle<>(clazz, instance);
        final Object previous;
        synchronized (lock) {
            previous = services.putIfAbsent(clazz, handle);
        }
        if (previous != null) {
            throw new IllegalArgumentException("Instance " + previous + " was already registered as a service type " + clazz);
        }
        logger.debug("{} mapped to {}", clazz, instance);
        return handle;
    }

    @Override
    public <T> Optional<T> tryResolve(Class<T> clazz) {
        final ServiceHandle<?> service;
        synchronized (lock) {
            service = services.get(clazz);
        }
        return Optional.ofNullable(service)
                .map(ServiceHandle::get)
                .map(clazz::cast);
    }

    @Override
    public void close() {
        synchronized (lock) {
            if (!services.isEmpty()) {
                logger.warn("Found abandoned services: {}", services.keySet());
            }
            services.values()
                    .stream()
                    .map(ServiceHandle::get)
                    .forEach(Closeables::close);
            services.clear();
        }
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
            synchronized (lock) {
                if (!services.remove(clazz, this)) {
                    throw new IllegalStateException("Service instance " + instance + " is not mapped to " + clazz);
                }
            }
            logger.debug("Service {} unmapped from {}", clazz, instance);
            Closeables.close(instance);
        }
    }
}
