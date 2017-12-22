package com.github.ykiselev.opengl.matrices;

import java.nio.FloatBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Vector {

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

    /**
     * Normalizes vector.
     * This method advances output vector position but left unchanged input vector position (if input and output vectors are not the same).
     *
     * @param vector the source vector
     * @param result the output vector
     */
    public static void normalize(FloatBuffer vector, FloatBuffer result) {
        final double length = length(vector);
        double ool = 1.0;
        if (length != 0) {
            ool = 1.0 / length;
        }
        final int pos = vector.position();
        final float x = vector.get(pos);
        final float y = vector.get(pos + 1);
        final float z = vector.get(pos + 2);
        result.put((float) (ool * x))
                .put((float) (ool * y))
                .put((float) (ool * z));
    }

    /**
     * Calculates squared vector length.
     * This method doesn't change vector's position.
     *
     * @param vector the vector
     * @return squared length of vector
     */
    public static double squareLength(FloatBuffer vector) {
        final int pos = vector.position();
        final float x = vector.get(pos);
        final float y = vector.get(pos + 1);
        final float z = vector.get(pos + 2);
        return x * x + y * y + z * z;
    }

    /**
     * Converts vector to string representation. May be used for debugging.
     * This method doesn't change vector's position.
     *
     * @param vector the vector
     * @return string representation of vector
     */
    public static String toString(FloatBuffer vector) {
        final int pos = vector.position();
        return "{x=" + vector.get(pos) +
                ", y=" + vector.get(pos + 1) +
                ", z=" + vector.get(pos + 2) +
                "}";
    }

    /**
     * Calculates vector length.
     * This method doesn't change vector's position.
     *
     * @param vector the vector
     * @return vector length
     */
    public static double length(FloatBuffer vector) {
        return Math.sqrt(squareLength(vector));
    }

    /**
     * Calculates Dot product between vectors a and b.
     * Doesn't change vector's positions.
     *
     * @param a the first vector
     * @param b the second vector
     */
    public static double dotProduct(FloatBuffer a, FloatBuffer b) {
        final int ap = a.position();
        final int bp = b.position();
        return a.get(ap) * b.get(bp) + a.get(ap + 1) * b.get(bp + 1) + a.get(ap + 2) * b.get(bp + 2);
    }

    /**
     * Calculates cross-product of two vectors.
     * Doesn't change positions of vectors a and b (if they are not equal to result vector).
     *
     * @param a      the first vector
     * @param b      the second vector
     * @param result the resulting vector
     */
    public static void crossProduct(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
        final int ap = a.position();
        final int bp = b.position();
        final float x = a.get(ap + 1) * b.get(bp + 2) - a.get(ap + 2) * b.get(bp + 1);
        final float y = a.get(ap + 2) * b.get(bp) - a.get(ap) * b.get(bp + 2);
        final float z = a.get(ap) * b.get(bp + 1) - a.get(ap + 1) * b.get(bp);
        result.put(x)
                .put(y)
                .put(z);
    }

    /**
     * Scales the vector.
     * Doesn't change positions of vectors a and b (if they are not equal to result vector).
     *
     * @param a      the first vector
     * @param result the resulting vector
     */
    public static void scale(FloatBuffer a, float scale, FloatBuffer result) {
        final int ap = a.position();
        final float x = scale * a.get(ap);
        final float y = scale * a.get(ap + 1);
        final float z = scale * a.get(ap + 2);
        result.put(x)
                .put(y)
                .put(z);
    }

    /**
     * Applies specified operation to supplied vectors.
     * Doesn't change positions of vectors a and b (if they are not equal to result vector).
     *
     * @param a      the first vector
     * @param b      the second vector
     * @param result the resulting vector
     */
    private static void apply(FloatBuffer a, FloatBuffer b, Ops op, FloatBuffer result) {
        final int ap = a.position();
        final int bp = b.position();
        final float x = op.apply(a.get(ap), b.get(bp));
        final float y = op.apply(a.get(ap + 1), b.get(bp + 1));
        final float z = op.apply(a.get(ap + 2), b.get(bp + 2));
        result.put(x)
                .put(y)
                .put(z);
    }

    /**
     * Adds one vector to another.
     * Doesn't change positions of vectors a and b (if they are not equal to result vector).
     *
     * @param a      the first vector
     * @param b      the second vector
     * @param result the resulting vector
     */
    public static void add(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
        apply(a, b, Ops.ADDITION, result);
    }

    /**
     * Subtracts one vector from another.
     * Doesn't change positions of vectors a and b (if they are not equal to result vector).
     *
     * @param a      the first vector
     * @param b      the second vector
     * @param result the resulting vector
     */
    public static void subtract(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
        apply(a, b, Ops.SUBTRACTION, result);
    }

    /**
     * Multiplies one vector by another.
     * Doesn't change positions of vectors a and b (if they are not equal to result vector).
     *
     * @param a      the first vector
     * @param b      the second vector
     * @param result the resulting vector
     */
    public static void multiply(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
        apply(a, b, Ops.MULTIPLICATION, result);
    }

    /**
     * Divides one vector by another.
     * Doesn't change positions of vectors a and b (if they are not equal to result vector).
     *
     * @param a      the first vector
     * @param b      the second vector
     * @param result the resulting vector
     */
    public static void divide(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
        apply(a, b, Ops.DIVISION, result);
    }

    /**
     * Checks if two vectors are equal (absolute difference in each components is less than delta).
     *
     * @param a     the first vector
     * @param b     the second vector
     * @param delta the maximum difference between two numbers for which they are still considered equal.
     * @return the true if vectors are equal or false otherwise.
     */
    public static boolean equals(FloatBuffer a, FloatBuffer b, float delta) {
        final int ap = a.position();
        final int bp = b.position();
        return equals(a.get(ap), b.get(bp), delta)
                && equals(a.get(ap + 1), b.get(bp + 1), delta)
                && equals(a.get(ap + 2), b.get(bp + 2), delta);
    }

    private static boolean equals(float a, float b, float delta) {
        return Math.abs(a - b) <= delta + delta * Math.abs(b);
    }
}
