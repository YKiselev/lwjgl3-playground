package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.configuration.Config;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.config.ValueChangingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConfig implements PersistedConfiguration, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Services services;

    private volatile Map<String, ConfigValue> config;

    private static final VarHandle CH;

    static {
        try {
            CH = MethodHandles.lookup()
                    .in(AppConfig.class)
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
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setBoolean(String path, boolean value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setInt(String path, int value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setLong(String path, long value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setFloat(String path, float value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setDouble(String path, double value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setString(String path, String value) {
        final Object oldValue = getValue(path);
        if (Objects.equals(oldValue, value)) {
            logger.debug("Skipping setting \"{}\" to the same value \"{}\"...", path, value);
        } else {
            logger.debug("Setting \"{}\" to \"{}\"", path, value);
            final ValueChangingEvent result = services.resolve(Events.class).fire(
                    new ValueChangingEvent(path, oldValue, value)
            );
//            if (result != null) {
//                updateConfig(path, ConfigValueFactory.fromAnyRef(value));
//            }
        }
    }

//    private void updateConfig(String path, ConfigValue value) {
//        for (; ; ) {
//            final Config before = config;
//            final Config after = before.withValue(path, value);
//            if (CH.compareAndSet(this, before, after)) {
//                break;
//            }
//        }
//    }

    @Override
    public void close() throws Exception {
        new ConfigToFile(config, services.resolve(FileSystem.class)).run();
    }

    private Object getValue(String path) {
        throw new UnsupportedOperationException();
//        final Map<String, ConfigValue> cfg = this.config;
//        return this.config.hasPath(path) ? this.config.getValue(path).unwrapped() : null;
    }

}
