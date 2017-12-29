package com.github.ykiselev.io;

import com.github.ykiselev.common.Wrap;

import java.nio.ByteBuffer;

/**
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ReadableBytes {

    Wrap<ByteBuffer> read();
}
