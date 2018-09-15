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
    public boolean hasVariable(String path) {
        return delegate.hasVariable(path(path));
    }
}
