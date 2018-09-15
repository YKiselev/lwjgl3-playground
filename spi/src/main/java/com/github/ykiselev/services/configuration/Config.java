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

    /**
     * Returns list of elements of specified type.
     *
     * @param path  the variable path.
     * @param clazz the class of list element.
     * @param <T>   the type parameter.
     * @return list of elements of specified type.
     * @throws ClassCastException        if variable exists but is not a list or it's element type cannot be cast to {@code clazz}.
     * @throws VariableNotFoundException if variable does not exists.
     */
    <T> List<T> getList(String path, Class<T> clazz) throws ClassCastException, VariableNotFoundException;

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
     * @throws VariableNotFoundException if there is no variable at specified path
     */
    default String getString(String path) throws ClassCastException, VariableNotFoundException {
        return getValue(path, ConfigValue.class).getString();
    }

    /**
     * Returns value of boolean variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws VariableNotFoundException if there is no variable at specified path
     */
    default boolean getBoolean(String path) throws ClassCastException, VariableNotFoundException {
        return getValue(path, BooleanValue.class).value();
    }

    /**
     * Returns value of int variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws VariableNotFoundException if there is no variable at specified path
     * @throws ArithmeticException       if value overflows an int
     */
    default int getInt(String path) throws ClassCastException, VariableNotFoundException, ArithmeticException {
        return Math.toIntExact(getLong(path));
    }

    /**
     * Returns value of long variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws VariableNotFoundException if there is no variable at specified path
     */
    default long getLong(String path) throws ClassCastException, VariableNotFoundException {
        return getValue(path, LongValue.class).value();
    }

    /**
     * Returns value of float variable at specified path.
     *
     * @param path the variable path
     * @return variable value
     * @throws ClassCastException        if variable type does not match
     * @throws VariableNotFoundException if there is no variable at specified path
     * @throws ArithmeticException       if value overflows a float
     */
    default float getFloat(String path) throws ClassCastException, VariableNotFoundException, ArithmeticException {
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
     * @throws VariableNotFoundException if there is no variable at specified path
     */
    default double getDouble(String path) throws ClassCastException, VariableNotFoundException {
        return getValue(path, DoubleValue.class).value();
    }

    /**
     * Sets value of string variable at specified path (or creates new if does not exists).
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException if variable type does not match
     */
    default void set(String path, String value) throws ClassCastException {
        getOrCreateValue(path, StringValue.class).setString(value);
    }

    /**
     * Sets value of boolean variable at specified path (or creates new if does not exists).
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException if variable type does not match
     */
    default void set(String path, boolean value) throws ClassCastException {
        getOrCreateValue(path, BooleanValue.class).value(value);
    }

    /**
     * Sets value of int variable at specified path (or creates new if does not exists).
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException if variable type does not match
     */
    default void set(String path, int value) throws ClassCastException {
        set(path, (long) value);
    }

    /**
     * Sets value of long variable at specified path (or creates new if does not exists).
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException if variable type does not match
     */
    default void set(String path, long value) throws ClassCastException {
        getOrCreateValue(path, LongValue.class).value(value);
    }

    /**
     * Sets value of double variable at specified path (or creates new if does not exists).
     *
     * @param path  the variable path
     * @param value the value to set
     * @throws ClassCastException if variable type does not match
     */
    default void set(String path, double value) throws ClassCastException {
        getOrCreateValue(path, DoubleValue.class).value(value);
    }

    /**
     * Gets value of string list variable at specified path.
     *
     * @param path the variable path
     * @throws ClassCastException if variable is not a list or element type cannot be cast to {@link String} class.
     * @see Config#getList(java.lang.String, java.lang.Class)
     */
    default List<String> getStringList(String path) throws ClassCastException {
        return getList(path, String.class);
    }
}
