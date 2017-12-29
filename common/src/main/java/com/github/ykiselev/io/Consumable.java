package com.github.ykiselev.io;

import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Consumable<T> {

    void consume(Consumer<T> consumer);
}
