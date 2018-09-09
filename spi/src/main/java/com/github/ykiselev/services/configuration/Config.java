package com.github.ykiselev.services.configuration;

import com.github.ykiselev.services.configuration.values.BooleanValue;
import com.github.ykiselev.services.configuration.values.ConfigValue;
import com.github.ykiselev.services.configuration.values.DoubleValue;
import com.github.ykiselev.services.configuration.values.LongValue;
import com.github.ykiselev.services.configuration.values.StringValue;

import java.util.List;

/**
 * Mutable configuration.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Config {

    <V extends ConfigValue> V getValue(String path, Class<V> clazz);

    <V extends ConfigValue> V getOrCreateValue(String path, Class<V> clazz);

    <T> List<T> getList(String path, Class<T> clazz);

    boolean hasPath(String path);

    default String getString(String path) {
        final ConfigValue value = getValue(path, ConfigValue.class);
        return value != null ? value.getString() : null;
    }

    default boolean getBoolean(String path) {
        final BooleanValue value = getValue(path, BooleanValue.class);
        return value != null && value.value();
    }

    default int getInt(String path) {
        final long raw = getLong(path);
        if (raw < Integer.MIN_VALUE || raw > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Value " + raw + " can not be represented as int!");
        }
        return (int) raw;
    }

    default long getLong(String path) {
        final LongValue value = getValue(path, LongValue.class);
        return value != null ? value.value() : 0L;
    }

    default float getFloat(String path) {
        final double raw = getDouble(path);
        if (raw < Float.MIN_VALUE && raw > Float.MAX_VALUE) {
            throw new IllegalArgumentException("Value " + raw + " can not be represented as float!");
        }
        return (float) raw;
    }

    default double getDouble(String path) {
        final DoubleValue value = getValue(path, DoubleValue.class);
        return value != null ? value.value() : 0;
    }

    default void setString(String path, String value) {
        getOrCreateValue(path, StringValue.class).setString(value);
    }

    default void setBoolean(String path, boolean value) {
        getOrCreateValue(path, BooleanValue.class).value(value);
    }

    default void setInt(String path, int value) {
        setLong(path, value);
    }

    default void setLong(String path, long value) {
        getOrCreateValue(path, LongValue.class).value(value);
    }

    default void setFloat(String path, float value) {
        setDouble(path, value);
    }

    default void setDouble(String path, double value) {
        getOrCreateValue(path, DoubleValue.class).value(value);
    }

    default List<String> getStringList(String path) {
        return getList(path, String.class);
    }
}
