package com.github.ykiselev.io;

import com.github.ykiselev.assets.Resources;

import java.nio.channels.WritableByteChannel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface FileSystem extends Resources {

    /**
     * @param name   the file name that will be resolved relative to first writable folder.
     * @param append {@code true} to append to file or {@code false} to truncate file if it exists.
     * @return writable channel
     */
    WritableByteChannel openForWriting(String name, boolean append);

}
