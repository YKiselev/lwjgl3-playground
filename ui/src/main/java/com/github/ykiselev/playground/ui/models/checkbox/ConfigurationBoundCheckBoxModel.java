package com.github.ykiselev.playground.ui.models.checkbox;

import com.github.ykiselev.services.PersistedConfiguration;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConfigurationBoundCheckBoxModel implements CheckBoxModel {

    private final PersistedConfiguration configuration;

    private final String key;

    public ConfigurationBoundCheckBoxModel(PersistedConfiguration configuration, String key) {
        this.configuration = configuration;
        this.key = key;
    }

    @Override
    public boolean checked() {
        return configuration.root().getBoolean(key);
    }

    @Override
    public void checked(boolean value) {
        configuration.set(key, value);
    }
}
