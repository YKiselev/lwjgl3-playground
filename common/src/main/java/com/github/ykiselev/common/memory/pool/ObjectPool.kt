package com.github.ykiselev.common.memory.pool

import java.util.*
import java.util.function.Supplier

/**
 * Object pool. To use create helper class with thread local holding instance of such pool, e.g.
 * <pre>
 * private static final ThreadLocal<ObjectPool<Vector3f>> TLS = ThreadLocal.withInitial(() ->
 *      new ObjectPool<>(Vector3f::new));
 * </pre>
 * and static method like
 * <pre>
 * public static ObjectPoolFrame<Vector3f> push() {
 *      return TLS.get().push();
 * }
 * </pre>
 */
class ObjectPool<T>(private val supplier: Supplier<T>) {

    private val frames: MutableList<ObjectPoolFrame<T>> = ArrayList()

    private var index = 0

    fun push(): ObjectPoolFrame<T> {
        if (index == frames.size) {
            frames.add(ObjectPoolFrame(this))
        }
        return frames[index++]
    }

    fun pop(frame: ObjectPoolFrame<T>) {
        check(frames[index - 1] == frame) { "Wrong frame to pop: $frame" }
        index--
    }

    fun newInstance(): T {
        return supplier.get()
    }
}
