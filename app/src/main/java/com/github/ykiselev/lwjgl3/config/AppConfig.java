package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.configuration.Config;
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

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConfig implements PersistedConfiguration, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Services services;

    private volatile Map<String, Object> config;

    private final Config root = new Config() {

        @Override
        public <V extends ConfigValue> V getValue(String path, Class<V> clazz) {
            return clazz.cast(config.get(path));
        }

        @Override
        public <V extends ConfigValue> V getOrCreateValue(String path, Class<V> clazz) {
            final V result = Values.create(clazz);
            merge(Collections.singletonMap(path, result));
            return result;
        }

        @Override
        public <T> List<T> getList(String path, Class<T> clazz) {
            final Object raw = config.get(path);
            if (raw instanceof Values.ConstantList) {
                return ((Values.ConstantList) raw).toUniformList(clazz);
            }
            return null;
        }

        @Override
        public boolean hasPath(String path) {
            return config.containsKey(path);
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

    public AppConfig(Services services) {
        this.services = requireNonNull(services);
        this.config = new ConfigFromFile(services.resolve(FileSystem.class)).get();
    }

    @Override
    public Config root() {
        return root;
    }

    @Override
    public AutoCloseable wire(Map<String, ConfigValue> values) {
        merge(values);
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

    private void merge(Map<String, ConfigValue> values) {
        for (; ; ) {
            final Map<String, Object> before = this.config;
            final Map<String, Object> after = new HashMap<>(before);
            after.putAll(values);
            if (CH.compareAndSet(this, before, after)) {
                break;
            }
        }
    }

    @Override
    public void close() {
        new ConfigToFile(config, services.resolve(FileSystem.class)).run();
    }
}
