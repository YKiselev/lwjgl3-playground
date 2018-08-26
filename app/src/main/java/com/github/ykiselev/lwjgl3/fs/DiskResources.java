package com.github.ykiselev.lwjgl3.fs;

import com.github.ykiselev.lazy.Lazy;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DiskResources implements ResourceFolder {

    private final Collection<Path> paths;

    private final BooleanSupplier writable;

    public DiskResources(Collection<Path> paths) {
        this.paths = requireNonNull(paths);
        this.writable = Lazy.sync(() ->
                this.paths.stream()
                        .anyMatch(Files::isWritable)
        );
    }

    @Override
    public Optional<URL> resolve(String resource, boolean shouldExist) {
        final Predicate<Path> preFilter;
        final Predicate<Path> resFilter;
        if (shouldExist) {
            preFilter = p -> true;
            resFilter = p -> Files.exists(p);
        } else {
            preFilter = Files::isWritable;
            resFilter = v -> true;
        }
        return paths.stream()
                .filter(preFilter)
                .map(p -> p.resolve(resource))
                .filter(resFilter)
                .map(Path::toUri)
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        throw new UncheckedIOException(e);
                    }
                }).findFirst();
    }

    @Override
    public boolean isWritable() {
        return writable.getAsBoolean();
    }
}
