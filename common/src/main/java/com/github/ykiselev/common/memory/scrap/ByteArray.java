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

package com.github.ykiselev.common.memory.scrap;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ByteArray {

    int size();

    /**
     * Resizes the array. Note that only the last array can be resized up.
     *
     * @param value the new length
     */
    void size(int value);

    void set(int index, byte value);

    byte get(int index);

    void set(int fromIndex, byte[] src, int srcFromIndex, int length);

    void get(int fromIndex, byte[] dest, int destFromIndex, int length);

    void fill(int fromIndex, int toIndex, byte value);

    default void fill(byte value) {
        fill(0, size(), value);
    }
}
