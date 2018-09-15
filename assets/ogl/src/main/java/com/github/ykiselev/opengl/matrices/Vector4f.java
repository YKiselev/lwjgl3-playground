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

package com.github.ykiselev.opengl.matrices;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Vector4f {

    public float x, y, z, w;

    public Vector4f() {
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Checks if two vectors are equal (absolute difference in each components is less than delta).
     *
     * @param b     the second vector
     * @param delta the maximum difference between two numbers for which they are still considered equal.
     * @return the true if vectors are equal or false otherwise.
     */
    public boolean equals(Vector4f b, float delta) {
        return equals(x, b.x, delta)
                && equals(y, b.y, delta)
                && equals(z, b.z, delta)
                && equals(w, b.w, delta);
    }

    private boolean equals(float a, float b, float delta) {
        return Math.abs(a - b) <= delta + delta * Math.abs(b);
    }

    @Override
    public String toString() {
        return "Vector4f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }
}
