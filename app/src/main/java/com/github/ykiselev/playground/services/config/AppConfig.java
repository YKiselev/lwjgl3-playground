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

package com.github.ykiselev.playground.services.config;

import com.github.ykiselev.common.cow.CopyOnModify;
import com.github.ykiselev.spi.services.FileSystem;
import com.github.ykiselev.spi.services.configuration.Config;
import com.github.ykiselev.spi.services.configuration.ConfigurationException;
import com.github.ykiselev.spi.services.configuration.ConfigurationException.VariableAlreadyExistsException;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.configuration.values.ConfigValue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConfig implements PersistedConfiguration, AutoCloseable {

    private final CopyOnModify<Map<String, ConfigValue>> config;

    private final FileConfig fileConfig;

    private final Predicate<Object> varFilter = obj ->
            obj instanceof ConfigValue;

    private final Config root = new Config() {

        @Override
        public <V extends ConfigValue> V getValue(String path, Class<V> clazz) {
            final V result = clazz.cast(getRawValue(path));
            if (result == null) {
                throw new ConfigurationException.VariableNotFoundException(path);
            }
            return result;
        }

        @Override
        public boolean hasVariable(String path) {
            return varFilter.test(getRawValue(path));
        }
    };

    AppConfig(FileConfig fileConfig) {
        this.fileConfig = requireNonNull(fileConfig);
        this.config = new CopyOnModify<>(Collections.emptyMap());
        loadAll("app.conf");
    }

    public AppConfig(FileSystem fileSystem) {
        this(new AppFileConfig(fileSystem));
    }

    @Override
    public Stream<ConfigValue> values() {
        return config.value()
                .values()
                .stream()
                .filter(varFilter);
    }

    @Override
    public Config root() {
        return root;
    }

    private Object getRawValue(String path) {
        return config.value().get(path);
    }

    @Override
    public AutoCloseable wire(Collection<ConfigValue> values) throws VariableAlreadyExistsException {
        final Map<String, ConfigValue> toWire = toMap(values);
        if (toWire.isEmpty()) {
            throw new IllegalArgumentException("Nothing to wire!");
        }
        config.modify(before -> {
            final Map<String, ConfigValue> after = new HashMap<>(before);
            toWire.forEach((k, newValue) -> {
                if (after.containsKey(k)) {
                    throw new VariableAlreadyExistsException(k);
                }
                after.put(k, newValue);
                final Object rawValue = fileConfig.getValue(k);
                if (rawValue != null) {
                    newValue.fromObject(rawValue);
                }
            });
            return after;
        });
        final Set<String> keysToRemove = Set.of(toWire.keySet().toArray(new String[0]));
        return () -> config.modify(before -> {
            final Map<String, ConfigValue> result = new HashMap<>(before);
            result.keySet().removeAll(keysToRemove);
            return result;
        });
    }

    private Map<String, ConfigValue> toMap(Collection<ConfigValue> values) {
        return values.stream().collect(Collectors.toMap(
                ConfigValue::name,
                Function.identity()
        ));
    }

    @Override
    public void persist(String name) {
        final Map<String, Object> map = new HashMap<>();
        config.value()
                .values()
                .stream()
                .filter(ConfigValue::isPersisted)
                .forEach(v -> map.put(v.name(), v.boxed()));
        fileConfig.persist(name, map);
    }

    private void applyFileValues() {
        config.modify(before -> {
            final Map<String, ConfigValue> after = new HashMap<>(before);
            after.forEach((k, v) -> {
                final Object rawValue = fileConfig.getValue(k);
                if (rawValue != null) {
                    v.fromObject(rawValue);
                }
            });
            return after;
        });
    }

    @Override
    public void load(String name) {
        fileConfig.load(name);
        applyFileValues();
    }

    @Override
    public void loadAll(String name) {
        fileConfig.loadAll(name);
        applyFileValues();
    }

    @Override
    public void close() {
        // todo
        //writer.accept(config.value());
    }
}
