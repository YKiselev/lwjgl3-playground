package com.github.ykiselev.assets.formats.obj;

import java.nio.ByteBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface IndexedGeometry {

    ByteBuffer vertices();

    ByteBuffer indices();

    int mode();
}
