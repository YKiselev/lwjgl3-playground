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

package com.github.ykiselev.common.math;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 05.04.2019
 */
public final class PowerOfTwo {

    public static int next(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value should be positive: " + value);
        }
        if (value == 0) {
            return 0;
        }
        final int highestOneBit = Integer.highestOneBit(value);
        if (highestOneBit == value) {
            return value;
        }
        return highestOneBit << 1;
    }
}
