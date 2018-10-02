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

package com.github.ykiselev.circular;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ArrayCircularBuffer<T> implements CircularBuffer<T> {

    private final T[] buffer;

    /**
     * Index of element for next read operation (0..buffer.length)
     */
    private int head;

    /**
     * Index of element for next write operation (0..buffer.length)
     */
    private int tail;

    /**
     * Actual number of elements in buffer.
     */
    private int count;

    @SuppressWarnings("unchecked")
    public ArrayCircularBuffer(Class<T> elementClass, int capacity) {
        this.buffer = (T[]) Array.newInstance(elementClass, capacity);
    }

    @Override
    public int capacity() {
        return buffer.length;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public void write(T value) {
        if (tail >= buffer.length) {
            tail = 0;
            if (head == tail) {
                head++;
            }
        }
        buffer[tail++] = value;
        count++;
        if (count > buffer.length) {
            count = buffer.length;
        }
    }

    @Override
    public T read() {
        if (count == 0) {
            throw new NoSuchElementException();
        }
        if (head >= buffer.length) {
            head = 0;
        }
        count--;
        return buffer[head++];
    }

    @Override
    public void clear() {
        head = tail = count = 0;
    }

    @Override
    public int copyTo(T[] dest) {
        if (head < tail) {
            System.arraycopy(buffer, head, dest, 0, count);
            return count;
        } else if (count > 0) {
            final int chunk = buffer.length - head;
            System.arraycopy(buffer, head, dest, 0, chunk);
            System.arraycopy(buffer, 0, dest, chunk, tail);
            return tail + chunk;
        }
        return 0;
    }
}
