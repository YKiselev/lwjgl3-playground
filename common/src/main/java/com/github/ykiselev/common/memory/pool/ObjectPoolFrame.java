package com.github.ykiselev.common.memory.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class ObjectPoolFrame<T> implements AutoCloseable {

    private static final ThreadLocal<Map<Class<?>, ObjectPool<?>>> TLS = ThreadLocal.withInitial(HashMap::new);

    private final ObjectPool<T> owner;

    private final List<T> pool = new ArrayList<>();

    private int index;

    int size() {
        return pool.size();
    }

    int index() {
        return index;
    }

    ObjectPoolFrame(ObjectPool<T> owner) {
        this.owner = requireNonNull(owner);
    }

    public T allocate() {
        if (index == pool.size()) {
            pool.add(owner.newInstance());
        }
        return pool.get(index++);
    }

    public static <T> ObjectPoolFrame<T> push(Class<T> clazz, Supplier<T> supplier) {
        return (ObjectPoolFrame<T>) TLS.get().computeIfAbsent(clazz, k -> new ObjectPool<>(supplier)).push();
    }

    /**
     * For unit testing usage only
     */
    static void clear() {
        TLS.get().clear();
    }

    @Override
    public void close() {
        index = 0;
        owner.pop(this);
    }

    static class ObjectPool<T> {

        private final Supplier<T> supplier;

        private final List<ObjectPoolFrame<T>> frames = new ArrayList<>();

        private int index;

        ObjectPool(Supplier<T> supplier) {
            this.supplier = requireNonNull(supplier);
        }

        ObjectPoolFrame<T> push() {
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
            return supplier.get();
        }
    }
}
