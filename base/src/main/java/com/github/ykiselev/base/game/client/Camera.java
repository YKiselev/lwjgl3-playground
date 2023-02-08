package com.github.ykiselev.base.game.client;

import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.matrices.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;

public final class Camera {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private double yaw, pitch;

    private float x, y, z;

    private final Vector3f direction = new Vector3f(), up = new Vector3f(), right = new Vector3f();

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void move(float delta) {
        buildVectors();
        x += direction.x * delta;
        y += direction.y * delta;
        logger.info("{}", this);
    }

    public void strafe(float delta) {
        buildVectors();
        x += right.x * delta;
        y += right.y * delta;
        logger.info("{}", this);
    }

    private static double limitAngle(double value) {
        if (value > 360) {
            return value - 360;
        }
        if (value < -360) {
            return value + 360;
        }
        return value;
    }

    public void rotate(double dx, double dy) {
        yaw = limitAngle(yaw - dy);
        pitch = limitAngle(pitch + dx);
        buildVectors();
        logger.info("{}", this);
    }

    private void buildVectors() {
        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer mat = ms.mallocFloat(16);

            buildRotation(mat);

            direction.set(0, 1, 0);
            Matrix.multiply(mat, direction);
            //up.set(0, 0, 1);
            //Matrix.multiply(mat, up);
            right.set(1, 0, 0);
            Matrix.multiply(mat, right);
            //right.crossProduct(direction, up);
            up.set(0, 0, 1);
            direction.crossProduct(up, right);
        }
        direction.normalize();
        up.normalize();
        right.normalize();
    }

    private void buildRotation(FloatBuffer m) {
        Matrix.rotation(Math.toRadians(yaw - 90), 0, Math.toRadians(pitch), m);
    }

    public void apply(float ratio, FloatBuffer m) {
        Matrix.perspective(
                (float) Math.toRadians(90),
                ratio,
                0.1f,
                150,
                m
        );

        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer mat = ms.mallocFloat(16);

            buildRotation(mat);
            Matrix.multiply(m, mat, m);

            Matrix.identity(mat);
            Matrix.translate(mat, -x, -y, -z, mat);
            Matrix.multiply(m, mat, m);

            Matrix.rotation(Math.toRadians(90), 0, 0, mat);
            //Matrix.multiply(m, mat, m);
        }
    }

    @Override
    public String toString() {
        return "Camera{" +
                "yaw=" + yaw +
                ", pitch=" + pitch +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", direction=" + direction +
                ", up=" + up +
                ", right=" + right +
                '}';
    }
}
