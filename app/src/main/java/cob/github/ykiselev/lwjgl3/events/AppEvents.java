package cob.github.ykiselev.lwjgl3.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppEvents implements Events {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<Class, Collection<ClosableConsumer>> subscribers = new ConcurrentHashMap<>();

    private final Map<Class, Class> cache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> AutoCloseable subscribe(Class<T> eventClass, Consumer<T> handler) {
        final Collection<ClosableConsumer> consumers = this.subscribers.computeIfAbsent(
                eventClass,
                k -> new CopyOnWriteArrayList<>()
        );
        ClosableConsumer result = null;
        for (ClosableConsumer consumer : consumers) {
            if (consumer.delegatesTo(handler)) {
                logger.warn("Already subscribed: {}", handler);
                result = consumer;
                break;
            }
        }
        if (result == null) {
            result = new ClosableConsumer<>(handler);
            consumers.add(result);
            clearCache();
        }
        return result;
    }

    private void clearCache() {
        cache.clear();
    }

    private Class getOrRefine(Class eventType) {
        return requireNonNull(
                cache.computeIfAbsent(eventType, this::refine),
                "No subscribers for " + eventType
        );
    }

    private Class refine(Class eventType) {
        Class clazz = eventType;
        while (clazz != Object.class) {
            if (subscribers.containsKey(clazz)) {
                return clazz;
            }
            for (Class iface : clazz.getInterfaces()) {
                if (subscribers.containsKey(iface)) {
                    return iface;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private Collection<ClosableConsumer> find(Class eventType) {
        return subscribers.get(
                getOrRefine(eventType)
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void send(Object message) {
        final Collection<ClosableConsumer> consumers = find(message.getClass());
        if (consumers != null) {
            consumers.forEach(h -> h.accept(message));
        }
    }

    private static final class ClosableConsumer<E> implements AutoCloseable {

        private volatile Consumer<E> consumer;

        ClosableConsumer(Consumer<E> consumer) {
            this.consumer = requireNonNull(consumer);
        }

        void accept(E e) {
            final Consumer<E> c = this.consumer;
            if (c != null) {
                c.accept(e);
            }
        }

        @Override
        public void close() {
            consumer = null;
        }

        boolean delegatesTo(Consumer c) {
            return c == consumer;
        }
    }
}
