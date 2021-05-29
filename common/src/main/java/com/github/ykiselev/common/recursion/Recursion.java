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

package com.github.ykiselev.common.recursion;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 10.05.2019
 */
public final class Recursion {

    @FunctionalInterface
    public interface Call<T> {

        Call<T> apply();

        default boolean isComplete() {
            return false;
        }

        default T result() {
            throw new Error("not implemented");
        }
    }

    /**
     * Convenient method for intermediate "recursive" calls.
     *
     * @param nextCall the next tail call.
     * @param <T>      type parameter
     * @return the next call
     */
    public static <T> Call<T> call(Call<T> nextCall) {
        return nextCall;
    }

    /**
     * Use this method to return result.
     *
     * @param value the value to return from {@link Call#result()}
     * @param <T>   type parameter
     * @return the last call with set value
     */
    public static <T> Call<T> done(T value) {
        return new Call<>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public T result() {
                return value;
            }

            @Override
            public Call<T> apply() {
                throw new Error("not implemented");
            }
        };
    }

    /**
     * Use this method to perform "recursive" calls.
     *
     * @param call the call to invoke
     * @param <T>  the type parameter
     * @return the result of the call
     */
    public static <T> Optional<T> invoke(Call<T> call) {
        return Stream.iterate(call, Call::apply)
                .filter(Call::isComplete)
                .findFirst()
                .map(Call::result);
    }
}
