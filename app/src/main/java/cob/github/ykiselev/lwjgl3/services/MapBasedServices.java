package cob.github.ykiselev.lwjgl3.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MapBasedServices implements Services {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<Class, Object> services;

    public MapBasedServices(Map<Class, Object> services) {
        this.services = requireNonNull(services);
    }

    public MapBasedServices() {
        this(new ConcurrentHashMap<>());
    }

    @Override
    public <T> void add(Class<T> clazz, T instance) {
        final Object previous = services.putIfAbsent(clazz, instance);
        if (previous != null) {
            throw new IllegalArgumentException("Instance " + previous + " was already registered as a service type " + clazz);
        }
    }

    @Override
    public <T> void remove(Class<T> clazz, Object instance) {
        final Object service = services.remove(clazz);
        if (service != instance) {
            throw new IllegalArgumentException("Instance " + instance + " is not registered as a service of type " + clazz);
        }
    }

    @Override
    public <T> T resolve(Class<T> clazz) {
        return requireNonNull(
                tryResolve(clazz),
                "Service not found: " + clazz
        );
    }

    @Override
    public <T> T tryResolve(Class<T> clazz) {
        return clazz.cast(services.get(clazz));
    }

    @Override
    public void close() {
        services.values()
                .stream()
                .filter(AutoCloseable.class::isInstance)
                .forEach(c -> {
                    try {
                        ((AutoCloseable) c).close();
                    } catch (Exception e) {
                        logger.error("Failed to close service {}!", c, e);
                    }
                });
        services.clear();
    }
}
