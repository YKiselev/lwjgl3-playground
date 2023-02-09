package com.github.ykiselev.spi.camera;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaneTest {
    @Test
    void shouldClassify() {
        var p = new Plane();
        // yz plane translated by 0.5 in x direction
        p.set(1, 0, 0, -0.5);

        assertEquals(Plane.Classification.INSIDE, p.classify(1, 0.5f, 0.5f));
        assertEquals(Plane.Classification.ON_PLANE, p.classify(0.5f, 0.5f, 0.5f));
        assertEquals(Plane.Classification.OUTSIDE, p.classify(0.3f, 0.5f, 0.5f));
    }
}