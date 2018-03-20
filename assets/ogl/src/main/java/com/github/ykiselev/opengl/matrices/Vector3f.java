package com.github.ykiselev.opengl.matrices;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Vector3f {

    public float x, y, z;

    private enum Ops {

        ADDITION {
            @Override
            float apply(float a, float b) {
                return a + b;
            }
        },
        SUBTRACTION {
            @Override
            float apply(float a, float b) {
                return a - b;
            }
        },
        MULTIPLICATION {
            @Override
            float apply(float a, float b) {
                return a * b;
            }
        },
        DIVISION {
            @Override
            float apply(float a, float b) {
                return a / b;
            }
        };

        abstract float apply(float a, float b);
    }

    public Vector3f() {
    }

    public Vector3f(Vector3f src) {
        this(src.x, src.y, src.z);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "Vector3f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    /**
     * Normalizes vector.
     */
    public void normalize() {
        final double length = length();
        double ool = 1.0;
        if (length != 0) {
            ool = 1.0 / length;
        }
        x *= ool;
        y *= ool;
        z *= ool;
    }

    /**
     * Calculates squared vector length.
     *
     * @return squared length of vector
     */
    public double squareLength() {
        return x * x + y * y + z * z;
    }

    /**
     * Calculates vector length.
     *
     * @return vector length
     */
    public double length() {
        return Math.sqrt(squareLength());
    }

    /**
     * Calculates Dot product between this vectors.
     *
     * @param b the second vector
     */
    public double dotProduct(Vector3f b) {
        return x * b.x + y * b.y + z * b.z;
    }

    /**
     * Calculates cross-product vector c = (a × b) that is perpendicular to both a and b, with a direction given by the right-hand rule.
     *
     * @param a the first vector
     * @param b the second vector
     */
    public void crossProduct(Vector3f a, Vector3f b) {
        set(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    /**
     * Calculates cross-product of this vector and another.
     *
     * @param b the second vector
     */
    public void crossProduct(Vector3f b) {
        crossProduct(this, b);
    }

    /**
     * Scales the vector.
     */
    public void scale(float scale) {
        x *= scale;
        y *= scale;
        z *= scale;
    }

    /**
     * Applies specified operation to supplied vectors.
     *
     * @param a the first vector
     * @param b the second vector
     */
    private void apply(Vector3f a, Vector3f b, Ops op) {
        x = op.apply(a.x, b.x);
        y = op.apply(a.y, b.y);
        z = op.apply(a.z, b.z);
    }

    /**
     * Adds one vector to another.
     *
     * @param a the first vector
     * @param b the second vector
     */
    public void add(Vector3f a, Vector3f b) {
        apply(a, b, Ops.ADDITION);
    }

    public void add(Vector3f b) {
        add(this, b);
    }

    /**
     * Subtracts one vector from another.
     *
     * @param a the first vector
     * @param b the second vector
     */
    public void subtract(Vector3f a, Vector3f b) {
        apply(a, b, Ops.SUBTRACTION);
    }

    public void subtract(Vector3f b) {
        subtract(this, b);
    }

    /**
     * Multiplies one vector by another.
     *
     * @param a the first vector
     * @param b the second vector
     */
    public void multiply(Vector3f a, Vector3f b) {
        apply(a, b, Ops.MULTIPLICATION);
    }

    public void multiply(Vector3f b) {
        multiply(this, b);
    }

    /**
     * Divides one vector by another.
     *
     * @param a the first vector
     * @param b the second vector
     */
    public void divide(Vector3f a, Vector3f b) {
        apply(a, b, Ops.DIVISION);
    }

    public void divide(Vector3f b) {
        divide(this, b);
    }

    /**
     * Checks if two vectors are equal (absolute difference in each components is less than delta).
     *
     * @param b     the second vector
     * @param delta the maximum difference between two numbers for which they are still considered equal.
     * @return the true if vectors are equal or false otherwise.
     */
    public boolean equals(Vector3f b, float delta) {
        return equals(x, b.x, delta)
                && equals(y, b.y, delta)
                && equals(z, b.z, delta);
    }

    public boolean isEmpty(float delta) {
        return equals(x, 0, delta)
                && equals(y, 0, delta)
                && equals(z, 0, delta);
    }

    private boolean equals(float a, float b, float delta) {
        return Math.abs(a - b) <= delta + delta * Math.abs(b);
    }
}
