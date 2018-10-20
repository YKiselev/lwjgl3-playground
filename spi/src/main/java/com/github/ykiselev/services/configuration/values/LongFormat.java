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

package com.github.ykiselev.services.configuration.values;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public enum LongFormat {

    DECIMAL {
        @Override
        String toString(long value) {
            return Long.toString(value);
        }

        @Override
        long parseLong(String value) {
            return Long.parseLong(value);
        }

        @Override
        Object boxed(long value) {
            return value;
        }
    }, HEXADECIMAL {
        @Override
        String toString(long value) {
            return "0x" + Long.toHexString(value);
        }

        @Override
        long parseLong(String value) {
            if (value != null) {
                if (value.startsWith("0x")) {
                    value = value.substring(2);
                }

                return Long.parseUnsignedLong(value, 16);
            }
            return 0;
        }

        @Override
        Object boxed(long value) {
            return toString(value);
        }
    };

    abstract String toString(long value);

    abstract long parseLong(String value);

    abstract Object boxed(long value);
}
