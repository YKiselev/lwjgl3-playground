package com.github.ykiselev.opengl.models;

import com.github.ykiselev.opengl.Bindable;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ObjModel implements Bindable, AutoCloseable {

    @Override
    public void bind() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void unbind() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int id() {
        return 0;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("not implemented");
    }
}
