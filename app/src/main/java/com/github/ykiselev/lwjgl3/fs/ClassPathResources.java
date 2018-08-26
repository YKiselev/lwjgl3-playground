package com.github.ykiselev.lwjgl3.fs;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ClassPathResources implements ResourceFolder {

    private final ClassLoader loader;

    public ClassPathResources(ClassLoader loader) {
        this.loader = requireNonNull(loader);
    }

    @Override
    public Optional<URL> resolve(String resource, boolean shouldExist) {
        return Stream.of(resource, "/" + resource)
                .map(loader::getResource)
                .filter(Objects::nonNull)
                .findFirst();
    }
}
