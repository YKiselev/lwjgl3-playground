package com.github.ykiselev.spi.camera;

import com.github.ykiselev.opengl.matrices.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Plane {

    public enum Classification {
        INSIDE, OUTSIDE, ON_PLANE
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            logger.debug("{} with radius {} is outside (by {}) of {}", p, radius, r, this);
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
