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

package com.github.ykiselev.opengl.text;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Glyph {

    private final float s0, t0, s1, t1;

    private final int width;

    public float s0() {
        return s0;
    }

    public float t0() {
        return t0;
    }

    public float s1() {
        return s1;
    }

    public float t1() {
        return t1;
    }

    public int width() {
        return width;
    }

    public Glyph(float s0, float t0, float s1, float t1, int width) {
        this.s0 = s0;
        this.t0 = t0;
        this.s1 = s1;
        this.t1 = t1;
        this.width = width;
    }
}
