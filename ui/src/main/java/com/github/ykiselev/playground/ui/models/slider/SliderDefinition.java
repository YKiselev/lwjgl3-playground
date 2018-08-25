package com.github.ykiselev.playground.ui.models.slider;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SliderDefinition {

    private final int minValue;

    private final int maxValue;

    private final int step;

    public int minValue() {
        return minValue;
    }

    public int maxValue() {
        return maxValue;
    }

    public int step() {
        return step;
    }

    public int range() {
        return (maxValue - minValue) / step;
    }

    public SliderDefinition(int minValue, int maxValue, int step) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
    }

    private int refine(int value) {
        final int rem = value % step;
        if (rem != 0) {
            value -= rem;
        }
        if (value < minValue) {
            value = minValue;
        }
        if (value > maxValue) {
            value = maxValue;
        }
        return value;
    }

    public int increase(int value) {
        return refine(value + step);
    }

    public int decrease(int value) {
        return refine(value - step);
    }

}
