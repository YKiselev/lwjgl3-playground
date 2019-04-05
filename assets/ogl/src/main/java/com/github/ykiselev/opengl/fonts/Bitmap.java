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

package com.github.ykiselev.opengl.fonts;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 07.04.2019
 */
public final class Bitmap<B> {

    private final int width;

    private final int height;

    private final B pixels;

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public B pixels() {
        return pixels;
    }

    public Bitmap(int width, int height, B pixels) {
        this.width = width;
        this.height = height;
        this.pixels = requireNonNull(pixels);
    }

    @Override
    public String toString() {
        return "Bitmap{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
