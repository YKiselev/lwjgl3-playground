package com.github.ykiselev.opengl.matrices;

import java.nio.FloatBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Vector {

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
}
