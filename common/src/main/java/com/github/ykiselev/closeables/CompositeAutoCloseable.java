package com.github.ykiselev.closeables;

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
}
