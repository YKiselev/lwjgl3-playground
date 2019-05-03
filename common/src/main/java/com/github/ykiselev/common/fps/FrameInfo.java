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

    /**
     * @return total number of calls to {@link FrameInfo#add(double)}
     */
    public long totalFrames() {
        return totalFrames;
    }

    /**
     * @return the minimum frame time (in milliseconds) in current window
     */
    public double min() {
        return min;
    }

    /**
     * @return the maximum frame time (in milliseconds) in current window
     */
    public double max() {
        return max;
    }

    /**
     * Note: accuracy of this value depends on window size.
     *
     * @return average frame time (in milliseconds) across values of current window
     */
    public double avg() {
        return avg;
    }

    /**
     * @return frames per second derived from average frame time (1000/avg())
     * @see FrameInfo#avg
     */
    public double fps() {
        return avg != 0 ? 1000.0 / avg : 0;
    }

    /**
     * Size of window to aggregate frame times.
     *
     * @param windowSize the size of sliding window to collect frame times
     */
    public FrameInfo(int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size should be >= 1!");
        }
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
