package com.github.ykiselev.services.configuration;

import com.github.ykiselev.services.configuration.ConfigurationException.VariableNotFoundException;
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

    /**
     * Gets config variable. Requested variable should exists.
     *
     * @param path  the variable path
     * @param clazz the type variable expected to be of
     * @param <V>   type parameter
     * @return the found config variable
     * @throws ClassCastException        if variable type does not match
     * @throws VariableNotFoundException if there is no variable at specified path
     */
    <V extends ConfigValue> V getValue(String path, Class<V> clazz) throws ClassCastException, VariableNotFoundException;

    /**
     * Gets or creates variable. If variable exists and have the same type it is retured. If there is no variable at specified path - new variable is created.
     *
     * @param path  the variable path
     * @param clazz the type variable expected to be of
     * @param <V>   type parameter
     * @return the config variable
     * @throws ClassCastException if existing variable type does not match
     */
    <V extends ConfigValue> V getOrCreateValue(String path, Class<V> clazz) throws ClassCastException;

    <T> List<T> getList(String path, Class<T> clazz);

    /**
     * @param path the path to check
     * @return {@code true} if there is variable at the specified path.
     */
    boolean hasVariable(String path);

    /**
     * @param path the variable path
     * @return variable value
     * @throws VariableNotFoundException if there is no variable at specified path
     */
    default String getString(String path) {
        final ConfigValue value = getValue(path, ConfigValue.class);
        return value != null ? value.getString() : null;
    }

    default boolean getBoolean(String path) {
        final BooleanValue value = getValue(path, BooleanValue.class);
        return value != null && value.value();
    }

    default int getInt(String path) {
        return Math.toIntExact(getLong(path));
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

    default void set(String path, String value) {
        getOrCreateValue(path, StringValue.class).setString(value);
    }

    default void set(String path, boolean value) {
        getOrCreateValue(path, BooleanValue.class).value(value);
    }

    default void set(String path, int value) {
        set(path, (long) value);
    }

    default void set(String path, long value) {
        getOrCreateValue(path, LongValue.class).value(value);
    }

    default void set(String path, float value) {
        set(path, (double) value);
    }

    default void set(String path, double value) {
        getOrCreateValue(path, DoubleValue.class).value(value);
    }

    default List<String> getStringList(String path) {
        return getList(path, String.class);
    }
}
