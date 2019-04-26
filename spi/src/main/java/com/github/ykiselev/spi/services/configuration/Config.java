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

package com.github.ykiselev.spi.services.configuration;

import com.github.ykiselev.spi.services.configuration.values.Values;
import com.github.ykiselev.spi.services.configuration.values.ConfigValue;

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
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    <V extends ConfigValue> V getValue(String path, Class<V> clazz) throws ClassCastException, ConfigurationException.VariableNotFoundException;

    /**
     * Checks if there is any variable at specified path.
     *
     * @param path the path to check
     * @return {@code true} if there is variable at the specified path.
     */
    boolean hasVariable(String path);

    /**
     * Returns value of string variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    default String getString(String path) throws ClassCastException, ConfigurationException.VariableNotFoundException {
        return getValue(path, ConfigValue.class).toString();
    }

    /**
     * Returns value of boolean variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    default boolean getBoolean(String path) throws ClassCastException, ConfigurationException.VariableNotFoundException {
        return getValue(path, Values.WiredBoolean.class).value();
    }

    /**
     * Returns value of int variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     * @throws ArithmeticException       if value overflows an int
     */
    default int getInt(String path) throws ClassCastException, ConfigurationException.VariableNotFoundException, ArithmeticException {
        return Math.toIntExact(getLong(path));
    }

    /**
     * Returns value of long variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    default long getLong(String path) throws ClassCastException, ConfigurationException.VariableNotFoundException {
        return getValue(path, Values.WiredLong.class).value();
    }

    /**
     * Returns value of float variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     * @throws ArithmeticException       if value overflows a float
     */
    default float getFloat(String path) throws ClassCastException, ConfigurationException.VariableNotFoundException, ArithmeticException {
        final double raw = getDouble(path);
        if (raw < Float.MIN_VALUE && raw > Float.MAX_VALUE) {
            throw new ArithmeticException("Value " + raw + " cannot be represented as float!");
        }
        return (float) raw;
    }

    /**
     * Returns value of double variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    default double getDouble(String path) throws ClassCastException, ConfigurationException.VariableNotFoundException {
        return getValue(path, Values.WiredDouble.class).value();
    }

    /**
     * Sets value of string variable at specified path.
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    default void set(String path, String value) throws ClassCastException, ConfigurationException.VariableNotFoundException {
        getValue(path, Values.WiredString.class).setString(value);
    }

    /**
     * Sets value of boolean variable at specified path.
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    default void set(String path, boolean value) throws ClassCastException, ConfigurationException.VariableNotFoundException {
        getValue(path, Values.WiredBoolean.class).value(value);
    }

    /**
     * Sets value of int variable at specified path.
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    default void set(String path, int value) throws ClassCastException, ConfigurationException.VariableNotFoundException {
        set(path, (long) value);
    }

    /**
     * Sets value of long variable at specified path.
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    default void set(String path, long value) throws ClassCastException, ConfigurationException.VariableNotFoundException {
        getValue(path, Values.WiredLong.class).value(value);
    }

    /**
     * Sets value of double variable at specified path.
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException        if variable type does not match
     * @throws ConfigurationException.VariableNotFoundException if there is no variable at specified path
     */
    default void set(String path, double value) throws ClassCastException, ConfigurationException.VariableNotFoundException {
        getValue(path, Values.WiredDouble.class).value(value);
    }
}
