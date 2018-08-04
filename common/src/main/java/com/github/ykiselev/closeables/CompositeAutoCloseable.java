package com.github.ykiselev.closeables;

import java.util.Arrays;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CompositeAutoCloseable implements AutoCloseable {

    private final AutoCloseable[] closeables;

    public CompositeAutoCloseable(AutoCloseable... closeables) {
        this.closeables = closeables.clone();
    }

    @Override
    public void close() throws Exception {
        for (AutoCloseable subscription : closeables) {
            subscription.close();
        }
    }

    public CompositeAutoCloseable and(AutoCloseable value) {
        final AutoCloseable[] array = Arrays.copyOf(closeables, closeables.length + 1);
        array[closeables.length] = value;
        return new CompositeAutoCloseable(array);
    }

    public CompositeAutoCloseable reverse() {
        final AutoCloseable[] tmp = new AutoCloseable[closeables.length];
        for (int i = 0; i <= closeables.length / 2; i++) {
            tmp[tmp.length - i - 1] = closeables[i];
            tmp[i] = closeables[closeables.length - i - 1];
        }
        return new CompositeAutoCloseable(tmp);
    }
}
