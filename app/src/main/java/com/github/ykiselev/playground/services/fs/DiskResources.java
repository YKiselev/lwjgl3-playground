/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.playground.services.fs;

import com.github.ykiselev.lazy.Lazy;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DiskResources implements ResourceFolder {

    private final Collection<Path> paths;

    private final BooleanSupplier writable;

    public DiskResources(Path... paths) {
        this(Arrays.asList(paths.clone()));
    }

    public DiskResources(Collection<Path> paths) {
        this.paths = requireNonNull(paths);
        this.writable = Lazy.sync(() ->
                this.paths.stream()
                        .anyMatch(Files::isWritable)
        );
    }

    private Stream<URL> find(String resource, boolean shouldExist) {
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
                });
    }

    @Override
    public Optional<URL> resolve(String resource, boolean shouldExist) {
        return find(resource, shouldExist)
                .findFirst();
    }

    @Override
    public Stream<URL> resolveAll(String resource) {
        return find(resource, true);
    }

    @Override
    public boolean isWritable() {
        return writable.getAsBoolean();
    }
}
