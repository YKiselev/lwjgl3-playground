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

package com.github.ykiselev.common;

import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 05.04.2019
 */
public final class IntDimensions {

    private final int width;

    private final int height;

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public IntDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntDimensions that = (IntDimensions) o;
        return width == that.width &&
                height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return "IntDimensions{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
