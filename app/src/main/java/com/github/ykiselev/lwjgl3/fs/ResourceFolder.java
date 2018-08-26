package com.github.ykiselev.lwjgl3.fs;

import java.net.URL;
import java.util.Optional;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ResourceFolder {

    default Optional<URL> resolve(String resource) {
        return resolve(resource, true);
    }

    Optional<URL> resolve(String resource, boolean shouldExist);

    default boolean isWritable() {
        return false;
    }
}
