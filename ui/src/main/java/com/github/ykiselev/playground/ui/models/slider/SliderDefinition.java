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
