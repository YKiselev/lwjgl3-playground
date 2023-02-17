package com.github.ykiselev.spi.camera;

import com.github.ykiselev.opengl.matrices.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaneTest {
    @Test
    void shouldClassify() {
        var p = new Plane();
        // yz plane translated by 0.5 in x direction
        p.set(1, 0, 0, -0.5f);

        assertEquals(Plane.Classification.INSIDE, p.classify(new Vector3f(1, 0.5f, 0.5f)));
        assertEquals(Plane.Classification.ON_PLANE, p.classify(new Vector3f(0.5f, 0.5f, 0.5f)));
        assertEquals(Plane.Classification.OUTSIDE, p.classify(new Vector3f(0.3f, 0.5f, 0.5f)));
    }
}