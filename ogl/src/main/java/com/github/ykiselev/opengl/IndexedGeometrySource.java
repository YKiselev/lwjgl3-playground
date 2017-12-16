package com.github.ykiselev.opengl;

import java.nio.ByteBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface IndexedGeometrySource extends AutoCloseable {

    ByteBuffer vertices();

    ByteBuffer indices();

    int mode();

    @Override
    void close();
}
