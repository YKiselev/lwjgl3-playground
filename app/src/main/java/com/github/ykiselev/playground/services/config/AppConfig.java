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

import com.github.ykiselev.cow.CopyOnModify;
import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.configuration.Config;
import com.github.ykiselev.services.configuration.ConfigurationException;
import com.github.ykiselev.services.configuration.PersistedConfiguration;
import com.github.ykiselev.services.configuration.values.ConfigValue;
import com.github.ykiselev.services.configuration.values.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConfig implements PersistedConfiguration, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CopyOnModify<Map<String, Object>> config;

    private final Consumer<Map<String, Object>> writer;

    private final Predicate<Object> varFilter = obj ->
            obj instanceof ConfigValue;

    private final Config root = new Config() {

        @SuppressWarnings("unchecked")
        @Override
        public <V extends ConfigValue> V getValue(String path, Class<V> clazz) {
            final V result = clazz.cast(getRawValue(path));
            if (result == null) {
                throw new ConfigurationException.VariableNotFoundException(path);
            }
            return result;
        }

        @Override
        public <V extends ConfigValue> V getOrCreateValue(String path, Class<V> clazz) {
            final Object raw = getRawValue(path);
            if (raw != null) {
                return clazz.cast(raw);
            }
            final V result = Values.simpleValue(path, clazz);
            merge(Collections.singletonMap(path, result));
            return result;
        }

        @Override
        public boolean hasVariable(String path) {
            return varFilter.test(getRawValue(path));
        }

    };

    public AppConfig(Supplier<Map<String, Object>> reader, Consumer<Map<String, Object>> writer) {
        this.config = new CopyOnModify<>(reader.get());
        this.writer = requireNonNull(writer);
    }

    public AppConfig(Services services) {
        this(new ConfigFromFile(services.resolve(FileSystem.class)),
                new ConfigToFile(services.resolve(FileSystem.class)));
    }

    @Override
    public Stream<ConfigValue> values() {
        return config.value()
                .entrySet()
                .stream()
                .filter(e -> varFilter.test(e.getValue()))
                .map(e -> (ConfigValue) e.getValue());
    }

    @Override
    public Config root() {
        return root;
    }

    private Object getRawValue(String path) {
        return config.value().get(path);
    }

    @Override
    public AutoCloseable wire(Collection<ConfigValue> values) {
        final Map<String, ConfigValue> toWire = toMap(values);
        if (toWire.isEmpty()) {
            throw new IllegalArgumentException("Nothing to wire!");
        }
        final Map<String, Object> previous = merge(toWire);
        toWire.forEach((key, value) -> {
            final Object raw = previous.get(key);
            if (raw instanceof ConfigValue) {
                value.setString(((ConfigValue) raw).getString());
            }
        });
        final Set<String> keysToRemove = new HashSet<>(toWire.keySet());
        return () -> {
            final Map<String, ConfigValue> modified = unwire(keysToRemove);
            if (modified.size() != keysToRemove.size()) {
                logger.warn("Expected {} but got {}", keysToRemove, modified.keySet());
            }
            merge(modified);
        };
    }

    private Map<String, ConfigValue> toMap(Collection<ConfigValue> values) {
        return values.stream().collect(Collectors.toMap(
                ConfigValue::name,
                Function.identity()
        ));
    }

    /**
     * For each supplied key extracts wired value from current map, creates simple (non-wired) version of that value and
     * puts that simple value in result map.
     * Note: resulting map is not a full copy of current map, it contains entries only for supplied {@code keys}!
     *
     * @param keys the keys to unwire
     * @return the result map with simple values
     */
    private Map<String, ConfigValue> unwire(Set<String> keys) {
        final Map<String, Object> current = config.value();
        final Map<String, ConfigValue> result = new HashMap<>();
        for (String key : keys) {
            final Object raw = current.get(key);
            if (raw instanceof ConfigValue) {
                result.put(key, Values.toSimpleValue((ConfigValue) raw));
            }
        }
        return result;
    }

    /**
     * Replaces current map with new version which is a result of merge of two maps (current and {@code value}).
     *
     * @param values the values to merge with current map
     * @return the previous map
     */
    private Map<String, Object> merge(Map<String, ConfigValue> values) {
        return config.modify(before -> {
            final Map<String, Object> after = new HashMap<>(before);
            after.putAll(values);
            return after;
        });
    }

    @Override
    public void close() {
        writer.accept(config.value());
    }
}
