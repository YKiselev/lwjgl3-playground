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

package com.github.ykiselev.lwjgl3.services.fs;

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
