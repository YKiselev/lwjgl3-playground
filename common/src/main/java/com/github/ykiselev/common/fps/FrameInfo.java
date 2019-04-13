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

package com.github.ykiselev.common.fps;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class FrameInfo {

    private long totalFrames;

    private double[] window;

    private int index;

    private int count;

    private double min, max, avg;

    public int window() {
        return window.length;
    }

    public long totalFrames() {
        return totalFrames;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public double avg() {
        return avg;
    }

    public double fps() {
        return avg != 0 ? 1000.0 / avg : 0;
    }

    /**
     * Size ofwindow to aggregate frame times.
     *
     * @param windowSize the size of sliding window to collect frame times
     */
    public FrameInfo(int windowSize) {
        this.window = new double[windowSize];
    }

    /**
     * @param frameTime last frame time in milliseconds
     */
    public void add(double frameTime) {
        totalFrames++;
        window[index++] = frameTime;
        if (index >= window.length) {
            index = 0;
        }
        if (count < window.length) {
            count++;
        }
        calculate();
    }

    private void calculate() {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        avg = 0;
        double sum = 0;
        for (int i = 0; i < count; i++) {
            final double v = window[i];
            if (v < min) {
                min = v;
            }
            if (v > max) {
                max = v;
            }
            sum += v;
        }
        avg = sum / window.length;
    }
}
