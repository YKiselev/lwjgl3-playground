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
package com.github.ykiselev.spi.services.configuration

import com.github.ykiselev.spi.services.configuration.values.*
import com.github.ykiselev.spi.services.configuration.values.Values.*
import java.util.function.IntConsumer
import java.util.function.IntSupplier

/**
 * Wired values map builder.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class WiredValues(private val cfg: PersistedConfiguration) {

    private val values: MutableList<ConfigValue> = mutableListOf()

    private fun add(value: ConfigValue): WiredValues {
        values.add(value)
        return this
    }

    fun withString(
        name: String,
        getter: StringGetter,
        setter: StringSetter?,
        persisted: Boolean
    ): WiredValues =
        add(WiredString(name, persisted, getter, setter))

    fun withString(name: String, getter: StringGetter, persisted: Boolean): WiredValues =
        withString(name, getter, null, persisted)

    fun withBoolean(
        name: String,
        getter: BooleanGetter,
        setter: BooleanSetter?,
        persisted: Boolean
    ): WiredValues =
        add(WiredBoolean(name, persisted, getter, setter))

    fun withBoolean(name: String, getter: BooleanGetter, persisted: Boolean): WiredValues =
        withBoolean(name, getter, null, persisted)

    fun withInt(
        name: String,
        getter: IntSupplier,
        setter: IntConsumer?,
        persisted: Boolean,
        format: LongFormat
    ): WiredValues =
        add(
            WiredLong(
                name,
                persisted,
                format,
                {
                    getter.asInt.toLong()
                }
            ) { v: Long ->
                setter?.accept(v.toInt())
            }
        )

    fun withInt(name: String, getter: IntSupplier, setter: IntConsumer?, persisted: Boolean): WiredValues =
        withInt(name, getter, setter, persisted, LongFormat.DECIMAL)

    fun withHexInt(name: String, getter: IntSupplier, setter: IntConsumer?, persisted: Boolean): WiredValues =
        withInt(name, getter, setter, persisted, LongFormat.HEXADECIMAL)

    fun withInt(name: String, getter: IntSupplier, persisted: Boolean, format: LongFormat): WiredValues =
        withInt(name, getter, null, persisted, format)

    fun withInt(name: String, getter: IntSupplier, persisted: Boolean): WiredValues =
        withInt(name, getter, null, persisted)

    fun withHexInt(name: String, getter: IntSupplier, persisted: Boolean): WiredValues =
        withHexInt(name, getter, null, persisted)

    fun withLong(
        name: String,
        getter: LongGetter,
        setter: LongSetter?,
        persisted: Boolean,
        format: LongFormat
    ): WiredValues =
        add(WiredLong(name, persisted, format, getter, setter))

    fun withLong(name: String, getter: LongGetter, setter: LongSetter?, persisted: Boolean): WiredValues =
        add(WiredLong(name, persisted, LongFormat.DECIMAL, getter, setter))

    fun withHexLong(name: String, getter: LongGetter, setter: LongSetter?, persisted: Boolean): WiredValues =
        add(WiredLong(name, persisted, LongFormat.HEXADECIMAL, getter, setter))

    fun withLong(name: String, getter: LongGetter, persisted: Boolean, format: LongFormat): WiredValues =
        withLong(name, getter, null, persisted, format)

    fun withLong(name: String, getter: LongGetter, persisted: Boolean): WiredValues =
        withLong(name, getter, null, persisted)

    fun withHexLong(name: String, getter: LongGetter, persisted: Boolean): WiredValues =
        withHexLong(name, getter, null, persisted)

    fun withDouble(name: String, getter: DoubleGetter, setter: DoubleSetter?, persisted: Boolean): WiredValues =
        add(WiredDouble(name, persisted, getter, setter))

    fun withDouble(name: String, getter: DoubleGetter, persisted: Boolean): WiredValues =
        withDouble(name, getter, null, persisted)

    fun build(): AutoCloseable =
        cfg.wire(values)
}
