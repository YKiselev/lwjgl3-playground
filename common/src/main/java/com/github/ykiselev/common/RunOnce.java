package com.github.ykiselev.common;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class RunOnce implements Runnable {

    private volatile Runnable delegate;

    public boolean wasRun() {
        return delegate == null;
    }

    public RunOnce(Runnable runnable) {
        this.delegate = requireNonNull(runnable);
    }

    @Override
    public synchronized void run() {
        final Runnable r = delegate;
        if (r != null) {
            try {
                r.run();
            } finally {
                delegate = null;
            }
        }
    }
}
