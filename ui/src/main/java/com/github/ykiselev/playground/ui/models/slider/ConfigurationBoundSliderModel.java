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

package com.github.ykiselev.playground.ui.models.slider;

import com.github.ykiselev.services.configuration.Config;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConfigurationBoundSliderModel implements SliderModel {

    private final String key;

    private final Config configuration;

    private final SliderDefinition definition;

    public ConfigurationBoundSliderModel(SliderDefinition definition, Config configuration, String key) {
        this.key = requireNonNull(key);
        this.configuration = requireNonNull(configuration);
        this.definition = requireNonNull(definition);
    }

    @Override
    public SliderDefinition definition() {
        return definition;
    }

    @Override
    public int value() {
        return configuration.getInt(key);
    }

    @Override
    public void value(int value) {
        configuration.set(key, value);
    }

    @Override
    public void increase() {
        value(definition.increase(value()));
    }

    @Override
    public void decrease() {
        value(definition.decrease(value()));
    }
}
