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
package com.github.ykiselev.common.closeables

import com.github.ykiselev.wrap.Wrap
import java.util.function.UnaryOperator


/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
object Closeables {

    @JvmStatic
    fun closeAll(vararg closeables: AutoCloseable?) {
        closeAll(closeables.asIterable())
    }

    @JvmStatic
    fun closeAll(closeables: Iterable<AutoCloseable?>) {
        var ex: RuntimeException? = null
        for (closeable in closeables) {
            closeSilently(closeable)?.also {
                if (ex != null) {
                    ex?.addSuppressed(it)
                } else {
                    ex = it
                }
            }
        }
        if (ex != null) {
            throw RuntimeException("Failed to close all delegates!", ex)
        }
    }

    @JvmStatic
    fun closeIfNeeded(obj: Any?) {
        if (obj is AutoCloseable) {
            close(obj)
        }
    }

    @JvmStatic
    fun close(obj: AutoCloseable) {
        try {
            obj.close()
        } catch (e: RuntimeException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun closeSilently(obj: AutoCloseable?): RuntimeException? {
        return try {
            obj?.close()
            null
        } catch (e: Exception) {
            RuntimeException(e)
        }
    }

    /**
     * @return new guard instance
     */
    @JvmStatic
    fun newGuard(): Guard = Guard()

    /**
     * Creates composite closeable.
     * @return new composite closeable instance for supplied arguments.
     */
    @JvmStatic
    fun compose(vararg c: AutoCloseable): Composite = Composite(*c)

    /**
     *
     */
    class Guard : AutoCloseable {

        private val closeables: MutableList<AutoCloseable> = mutableListOf()

        fun <T : AutoCloseable> add(closeable: T): T {
            closeables.add(closeable)
            return closeable
        }

        /**
         * Use this method when you intend to control added object's lifetime via detached AutoCloseable.
         * @param wrap wrapped object
         * @return object
         */
        fun <T> add(wrap: Wrap<out T>): T {
            closeables.add(wrap)
            return wrap.value()
        }

        /**
         * Use this method when you are using this Guard only to temporarily keep wrapped object(s) till the end of complex initialization
         * @param wrap wrapped object
         * @return wrapped object
         */
        fun <T> hold(wrap: Wrap<T>): Wrap<T> {
            closeables.add(wrap)
            return wrap
        }

        fun detach(): AutoCloseable =
            closeables.toTypedArray<AutoCloseable>().let {
                closeables.clear()
                Composite(*it)
            }

        override fun close() =
            closeAll(closeables)
    }

    class Composite(private vararg val items: AutoCloseable) : AutoCloseable {

        override fun close() {
            closeAll(*items)
        }

        fun and(value: AutoCloseable): Composite {
            return Composite(*this.items, value)
        }

        fun with(value: UnaryOperator<Composite>): Composite {
            return value.apply(this)
        }

        fun reverse(): Composite =
            Composite(*items.reversedArray())
    }
}
