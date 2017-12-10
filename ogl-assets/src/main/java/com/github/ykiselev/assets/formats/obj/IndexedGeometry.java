package com.github.ykiselev.assets.formats.obj;

import java.nio.ByteBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface IndexedGeometry extends AutoCloseable {

    ByteBuffer vertices();

    ByteBuffer indices();

    int mode();

    @Override
    void close();
}
