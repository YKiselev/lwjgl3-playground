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

package com.github.ykiselev.lifetime;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CountedRef<T> implements Ref<T> {

    private final Consumer<T> disposer;

    private volatile T reference;

    private volatile long counter = 0;

    public CountedRef(T reference, Consumer<T> disposer) {
        this.reference = requireNonNull(reference);
        this.disposer = requireNonNull(disposer);
    }

    @Override
    public synchronized T newRef() {
        final T ref = this.reference;
        if (ref == null) {
            return null;
        }
        ++counter;
        return ref;
    }

    @Override
    public synchronized long release() {
        final long value = --counter;
        if (value == 0 && reference != null) {
            try {
                disposer.accept(reference);
            } finally {
                reference = null;
            }
        }
        return value;
    }

    @Override
    public void close() {
        release();
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
