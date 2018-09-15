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

package com.github.ykiselev.lwjgl3.services.config;

import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.configuration.Config;
import com.github.ykiselev.services.configuration.ConfigurationException;
import com.github.ykiselev.services.configuration.values.ConfigValue;
import com.github.ykiselev.services.configuration.values.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConfig implements PersistedConfiguration, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile Map<String, Object> config;

    private final Consumer<Map<String, Object>> writer;

    private final Config root = new Config() {

        @SuppressWarnings("unchecked")
        @Override
        public <V extends ConfigValue> V getValue(String path, Class<V> clazz) {
            final V result = clazz.cast(config.get(path));
            if (result == null) {
                throw new ConfigurationException.VariableNotFoundException(path);
            }
            return result;
        }

        @Override
        public <V extends ConfigValue> V getOrCreateValue(String path, Class<V> clazz) {
            final Object raw = config.get(path);
            if (raw != null) {
                return clazz.cast(raw);
            }
            final V result = Values.simpleValue(clazz);
            merge(Collections.singletonMap(path, result));
            return result;
        }

        @Override
        public <T> List<T> getList(String path, Class<T> clazz) {
            final Object raw = config.get(path);
            if (raw instanceof ConstantList) {
                return ((ConstantList) raw).toUniformList(clazz);
            } else if (raw == null) {
                throw new ConfigurationException.VariableNotFoundException(path);
            } else {
                throw new ClassCastException("Cannot cast " + raw.getClass().getName() + " to " + clazz.getName());
            }
        }

        @Override
        public boolean hasVariable(String path) {
            final Object raw = config.get(path);
            return raw instanceof ConfigValue || raw instanceof ConstantList;
        }
    };

    private static final VarHandle CH;

    static {
        try {
            CH = MethodHandles.lookup()
                    .findVarHandle(AppConfig.class, "config", Map.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public AppConfig(Supplier<Map<String, Object>> reader, Consumer<Map<String, Object>> writer) {
        this.config = requireNonNull(reader.get());
        this.writer = requireNonNull(writer);
    }

    public AppConfig(Services services) {
        this(new ConfigFromFile(services.resolve(FileSystem.class)),
                new ConfigToFile(services.resolve(FileSystem.class)));
    }

    @Override
    public Config root() {
        return root;
    }

    @Override
    public AutoCloseable wire(Map<String, ConfigValue> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Nothing to wire!");
        }
        final Map<String, Object> previous = merge(values);
        values.forEach((key, value) -> {
            final Object raw = previous.get(key);
            if (raw instanceof ConfigValue) {
                value.setString(((ConfigValue) raw).getString());
            }
        });
        final Set<String> keysToRemove = new HashSet<>(values.keySet());
        return () -> {
            final Map<String, ConfigValue> modified = unwire(keysToRemove);
            if (modified.size() != keysToRemove.size()) {
                logger.warn("Expected {} but got {}", keysToRemove, modified.keySet());
            }
            merge(modified);
        };
    }

    private Map<String, ConfigValue> unwire(Set<String> keys) {
        final Map<String, Object> current = this.config;
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
     * @param values the values to merge with current map
     * @return the previous map
     */
    private Map<String, Object> merge(Map<String, ConfigValue> values) {
        for (; ; ) {
            final Map<String, Object> before = this.config;
            final Map<String, Object> after = new HashMap<>(before);
            after.putAll(values);
            if (CH.compareAndSet(this, before, after)) {
                return before;
            }
        }
    }

    @Override
    public void close() {
        writer.accept(config);
    }
}
