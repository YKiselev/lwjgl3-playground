package cob.github.ykiselev.lwjgl3.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MapBasedServices implements Services {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<Class, Service> services;

    private final AtomicLong order = new AtomicLong();

    public MapBasedServices(Map<Class, Service> services) {
        this.services = requireNonNull(services);
    }

    public MapBasedServices() {
        this(new ConcurrentHashMap<>());
    }

    private Service wrap(Object instance) {
        return new Service(instance, order.getAndIncrement());
    }

    @Override
    public <T> void add(Class<T> clazz, T instance) {
        final Object previous = services.putIfAbsent(
                clazz,
                wrap(instance)
        );
        if (previous != null) {
            throw new IllegalArgumentException("Instance " + previous + " was already registered as a service type " + clazz);
        }
    }

    @Override
    public <T> void remove(Class<T> clazz, Object instance) {
        final Service service = services.remove(clazz);
        if (service.instance != instance) {
            throw new IllegalArgumentException("Instance " + instance + " is not registered as a service of type " + clazz);
        }
    }

    @Override
    public <T> Optional<T> tryResolve(Class<T> clazz) {
        return Optional.ofNullable(services.get(clazz))
                .map(Service::instance)
                .map(clazz::cast);
    }

    @Override
    public void close() {
        services.values().stream()
                .sorted(Comparator.comparing(Service::order).reversed())
                .forEach(Service::close);
        services.clear();
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
