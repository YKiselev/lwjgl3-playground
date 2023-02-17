package com.github.ykiselev.spi.camera;

import com.github.ykiselev.opengl.matrices.Vector3f;

public final class BoundingSphere {

    private final Vector3f center = new Vector3f();

    private float radius;

    public void set(float x, float y, float z, float radius) {
        center.set(x, y, z);
        this.radius = radius;
    }

    public boolean intersects(Frustum frustum) {
        return frustum.intersects(center, radius);
    }
}
