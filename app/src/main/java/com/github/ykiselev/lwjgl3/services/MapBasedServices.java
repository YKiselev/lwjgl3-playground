package com.github.ykiselev.lwjgl3.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MapBasedServices implements Services {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<Class, Service> services = new HashMap<>();

    private final AtomicLong order = new AtomicLong();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private Service wrap(Object instance) {
        return new Service(instance, order.getAndIncrement());
    }

    @Override
    public <T> void add(Class<T> clazz, T instance) {
        lock.writeLock().lock();
        try {
            final Object previous = services.putIfAbsent(
                    clazz,
                    wrap(instance)
            );
            if (previous != null) {
                throw new IllegalArgumentException("Instance " + previous + " was already registered as a service type " + clazz);
            }
            logger.debug("{} mapped to {}", clazz, instance);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public <T> void remove(Class<T> clazz, Object instance) {
        lock.writeLock().lock();
        try {
            final Service service = services.get(clazz);
            if (service.instance != instance) {
                throw new IllegalArgumentException("Instance " + instance + " is not registered as a service of type " + clazz);
            }
            services.remove(clazz);
            logger.debug("{} unmapped from {}", clazz, instance);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public <T> Optional<T> tryResolve(Class<T> clazz) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(services.get(clazz))
                    .map(Service::instance)
                    .map(clazz::cast);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T> T resolveOrAdd(Class<T> clazz, Supplier<T> supplier) {
        return tryResolve(clazz).orElseGet(
                () -> {
                    lock.writeLock().lock();
                    try {
                        return tryResolve(clazz).orElseGet(
                                () -> {
                                    add(clazz, supplier.get());
                                    return tryResolve(clazz)
                                            .orElseThrow(IllegalStateException::new);
                                }
                        );
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
        );
    }

    @Override
    public <T extends Removable> Optional<T> tryRemove(Class<T> clazz) {
        lock.writeLock().lock();
        try {
            return tryResolve(clazz)
                    .map(s -> {
                                if (s.canBeRemoved()) {
                                    remove(clazz, s);
                                    return s;
                                }
                                return null;
                            }
                    );
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void close() {
        lock.writeLock().lock();
        try {
            services.values().stream()
                    .sorted(Comparator.comparing(Service::order).reversed())
                    .forEach(Service::close);
            services.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Service value class.
     * Holds service instance along with order of registration.
     */
    private final class Service {

        final Object instance;

        final long order;

        Object instance() {
            return instance;
        }

        long order() {
            return order;
        }

        Service(Object instance, long order) {
            this.instance = requireNonNull(instance);
            this.order = order;
        }

        void close() {
            if (instance instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) instance).close();
                } catch (Exception e) {
                    logger.error("Failed to close service {}!", instance, e);
                }
            }
        }
    }
}
