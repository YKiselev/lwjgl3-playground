package com.github.ykiselev.lwjgl3.layers.ui.models.slider;

import com.github.ykiselev.services.PersistedConfiguration;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConfigurationBoundSliderModel implements SliderModel {

    private final String key;

    private final PersistedConfiguration configuration;

    private final SliderDefinition definition;

    public ConfigurationBoundSliderModel(SliderDefinition definition, PersistedConfiguration configuration, String key) {
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
        return configuration.root().getInt(key);
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
