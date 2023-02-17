package com.github.ykiselev.opengl.pools;

import com.github.ykiselev.common.memory.pool.ObjectPool;
import com.github.ykiselev.common.memory.pool.ObjectPoolFrame;
import com.github.ykiselev.opengl.matrices.Vector3f;

public final class Vector3fPool {

    private static final ThreadLocal<ObjectPool<Vector3f>> TLS = ThreadLocal.withInitial(() ->
            new ObjectPool<>(Vector3f::new));

    public static ObjectPoolFrame<Vector3f> push() {
        return TLS.get().push();
    }
}
