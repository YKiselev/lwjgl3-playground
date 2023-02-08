package com.github.ykiselev.opengl.pools;

import com.github.ykiselev.common.memory.pool.ObjectPoolFrame;
import com.github.ykiselev.opengl.matrices.Vector3f;

public final class Vector3fPool {

    public static ObjectPoolFrame<Vector3f> push() {
        return ObjectPoolFrame.push(Vector3f.class, Vector3f::new);
    }
}
