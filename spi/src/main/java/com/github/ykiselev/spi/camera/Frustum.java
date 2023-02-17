package com.github.ykiselev.spi.camera;

import com.github.ykiselev.opengl.matrices.Vector3f;

import java.nio.FloatBuffer;

public final class Frustum {

    private final Plane left = new Plane();
    private final Plane top = new Plane();
    private final Plane right = new Plane();
    private final Plane bottom = new Plane();
    private final Plane near = new Plane();
    private final Plane far = new Plane();

    /**
     * Extracts this frustum's planes from supplied projection matrix using Gribb/Hartmann method.
     * Pi is row vector of matrix.
     * Planes = P4(+|-)Pi where i=1,2,3
     *
     * @param m the matrix
     */
    public void setFromMatrix(FloatBuffer m) {
        // P4 + P1
        left.set(m.get(3) + m.get(0), m.get(7) + m.get(4), m.get(11) + m.get(8), m.get(15) + m.get(12));
        // P4 - P1
        right.set(m.get(3) - m.get(0), m.get(7) - m.get(4), m.get(11) - m.get(8), m.get(15) - m.get(12));
        // P4 + P2
        bottom.set(m.get(3) + m.get(1), m.get(7) + m.get(5), m.get(11) + m.get(9), m.get(15) + m.get(13));
        // P4 - P2
        top.set(m.get(3) - m.get(1), m.get(7) - m.get(5), m.get(11) - m.get(9), m.get(15) - m.get(13));
        // P4 + P3
        near.set(m.get(3) + m.get(2), m.get(7) + m.get(6), m.get(11) + m.get(10), m.get(15) + m.get(14));
        // P4 - P3
        far.set(m.get(3) - m.get(2), m.get(7) - m.get(6), m.get(11) - m.get(10), m.get(15) - m.get(14));
    }

    public boolean isInside(Vector3f p) {
        return intersects(p, 0f);
    }

    public boolean intersects(Vector3f p, float radius) {
        if (near.classify(p, radius) == Plane.Classification.OUTSIDE) {
            return false;
        }
        if (far.classify(p, radius) == Plane.Classification.OUTSIDE) {
            return false;
        }
        if (left.classify(p, radius) == Plane.Classification.OUTSIDE) {
            return false;
        }
        if (top.classify(p, radius) == Plane.Classification.OUTSIDE) {
            return false;
        }
        if (right.classify(p, radius) == Plane.Classification.OUTSIDE) {
            return false;
        }
        return bottom.classify(p, radius) != Plane.Classification.OUTSIDE;
    }

    @Override
    public String toString() {
        return "Frustum{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", near=" + near +
                ", far=" + far +
                '}';
    }
}
