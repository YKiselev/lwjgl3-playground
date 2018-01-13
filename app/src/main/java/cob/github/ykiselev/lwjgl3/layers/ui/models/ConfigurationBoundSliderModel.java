package cob.github.ykiselev.lwjgl3.layers.ui.models;

import cob.github.ykiselev.lwjgl3.config.PersistedConfiguration;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConfigurationBoundSliderModel implements SliderModel {

    private final String key;

    private final PersistedConfiguration configuration;

    private final ListenableSliderModel delegate;

    private void listener(SliderModel model, int oldValue) {
        configuration.set(key, model.value());
    }

    public ConfigurationBoundSliderModel(SliderDefinition definition, PersistedConfiguration configuration, String key) {
        this.key = requireNonNull(key);
        this.configuration = requireNonNull(configuration);
        this.delegate = new ListenableSliderModel(definition, this::listener);
        delegate.value(
                configuration.root().getInt(key)
        );
    }

    @Override
    public SliderDefinition definition() {
        return delegate.definition();
    }

    @Override
    public int value() {
        return delegate.value();
    }

    @Override
    public void value(int value) {
        delegate.value(value);
    }

    @Override
    public void increase() {
        delegate.increase();
    }

    @Override
    public void decrease() {
        delegate.decrease();
    }
}
