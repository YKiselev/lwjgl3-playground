package com.github.ykiselev.io;

import java.nio.ByteBuffer;

/**
 * todo resource freeing semantics will be different for different implementations!
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ByteChannelAsByteBuffer {

    ByteBuffer read();
}
