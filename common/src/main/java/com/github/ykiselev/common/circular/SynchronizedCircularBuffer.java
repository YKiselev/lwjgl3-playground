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

package com.github.ykiselev.common.circular;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SynchronizedCircularBuffer<T> implements CircularBuffer<T> {

    private final Object lock = new Object();

    private final CircularBuffer<T> delegate;

    public SynchronizedCircularBuffer(CircularBuffer<T> delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public int capacity() {
        synchronized (lock) {
            return delegate.capacity();
        }
    }

    @Override
    public int count() {
        synchronized (lock) {
            return delegate.count();
        }
    }

    @Override
    public void write(T value) {
        synchronized (lock) {
            delegate.write(value);
        }
    }

    @Override
    public T read() {
        synchronized (lock) {
            return delegate.read();
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            delegate.clear();
        }
    }

    @Override
    public int copyTo(T[] dest) {
        synchronized (lock) {
            return delegate.copyTo(dest);
        }
    }

    @Override
    public T get(int index) {
        synchronized (lock) {
            return delegate.get(index);
        }
    }
}
