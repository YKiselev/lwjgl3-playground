package com.github.ykiselev.closeables;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Closeables {

    public static void close(Object obj) throws RuntimeException {
        if (obj instanceof AutoCloseable) {
            try {
                ((AutoCloseable) obj).close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
