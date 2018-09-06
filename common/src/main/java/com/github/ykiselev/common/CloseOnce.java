package com.github.ykiselev.common;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 * @deprecated seems to be unused
 */
@Deprecated
public final class CloseOnce implements AutoCloseable {

    private volatile AutoCloseable closeable;

    public boolean isClosed() {
        return closeable == null;
    }

    public CloseOnce(AutoCloseable delegate) {
        this.closeable = requireNonNull(delegate);
    }

    @Override
    public synchronized void close() throws Exception {
        final AutoCloseable c = closeable;
        if (c != null) {
            try {
                c.close();
            } finally {
                closeable = null;
            }
        }
    }
}
