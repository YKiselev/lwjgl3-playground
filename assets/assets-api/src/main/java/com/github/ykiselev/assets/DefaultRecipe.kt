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
package com.github.ykiselev.assets

import java.util.*

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 14.04.2019
 */
class DefaultRecipe<K, A, C>(val key: K, val type: Class<A>, private val context: C) : Recipe<K, A, C> {

    override fun type(): Class<A> {
        return type
    }

    override fun key(): K {
        return key
    }

    override fun context(): C {
        return context
    }

    class Dummy

    companion object {

        private val dummy = Dummy()

        @JvmStatic
        fun <A, K> of(key: K, type: Class<A>): Recipe<K, A, Dummy> {
            return DefaultRecipe(key, type, dummy)
        }

        @JvmStatic
        fun <A> of(type: Class<A>): Recipe<String, A, Dummy> {
            return DefaultRecipe(type.getName(), type, dummy)
        }

        @JvmStatic
        fun <A, C> of(type: Class<A>, context: C): Recipe<String, A, C> {
            return DefaultRecipe(type.getName(), type, context)
        }
    }
}
