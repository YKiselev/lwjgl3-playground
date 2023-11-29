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
package com.github.ykiselev.wrap

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
object Wraps {
    /**
     * Convenient method to wrap instance of class that doesn't implement [AutoCloseable].
     * Note: wrapper created by this method upon calling method [Wrap.close] does nothing.
     *
     * @param value the value to wrap
     * @param [T]   the type parameter
     * @return the no-op wrapper
     */
    @JvmStatic
    fun <T> noop(value: T): Wrap<T> =
        object : Wrap<T>(value) {
            override fun close() {
                // no-op
            }
        }

    /**
     * Use this method to wrap instances of classes implementing [AutoCloseable].
     *
     * @param value the value to wrap
     * @param [T]   the type parameter
     * @return the wrapper of supplied value which will call [AutoCloseable.close] on contained instance upon calling [Wrap.close]
     */
    @JvmStatic
    fun <T : AutoCloseable> of(value: T): Wrap<T> =
        object : Wrap<T>(value) {
            override fun close() {
                try {
                    value.close()
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }
}
