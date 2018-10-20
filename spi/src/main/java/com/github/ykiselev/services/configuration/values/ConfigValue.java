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

package com.github.ykiselev.services.configuration.values;

import com.github.ykiselev.api.Named;

import static java.util.Objects.requireNonNull;

/**
 * Configuration value.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class ConfigValue implements Named {

    private final String name;

    private final boolean persisted;

    public final boolean isPersisted() {
        return persisted;
    }

    @Override
    public final String name() {
        return name;
    }

    protected ConfigValue(String name, boolean persisted) {
        this.name = requireNonNull(name);
        this.persisted = persisted;
    }

    public abstract void setString(String value);

    public abstract void fromObject(Object value);

    public abstract Object boxed();

    public boolean isReadOnly() {
        return false;
    }
}
