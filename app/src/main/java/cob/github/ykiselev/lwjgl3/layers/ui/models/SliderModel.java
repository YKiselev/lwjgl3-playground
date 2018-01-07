package cob.github.ykiselev.lwjgl3.layers.ui.models;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SliderModel {

    private final int minValue;

    private final int maxValue;

    private final int step;

    private final SliderEventListener listener;

    private int value;

    public SliderModel(int minValue, int maxValue, int step, SliderEventListener listener) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
        this.listener = requireNonNull(listener);
    }

    public int minValue() {
        return minValue;
    }

    public int maxValue() {
        return maxValue;
    }

    public int step() {
        return step;
    }

    public int value() {
        return value;
    }

    public void value(int value) {
        if (value < minValue) {
            value = minValue;
        }
        if (value > maxValue) {
            value = maxValue;
        }
        if (value != this.value) {
            final int oldValue = this.value;
            this.value = value;
            listener.onSliderChanged(this, oldValue);
        }
    }

    public void increase() {
        value(value + step);
    }

    public void decrease() {
        value(value - step);
    }
}
