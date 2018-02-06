package com.github.ykiselev.io;

import com.github.ykiselev.assets.Resources;

import java.nio.channels.WritableByteChannel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface FileSystem extends Resources {

    WritableByteChannel openForWriting(String name, boolean append);

}
