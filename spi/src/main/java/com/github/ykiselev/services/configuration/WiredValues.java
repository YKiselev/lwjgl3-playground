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
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.configuration.values.ConfigValue;
import com.github.ykiselev.services.configuration.values.Values;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Wired values map builder.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class WiredValues {

    private final PersistedConfiguration cfg;

    private final List<Map.Entry<String, ConfigValue>> values = new ArrayList<>();

    public WiredValues(PersistedConfiguration cfg) {
        this.cfg = requireNonNull(cfg);
    }

    private WiredValues add(String path, ConfigValue value) {
        values.add(new AbstractMap.SimpleImmutableEntry<>(path, value));
        return this;
    }

    public WiredValues withString(String path, Supplier<String> getter, Consumer<String> setter, boolean persisted) {
        return add(path, new Values.WiredString(persisted, getter, setter));
    }

    public WiredValues withString(String path, Supplier<String> getter, boolean persisted) {
        return withString(path, getter, null, persisted);
    }

    public WiredValues withBoolean(String path, BooleanSupplier getter, BooleanConsumer setter, boolean persisted) {
        return add(path, new Values.WiredBoolean(persisted, getter, setter));
    }

    public WiredValues withBoolean(String path, BooleanSupplier getter, boolean persisted) {
        return withBoolean(path, getter, null, persisted);
    }

    public WiredValues withInt(String path, IntSupplier getter, IntConsumer setter, boolean persisted) {
        return add(path, new Values.WiredLong(persisted, getter::getAsInt, v -> setter.accept(Math.toIntExact(v))));
    }

    public WiredValues withInt(String path, IntSupplier getter, boolean persisted) {
        return withInt(path, getter, null, persisted);
    }

    public WiredValues withLong(String path, LongSupplier getter, LongConsumer setter, boolean persisted) {
        return add(path, new Values.WiredLong(persisted, getter, setter));
    }

    public WiredValues withLong(String path, LongSupplier getter, boolean persisted) {
        return withLong(path, getter, null, persisted);
    }

    public WiredValues withDouble(String path, DoubleSupplier getter, DoubleConsumer setter, boolean persisted) {
        return add(path, new Values.WiredDouble(persisted, getter, setter));
    }

    public WiredValues withDouble(String path, DoubleSupplier getter, boolean persisted) {
        return withDouble(path, getter, null, persisted);
    }

    private Map<String, ConfigValue> toMap() {
        return values.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public AutoCloseable build() {
        return cfg.wire(toMap());
    }

}
