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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class Vector3fTest {

    private void assertVectorEquals(float x, float y, float z, Vector3f v) {
        assertEquals(x, v.x, 0.001f);
        assertEquals(y, v.y, 0.001f);
        assertEquals(z, v.z, 0.001f);
    }

    @Test
    public void set() {
        final Vector3f v = new Vector3f(1, 2, 3);
        v.set(4, 5, 6);
        assertVectorEquals(4, 5, 6, v);
    }

    @Test
    public void normalize() {
        final Vector3f v = new Vector3f(1, 1, 1);
        v.normalize();
        assertVectorEquals(0.577f, 0.577f, 0.577f, v);
    }

    @Test
    public void squareLength() {
        assertEquals(
                3,
                new Vector3f(1, 1, 1)
                        .squareLength(),
                0.0001f
        );
    }

    @Test
    public void length() {
        assertEquals(
                1.732f,
                new Vector3f(1, 1, 1)
                        .length(),
                0.001f
        );
    }

    @Test
    public void dotProduct() {
        assertEquals(
                9,
                new Vector3f(1, 1, 1)
                        .dotProduct(new Vector3f(2, 3, 4)),
                0.001f
        );
    }

    @Test
    public void crossProduct() {
        final Vector3f v = new Vector3f();
        v.crossProduct(
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0)
        );
        assertVectorEquals(0, 0, 1, v);

        v.crossProduct(
                new Vector3f(-1, 0, 0),
                new Vector3f(0, -1, 0)
        );
        assertVectorEquals(0, 0, 1, v);

        v.crossProduct(
                new Vector3f(-1, 0, 0),
                new Vector3f(0, 1, 0)
        );
        assertVectorEquals(0, 0, -1, v);

        v.set(1, 0, 0);
        v.crossProduct(new Vector3f(0, 1, 0));
        assertVectorEquals(0, 0, 1, v);
    }

    @Test
    public void scale() {
        final Vector3f v = new Vector3f(1, 2, 3);
        v.scale(3);
        assertVectorEquals(3, 6, 9, v);
    }

    @Test
    public void addTwoArgs() {
        final Vector3f v = new Vector3f();
        v.add(
                new Vector3f(1, 2, 3),
                new Vector3f(4, 5, 6)
        );
        assertVectorEquals(5, 7, 9, v);
    }

    @Test
    public void add() {
        final Vector3f v = new Vector3f(1, 2, 3);
        v.add(new Vector3f(4, 5, 6));
        assertVectorEquals(5, 7, 9, v);
    }

    @Test
    public void subtractTwoArgs() {
        final Vector3f v = new Vector3f();
        v.subtract(
                new Vector3f(4, 7, 11),
                new Vector3f(1, 2, 3)
        );
        assertVectorEquals(3, 5, 8, v);
    }

    @Test
    public void subtract() {
        final Vector3f v = new Vector3f(4, 7, 11);
        v.subtract(new Vector3f(1, 2, 3));
        assertVectorEquals(3, 5, 8, v);
    }

    @Test
    public void multiplyTwoArgs() {
        final Vector3f v = new Vector3f();
        v.multiply(
                new Vector3f(1, 2, 3),
                new Vector3f(4, 5, 6)
        );
        assertVectorEquals(4, 10, 18, v);
    }

    @Test
    public void multiply() {
        final Vector3f v = new Vector3f(1, 2, 3);
        v.multiply(new Vector3f(4, 5, 6));
        assertVectorEquals(4, 10, 18, v);
    }

    @Test
    public void divideTwoArgs() {
        final Vector3f v = new Vector3f();
        v.divide(
                new Vector3f(2, 6, 9),
                new Vector3f(1, 2, 3)
        );
        assertVectorEquals(2, 3, 3, v);
    }

    @Test
    public void divide() {
        final Vector3f v = new Vector3f(2, 6, 9);
        v.divide(new Vector3f(1, 2, 3));
        assertVectorEquals(2, 3, 3, v);
    }

    @Test
    public void equals() {
        final Vector3f v = new Vector3f(1.0001f, 2.0001f, 3.0001f);
        assertTrue(
                v.equals(
                        new Vector3f(1.0002f, 2.0002f, 3.0000f),
                        0.0001f
                )
        );
        assertFalse(
                v.equals(
                        new Vector3f(1.0003f, 2.0002f, 3.0000f),
                        0.0001f
                )
        );
    }

    @Test
    public void isEmpty() {
        assertTrue(
                new Vector3f(0.0001f, 0.0001f, 0.0001f)
                        .isEmpty(0.0001f)
        );
    }
}