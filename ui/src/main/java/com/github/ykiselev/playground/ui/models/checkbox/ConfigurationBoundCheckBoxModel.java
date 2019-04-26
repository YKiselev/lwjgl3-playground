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

package com.github.ykiselev.playground.ui.models.checkbox;

import com.github.ykiselev.spi.services.configuration.Config;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConfigurationBoundCheckBoxModel implements CheckBoxModel {

    private final Config configuration;

    private final String key;

    public ConfigurationBoundCheckBoxModel(Config configuration, String key) {
        this.configuration = requireNonNull(configuration);
        this.key = requireNonNull(key);
    }

    @Override
    public boolean checked() {
        return configuration.getBoolean(key);
    }

    @Override
    public void checked(boolean value) {
        configuration.set(key, value);
    }
}
