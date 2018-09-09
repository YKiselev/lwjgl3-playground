package com.github.ykiselev.services.configuration;

import com.github.ykiselev.services.configuration.values.ConfigValue;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class RelativeConfig implements Config {

    private final Config delegate;

    private final String base;

    public RelativeConfig(Config delegate, String base) {
        this.delegate = requireNonNull(delegate);
        this.base = requireNonNull(base);
    }

    private String path(String path) {
        return base + "." + path;
    }

    @Override
    public <V extends ConfigValue> V getValue(String path, Class<V> clazz) {
        return delegate.getValue(path(path), clazz);
    }

    @Override
    public <V extends ConfigValue> V getOrCreateValue(String path, Class<V> clazz) {
        return delegate.getOrCreateValue(path(path), clazz);
    }

    @Override
    public <T> List<T> getList(String path, Class<T> clazz) {
        return delegate.getList(path(path), clazz);
    }

    @Override
    public boolean hasPath(String path) {
        return delegate.hasPath(path(path));
    }
}
