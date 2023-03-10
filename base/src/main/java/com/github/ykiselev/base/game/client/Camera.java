package com.github.ykiselev.base.game.client;

import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.matrices.Vector3f;
import com.github.ykiselev.opengl.pools.Vector3fPool;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public final class Camera {

    private double yaw, pitch;

    private float x, y, z, dx, dy, rx, ry, zNear = 0.1f, zFar = 100f, fow = 90f;

    private final Vector3f direction = new Vector3f(), up = new Vector3f(), right = new Vector3f();

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void move(float delta) {
        buildVectors();
        x += dx * delta;
        y += dy * delta;
    }

    public void strafe(float delta) {
        buildVectors();
        x += rx * delta;
        y += ry * delta;
    }

    public void moveUp(float delta) {
        z += delta;
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
    }

    private void buildVectors() {
        try (var ms = MemoryStack.stackPush();
             var vectors = Vector3fPool.push()) {
            final FloatBuffer mat = ms.mallocFloat(16);

            buildRotation(mat);
            Matrix.inverse(mat, mat);

            direction.set(0, 0, -1);
            Matrix.multiply(mat, direction);
            up.set(0, 1, 0);
            Matrix.multiply(mat, up);
            right.set(1, 0, 0);
            Matrix.multiply(mat, right);

            Vector3f v = vectors.allocate();
            v.set(direction.x, direction.y, 0);
            v.normalize();
            dx = v.x;
            dy = v.y;

            v.set(right.x, right.y, 0);
            v.normalize();
            rx = v.x;
            ry = v.y;
        }
        direction.normalize();
        up.normalize();
        right.normalize();
    }

    private void buildRotation(FloatBuffer m) {
        Matrix.rotation(Math.toRadians(yaw - 90), 0, Math.toRadians(pitch), m);
    }

    public void apply(float ratio, FloatBuffer m) {
        Matrix.perspective((float) Math.toRadians(fow), ratio, zNear, zFar, m);

        try (MemoryStack ms = MemoryStack.stackPush()) {
            final FloatBuffer mat = ms.mallocFloat(16);

            buildRotation(mat);
            Matrix.multiply(m, mat, m);

            Matrix.identity(mat);
            Matrix.translate(mat, -x, -y, -z, mat);
            Matrix.multiply(m, mat, m);
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
                ", dx=" + dx +
                ", dy=" + dy +
                ", rx=" + rx +
                ", ry=" + ry +
                '}';
    }
}
