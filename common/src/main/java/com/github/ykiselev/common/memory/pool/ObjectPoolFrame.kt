package com.github.ykiselev.common.memory.pool;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class ObjectPoolFrame<T> implements AutoCloseable {

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

    @Override
    public void close() {
        index = 0;
        owner.pop(this);
    }
}
