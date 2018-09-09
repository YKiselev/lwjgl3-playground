package com.github.ykiselev.services.configuration;

import com.github.ykiselev.common.BooleanConsumer;
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
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class WiredValues {

    private final List<Map.Entry<String, ConfigValue>> values = new ArrayList<>();

    private WiredValues add(String path, ConfigValue value) {
        values.add(new AbstractMap.SimpleImmutableEntry<>(path, value));
        return this;
    }

    public WiredValues with(String path, Supplier<String> getter, Consumer<String> setter) {
        return add(path, new Values.WiredString(getter, setter));
    }

    public WiredValues with(String path, BooleanSupplier getter, BooleanConsumer setter) {
        return add(path, new Values.WiredBoolean(getter, setter));
    }

    public WiredValues with(String path, LongSupplier getter, LongConsumer setter) {
        return add(path, new Values.WiredLong(getter, setter));
    }

    public WiredValues with(String path, DoubleSupplier getter, DoubleConsumer setter) {
        return add(path, new Values.WiredDouble(getter, setter));
    }

    public Map<String, ConfigValue> build() {
        return values.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
