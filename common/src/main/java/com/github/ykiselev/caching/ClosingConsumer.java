package com.github.ykiselev.caching;

import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ClosingConsumer<V> implements Consumer<V> {

    @Override
    public void accept(V v) {
        if (v instanceof AutoCloseable) {
            try {
                ((AutoCloseable) v).close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
