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
package com.github.ykiselev.spi.services.configuration.values

import java.util.*

typealias StringGetter = () -> String
typealias StringSetter = ((String) -> Unit)
typealias BooleanGetter = () -> Boolean
typealias BooleanSetter = ((Boolean) -> Unit)
typealias LongGetter = () -> Long
typealias LongSetter = (Long) -> Unit
typealias DoubleGetter = () -> Double
typealias DoubleSetter = (Double) -> Unit

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class Values {
    class WiredString(
        name: String,
        persisted: Boolean,
        private val getter: StringGetter,
        private val setter: StringSetter?
    ) : ConfigValue(name, persisted) {

        fun value(): String =
            getter()

        fun value(value: String) {
            setter?.invoke(value)
        }

        override fun fromObject(value: Any) {
            setString(Objects.toString(value, null))
        }

        override fun toString(): String =
            value()

        override fun setString(value: String) =
            value(value)

        override fun boxed(): Any =
            value()

        override val isReadOnly: Boolean
            get() = setter != null
    }

    class WiredBoolean(
        name: String,
        persisted: Boolean,
        private val getter: BooleanGetter,
        private val setter: BooleanSetter?
    ) : ConfigValue(name, persisted) {

        fun value(): Boolean =
            getter()

        fun value(value: Boolean) {
            setter?.invoke(value)
        }

        override fun fromObject(value: Any) {
            if (value is Boolean) {
                value(value)
            } else {
                setString(Objects.toString(value, ""))
            }
        }

        override fun toString(): String =
            value().toString()

        override fun setString(value: String) {
            value(value.toBoolean())
        }

        override fun boxed(): Any =
            value()

        override val isReadOnly: Boolean
            get() = setter != null
    }

    class WiredLong(
        name: String,
        persisted: Boolean,
        private val format: LongFormat,
        private val getter: LongGetter,
        private val setter: LongSetter?
    ) : ConfigValue(name, persisted) {
        fun value(): Long =
            getter()

        fun value(value: Long) {
            setter?.invoke(value)
        }

        override fun fromObject(value: Any) {
            if (value is Number) {
                value(value.toLong())
            } else {
                setString(Objects.toString(value, "0"))
            }
        }

        override fun toString(): String =
            format.toString(value())

        override fun setString(value: String) {
            value(format.parseLong(value))
        }

        override fun boxed(): Any =
            format.boxed(value())

        override val isReadOnly: Boolean
            get() = setter != null
    }

    class WiredDouble(
        name: String,
        persisted: Boolean,
        private val getter: DoubleGetter,
        private val setter: DoubleSetter?
    ) : ConfigValue(name, persisted) {
        fun value(): Double =
            getter()


        fun value(value: Double) {
            setter?.invoke(value)
        }

        override fun fromObject(value: Any) {
            if (value is Number) {
                value(value.toDouble())
            } else {
                setString(Objects.toString(value, "0"))
            }
        }

        override fun toString(): String =
            value().toString()

        override fun setString(value: String) {
            value(value.toDouble())
        }

        override fun boxed(): Any =
            value()

        override val isReadOnly: Boolean
            get() = setter != null
    }
}
