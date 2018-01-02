package com.github.ykiselev.io;

import java.nio.channels.WritableByteChannel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface FileSystem {

    WritableByteChannel open(String name, boolean append);
}
