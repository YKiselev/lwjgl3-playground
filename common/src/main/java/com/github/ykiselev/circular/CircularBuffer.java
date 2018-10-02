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

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface CircularBuffer<T> {

    /**
     * @return this buffer's capacity
     */
    int capacity();

    /**
     * @return number of elements in this buffer
     */
    int count();

    /**
     * @return {@code true} if this buffer has elements {@code false} otherwise
     */
    default boolean isEmpty() {
        return count() == 0;
    }

    /**
     * Adds supplied value to the end of this buffer.
     *
     * @param value the value to add to the end of buffer
     */
    void write(T value);

    /**
     * Reads and removes first element of this buffer.
     *
     * @return the first element
     */
    T read();

    /**
     * Clears this buffer.
     */
    void clear();

    /**
     * Copies (but does not remove) all elements of this buffer into supplied array. If array's length N is less than
     * this buffer capacity only N first elements are copied.
     *
     * @param dest the destination array
     * @return the number of elements copied
     */
    int copyTo(T[] dest);
}
