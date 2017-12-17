package com.github.ykiselev.opengl.matrices;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class MatrixTest {

    private final Matrix m = new Matrix();

    @After
    public void tearDown() {
        m.close();
    }

    private void assertEquals(Matrix actual, float... expected) {
        int i = 0;
        for (float v : expected) {
            final float v1 = actual.buffer().get(i);
            if (v != v1) {
                fail("Expected " + v + " got " + v1 + " at " + i);
            }
            i++;
        }
    }

    @Test
    public void shouldBeIdentity() {
        m.identity();
        assertEquals(
                m,
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    @Test
    public void shouldBeZero() {
        assertEquals(
                m,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0
        );
    }
}