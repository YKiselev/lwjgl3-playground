package com.github.ykiselev.lwjgl3.fs;

import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ResourceFolder {

    default Optional<URL> resolve(String resource) {
        return resolve(resource, true);
    }

    Optional<URL> resolve(String resource, boolean shouldExist);

    Stream<URL> resolveAll(String resource);

    default boolean isWritable() {
        return false;
    }
}
