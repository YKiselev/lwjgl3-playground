package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.configuration.Config;
import com.github.ykiselev.services.configuration.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return ensureValueExists(path, clazz);
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
                    //.in(AppConfig.class)
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

    private <V extends ConfigValue> V ensureValueExists(String path, Class<V> clazz) {
        V result = null;
        for (; ; ) {
            final Map<String, Object> before = this.config;
            final Object existing = before.get(path);
            if (clazz.isInstance(existing)) {
                result = clazz.cast(existing);
                break;
            }
            final Map<String, Object> after = new HashMap<>(before);
            if (result == null) {
                result = Values.create(clazz);
            }
            after.put(path, result);
            if (CH.compareAndSet(this, before, after)) {
                break;
            }
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        new ConfigToFile(config, services.resolve(FileSystem.class)).run();
    }
}
