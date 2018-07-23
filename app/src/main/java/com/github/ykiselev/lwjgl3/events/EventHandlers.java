package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.lwjgl3.events.layers.EventHandler;

import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class EventHandlers {

    public static <T> EventHandler<T> of(Consumer<T> consumer) {
        return evt -> {
            consumer.accept(evt);
            return evt;
        };
    }
}
