package com.github.ykiselev.lwjgl3.layers.ui.models.slider;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ListenableSliderModel implements SliderModel {

    private final SliderDefinition definition;

    private final SliderEventListener listener;

    private int value;

    public ListenableSliderModel(SliderDefinition definition, SliderEventListener listener) {
        this.definition = requireNonNull(definition);
        this.listener = requireNonNull(listener);
    }

    @Override
    public SliderDefinition definition() {
        return definition;
    }

    @Override
    public int value() {
        return value;
    }

    @Override
    public void value(int value) {
        value = definition.refine(value);
        if (value != this.value) {
            final int oldValue = this.value;
            this.value = value;
            listener.onSliderChanged(this, oldValue);
        }
    }

    @Override
    public void increase() {
        value(definition.increase(value));
    }

    @Override
    public void decrease() {
        value(definition.decrease(value));
    }
}
