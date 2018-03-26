package com.github.ykiselev.lwjgl3.events;

import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Events {

    <T> AutoCloseable subscribe(Class<T> eventClass, Consumer<T> handler);

    void send(Object message);
}
