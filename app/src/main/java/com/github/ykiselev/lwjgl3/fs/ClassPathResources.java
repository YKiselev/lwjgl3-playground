package com.github.ykiselev.lwjgl3.fs;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        return Stream.of(resource)
                .map(loader::getResource)
                .filter(Objects::nonNull)
                .findFirst();
    }

    @Override
    public Stream<URL> resolveAll(String resource) {
        if (resource == null) {
            return Stream.empty();
        }
        return all(resource);
    }

    private Stream<URL> all(String name) {
        try {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            loader.getResources(name).asIterator(),
                            Spliterator.ORDERED
                    ),
                    false
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
