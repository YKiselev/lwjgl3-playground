package com.github.ykiselev.playground.ui.models.checkbox;

import com.github.ykiselev.services.configuration.Config;

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
