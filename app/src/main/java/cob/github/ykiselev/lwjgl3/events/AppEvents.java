package cob.github.ykiselev.lwjgl3.events;

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

    private final Map<Class, Collection<Consumer>> subscribers = new ConcurrentHashMap<>();

    private final Map<Class, Class> cache = new ConcurrentHashMap<>();

    @Override
    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        final Collection<Consumer> consumers = subscribers.computeIfAbsent(
                eventType,
                key -> new CopyOnWriteArrayList<>()
        );
        for (Consumer consumer : consumers) {
            if (consumer == handler) {
                return;
            }
        }
        consumers.add(handler);
        cache.clear();
    }

    @Override
    public <T> void unsubscribe(Class<T> eventType, Consumer<T> handler) {
        final Collection<Consumer> consumers = subscribers.get(eventType);
        if (consumers != null) {
            if (consumers.remove(handler)) {
                cache.clear();
                return;
            }
        }
        throw new IllegalArgumentException("Handler " + handler + " is not registered for " + eventType);
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

    private Collection<Consumer> find(Class eventType) {
        return subscribers.get(
                getOrRefine(eventType)
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void send(Object message) {
        final Collection<Consumer> consumers = find(message.getClass());
        if (consumers != null) {
            for (Consumer consumer : consumers) {
                consumer.accept(message);
            }
        }
    }
}
