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

package com.github.ykiselev.playground.events;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Array of handlers. Expected to be small thus each time new handler is added or removed array is expanded/trimmed by one element.
 * This class is thread-safe and lock-free.
 *
 * @param <T> type parameter
 */
final class Delegates<T> {

    private volatile T[] items;

    private static final VarHandle handle;

    static {
        try {
            handle = MethodHandles.lookup()
                    .findVarHandle(Delegates.class, "items", Object[].class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    /**
     * Empty array expected.
     *
     * @param items the typed array to use as reference when copying.
     */
    Delegates(T[] items) {
        this.items = items.clone();
    }

    /**
     * Adds new element to array if there is no such element (elements are compared by reference).
     *
     * @param item the item to add to the end of array
     * @return the handle which may be used to remove item later
     */
    AutoCloseable add(T item) {
        for (; ; ) {
            final T[] prevArray = array();
            final int existing = indexOf(prevArray, item);
            if (existing >= 0) {
                return handle(item);
            }
            final T[] newArray = Arrays.copyOf(prevArray, prevArray.length + 1);
            newArray[prevArray.length] = item;
            if (handle.compareAndSet(this, prevArray, newArray)) {
                return handle(item);
            }
        }
    }

    private AutoCloseable handle(T handler) {
        return () -> remove(handler);
    }

    private void remove(T handler) {
        for (; ; ) {
            final T[] prevArray = array();
            final int idx = indexOf(prevArray, handler);
            if (idx < 0) {
                break;
            }
            @SuppressWarnings("unchecked") final T[] newArray = (T[]) Array.newInstance(
                    prevArray.getClass().getComponentType(),
                    prevArray.length - 1
            );
            if (idx > 0) {
                System.arraycopy(prevArray, 0, newArray, 0, idx);
            }
            if (idx < newArray.length) {
                System.arraycopy(prevArray, idx + 1, newArray, idx, newArray.length - idx);
            }
            if (handle.compareAndSet(Delegates.this, prevArray, newArray)) {
                break;
            }
        }
    }

    private static <T> int indexOf(T[] array, T value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    T[] array() {
        return items;
    }
}
