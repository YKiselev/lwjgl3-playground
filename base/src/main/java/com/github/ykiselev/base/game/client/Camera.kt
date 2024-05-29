package com.github.ykiselev.base.game.client

import com.github.ykiselev.opengl.matrices.*

class Camera {

    private val direction = Vector3f()
    private val up = Vector3f()
    private val right = Vector3f()
    private var yaw = 0.0
    private var pitch = 0.0
    private var x = 0f
    private var y = 0f
    private var z = 0f
    private var dx = 0f
    private var dy = 0f
    private var rx = 0f
    private var ry = 0f
    private val zNear = 0.1f
    private val zFar = 100f
    private val fow = 90f

    fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun move(delta: Float) {
        buildVectors()
        x += dx * delta
        y += dy * delta
    }

    fun strafe(delta: Float) {
        buildVectors()
        x += rx * delta
        y += ry * delta
    }

    fun moveUp(delta: Float) {
        z += delta
    }

    fun rotate(dx: Double, dy: Double) {
        yaw = limitAngle(yaw - dy)
        pitch = limitAngle(pitch + dx)
    }

    private fun buildVectors() {
        math {
            val mat = buildRotation().inverse()

            val newDirection = mat * vec3f(0f, 0f, -1f)
            //direction.set(0f, 0f, -1f)
            //multiply(mat, direction, direction)
            val newUp = mat * vec3f(0f, 1f, 0f)
            //up.set(0f, 1f, 0f)
            //multiply(mat, up, up)
            val newRight = mat * vec3f(1f, 0f, 0f)
            //right.set(1f, 0f, 0f)
            //multiply(mat, right, right)

            val v1 = vec3f(newDirection.x, newDirection.y, 0f).normalize()
            //val v1 = Vector3f(direction.x, direction.y, 0f).normalize()
            //v.normalize()
            dx = v1.x
            dy = v1.y

            val v2 = vec3f(newRight.x, newRight.y, 0f).normalize()
            //v.normalize()
            rx = v2.x
            ry = v2.y

            direction.set(newDirection.normalize())
            up.set(newUp.normalize())
            right.set(newRight.normalize())
        }
    }

    private fun MathArena.buildRotation(): Matrix =
        rotation(Math.toRadians(yaw - 90), 0.0, Math.toRadians(pitch))

    fun apply(ratio: Float, m: Matrix) {
        math {
            val p = perspective(Math.toRadians(fow.toDouble()).toFloat(), ratio, zNear, zFar)//, m!!)
            val pr = p * buildRotation()
            m.set(pr * identity().translate(-x, -y, -z))
        }
    }

    override fun toString(): String {
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
                '}'
    }

    companion object {
        private fun limitAngle(value: Double): Double {
            if (value > 360) {
                return value - 360
            }
            if (value < -360) {
                return value + 360
            }
            return value
        }
    }
}
