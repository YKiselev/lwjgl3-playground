/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.common.lifetime;

import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.wrap.Wrap;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Simple value holder with reference counting.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Ref<T> implements AutoCloseable {

    private final Consumer<T> disposer;

    private volatile T reference;

    private volatile long counter = 0;

    /**
     * @param reference the reference to hold.
     * @param disposer  the disposer to use to dispose supplied value when reference counter drops to zero upon calling {@link Ref#release()}.
     */
    public Ref(T reference, Consumer<T> disposer) {
        this.reference = requireNonNull(reference);
        this.disposer = requireNonNull(disposer);
    }

    public static <V extends AutoCloseable> Ref<V> of(V value) {
        return new Ref<>(value, Closeables::close);
    }

    /**
     * Increments reference counter and returns wrapped value. Upon calling {@link Wrap#close()} this reference's counter will be decremented.
     *
     * @return the wrapped value.
     */
    public synchronized Wrap<T> newRef() {
        final T ref = this.reference;
        if (ref == null) {
            return null;
        }
        ++counter;
        return new Wrap<>(ref) {
            @Override
            public void close() {
                release();
            }
        };
    }

    /**
     * Decrements reference counter. If after decrement counter is equal to zero value is disposed using configured {@code disposer}.
     *
     * @return the counter value after decrement.
     */
    public synchronized long release() {
        final long value = --counter;
        freeIfUnused();
        return value;
    }

    private synchronized void freeIfUnused() {
        if (counter == 0 && reference != null) {
            try {
                disposer.accept(reference);
            } finally {
                reference = null;
            }
        }
    }

    @Override
    public void close() {
        freeIfUnused();
        if (reference != null) {
            throw new IllegalStateException("Resource leakage detected: Non-null reference after closing: " + reference);
        }
    }

    @Override
    public String toString() {
        return "CountedRef{" +
                "reference=" + reference +
                ", counter=" + counter +
                '}';
    }
}
