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

package com.github.ykiselev.wrap;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Wraps {

    /**
     * Convenient method to wrap instance of class that doesn't implement {@link AutoCloseable}.
     * Note: wrapper created by this method upon calling method {@link Wrap#close()} does nothing.
     *
     * @param value the value to wrap
     * @param <T>   the type parameter
     * @return the no-op wrapper
     */
    public static <T> Wrap<T> simple(T value) {
        return new Wrap<>(value) {
            @Override
            public void close() {
                // no-op
            }
        };
    }

    /**
     * Use this method to wrap instances of classes implementing {@link AutoCloseable}.
     *
     * @param value the value to wrap
     * @param <T>   the type parameter
     * @return the wrapper of supplied value which will call {@link AutoCloseable#close()} on contained instance upon calling {@link Wrap#close()}
     */
    public static <T extends AutoCloseable> Wrap<T> of(T value) {
        return new Wrap<>(value) {
            @Override
            public void close() {
                try {
                    value.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String toString() {
                return "Wrap{" + value + "}";
            }
        };
    }

}
