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

import com.github.ykiselev.spi.services.configuration.values.ConfigValue;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface PersistedConfiguration {

    /**
     * @return the configuration root.
     */
    Config root();

    /**
     * Wires specified config values to this configuration.
     *
     * @param values the config values to wire to this config.
     * @return the handle to the wired variable.
     */
    AutoCloseable wire(Collection<ConfigValue> values) throws ConfigurationException.VariableAlreadyExistsException;

    /**
     * Convenient method to wire many variables at once.
     *
     * @return the builder to wire many variables at once.
     */
    default WiredValues wire() {
        return new WiredValues(this);
    }

    Stream<ConfigValue> values();

    /**
     * Saves current configuration to file.
     *
     * @param name the file name (should be relative to writable directory).
     */
    void persist(String name);

    /**
     * Saves current config to file "app.conf".
     */
    default void persist() {
        persist("app.conf");
    }

    /**
     * Loads configuration from file.
     *
     * @param name the file name (relative to resource directory).
     */
    void load(String name);

    /**
     * Loads configuration from all files with such name found in resource folders.
     *
     * @param name the file name (relative to resource directory).
     */
    void loadAll(String name);
}
