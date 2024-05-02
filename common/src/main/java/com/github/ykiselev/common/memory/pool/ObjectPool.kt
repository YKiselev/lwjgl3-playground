package com.github.ykiselev.common.memory.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Object pool. To use create helper class with thread local holding instance of such pool, e.g.
 * <pre>
 *     private static final ThreadLocal<ObjectPool<Vector3f>> TLS = ThreadLocal.withInitial(() ->
 *             new ObjectPool<>(Vector3f::new));
 * </pre>
 * and static method like
 * <pre>
 *     public static ObjectPoolFrame<Vector3f> push() {
 *         return TLS.get().push();
 *     }
 * </pre>
 * @param <T>
 */
public final class ObjectPool<T> {

    private final Supplier<T> newInstanceSupplier;

    private final List<ObjectPoolFrame<T>> frames = new ArrayList<>();

    private int index;

    public ObjectPool(Supplier<T> newInstanceSupplier) {
        this.newInstanceSupplier = requireNonNull(newInstanceSupplier);
    }

    public ObjectPoolFrame<T> push() {
        if (index == frames.size()) {
            frames.add(new ObjectPoolFrame<>(this));
        }
        return frames.get(index++);
    }

    void pop(ObjectPoolFrame<T> frame) {
        if (frames.get(index - 1) != frame) {
            throw new IllegalStateException("Wrong frame to pop: " + frame);
        }
        index--;
    }

    T newInstance() {
        return newInstanceSupplier.get();
    }
}
