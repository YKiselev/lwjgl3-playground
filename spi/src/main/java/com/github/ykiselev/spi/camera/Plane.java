package com.github.ykiselev.spi.camera;

import com.github.ykiselev.opengl.matrices.Vector3f;

public final class Plane {

    public enum Classification {
        INSIDE, OUTSIDE, ON_PLANE
    }

    private final Vector3f normal = new Vector3f();

    private float d;

    public void set(float a, float b, float c, float d) {
        normal.set(a, b, c);
        double ool = normal.normalize();
        this.d = (float) (d * ool);
    }

    public Classification classify(Vector3f p) {
        return classify(p, 0f);
    }

    public Classification classify(Vector3f p, float radius) {
        double r = normal.dotProduct(p) + d + radius;
        if (r < 0) {
            return Classification.OUTSIDE;
        } else if (r == 0) {
            return Classification.ON_PLANE;
        }
        return Classification.INSIDE;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "normal=" + normal +
                ", d=" + d +
                '}';
    }
}
