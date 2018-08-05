package com.github.ykiselev.wrap;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Wraps {

    public static <T> Wrap<T> simple(T value) {
        return new Wrap<>(value) {
            @Override
            public void close() {
                // no-op
            }
        };
    }

    public static <T extends AutoCloseable> Wrap<T> of(T value) {
        return new Wrap<>(value) {
            @Override
            public void close() {
                try {
                    value.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String toString() {
                return "Wrap{" + value + "}";
            }
        };
    }

}
