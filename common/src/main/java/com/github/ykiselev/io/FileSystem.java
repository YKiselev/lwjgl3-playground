package com.github.ykiselev.io;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface FileSystem {

    WritableByteChannel openForWriting(String name, boolean append);

    ReadableByteChannel openForReading(String name);
}
