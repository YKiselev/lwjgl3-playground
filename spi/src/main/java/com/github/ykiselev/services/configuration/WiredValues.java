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

package com.github.ykiselev.services.configuration;

import com.github.ykiselev.common.BooleanConsumer;
import com.github.ykiselev.services.configuration.values.ConfigValue;
import com.github.ykiselev.services.configuration.values.LongFormat;
import com.github.ykiselev.services.configuration.values.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Wired values map builder.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class WiredValues {

    private final PersistedConfiguration cfg;

    private final List<ConfigValue> values = new ArrayList<>();

    public WiredValues(PersistedConfiguration cfg) {
        this.cfg = requireNonNull(cfg);
    }

    private WiredValues add(ConfigValue value) {
        values.add(value);
        return this;
    }

    public WiredValues withString(String name, Supplier<String> getter, Consumer<String> setter, boolean persisted) {
        return add(new Values.WiredString(name, persisted, getter, setter));
    }

    public WiredValues withString(String name, Supplier<String> getter, boolean persisted) {
        return withString(name, getter, null, persisted);
    }

    public WiredValues withBoolean(String name, BooleanSupplier getter, BooleanConsumer setter, boolean persisted) {
        return add(new Values.WiredBoolean(name, persisted, getter, setter));
    }

    public WiredValues withBoolean(String name, BooleanSupplier getter, boolean persisted) {
        return withBoolean(name, getter, null, persisted);
    }

    public WiredValues withInt(String name, IntSupplier getter, IntConsumer setter, boolean persisted, LongFormat format) {
        return add(new Values.WiredLong(name, persisted, format, () -> toLong(getter.getAsInt()), v -> setter.accept(toInt(v))));
    }

    public WiredValues withInt(String name, IntSupplier getter, IntConsumer setter, boolean persisted) {
        return add(new Values.WiredLong(name, persisted, LongFormat.DECIMAL, () -> toLong(getter.getAsInt()), v -> setter.accept(toInt(v))));
    }

    public WiredValues withHexInt(String name, IntSupplier getter, IntConsumer setter, boolean persisted) {
        return add(new Values.WiredLong(name, persisted, LongFormat.HEXADECIMAL, () -> toLong(getter.getAsInt()), v -> setter.accept(toInt(v))));
    }

    public WiredValues withInt(String name, IntSupplier getter, boolean persisted, LongFormat format) {
        return withInt(name, getter, null, persisted, format);
    }

    public WiredValues withInt(String name, IntSupplier getter, boolean persisted) {
        return withInt(name, getter, null, persisted);
    }

    public WiredValues withHexInt(String name, IntSupplier getter, boolean persisted) {
        return withHexInt(name, getter, null, persisted);
    }

    public WiredValues withLong(String name, LongSupplier getter, LongConsumer setter, boolean persisted, LongFormat format) {
        return add(new Values.WiredLong(name, persisted, format, getter, setter));
    }

    public WiredValues withLong(String name, LongSupplier getter, LongConsumer setter, boolean persisted) {
        return add(new Values.WiredLong(name, persisted, LongFormat.DECIMAL, getter, setter));
    }

    public WiredValues withHexLong(String name, LongSupplier getter, LongConsumer setter, boolean persisted) {
        return add(new Values.WiredLong(name, persisted, LongFormat.HEXADECIMAL, getter, setter));
    }

    public WiredValues withLong(String name, LongSupplier getter, boolean persisted, LongFormat format) {
        return withLong(name, getter, null, persisted, format);
    }

    public WiredValues withLong(String name, LongSupplier getter, boolean persisted) {
        return withLong(name, getter, null, persisted);
    }

    public WiredValues withHexLong(String name, LongSupplier getter, boolean persisted) {
        return withHexLong(name, getter, null, persisted);
    }

    public WiredValues withDouble(String name, DoubleSupplier getter, DoubleConsumer setter, boolean persisted) {
        return add(new Values.WiredDouble(name, persisted, getter, setter));
    }

    public WiredValues withDouble(String name, DoubleSupplier getter, boolean persisted) {
        return withDouble(name, getter, null, persisted);
    }

    public AutoCloseable build() {
        return cfg.wire(values);
    }

    private static long toLong(int value) {
        return value & 0x00000000ffffffffL;
    }

    private static int toInt(long value) {
        if ((int) value != value && (value & 0xffffffffL) != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int) value;
    }
}
