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
        configuration.setInt(key, value);
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
