package cob.github.ykiselev.lwjgl3.events;

import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Events {

    <T> void subscribe(Class<T> eventType, Consumer<T> handler);

    <T> void unsubscribe(Class<T> eventType, Consumer<T> handler);

    void send(Object message);
}
