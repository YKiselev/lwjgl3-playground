package cob.github.ykiselev.lwjgl3.layers.ui.models.checkbox;

import cob.github.ykiselev.lwjgl3.config.PersistedConfiguration;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConfigurationBoundCheckBoxModel implements CheckBoxModel {

    private final PersistedConfiguration configuration;

    private final String key;

    private final ListenableCheckBoxModel delegate;

    public ConfigurationBoundCheckBoxModel(PersistedConfiguration configuration, String key) {
        this.configuration = configuration;
        this.key = key;
        this.delegate = new ListenableCheckBoxModel(this::onCheckBoxChanged);
        delegate.checked(
                configuration.root().getBoolean(key)
        );
    }

    private void onCheckBoxChanged(CheckBoxModel model) {
        configuration.set(key, model.checked());
    }

    @Override
    public boolean checked() {
        return delegate.checked();
    }

    @Override
    public void checked(boolean value) {
        delegate.checked(value);
    }
}
