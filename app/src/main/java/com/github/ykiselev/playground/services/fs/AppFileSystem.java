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

import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.spi.services.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppFileSystem implements FileSystem {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<ResourceFolder> folders;

    /**
     * @param folders the read-only resource folders sorted by priority.
     */
    public AppFileSystem(ResourceFolder... folders) {
        this.folders = List.of(folders);
    }

    private static Path toPath(URL url) {
        try {
            return Paths.get(url.toURI()).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new ResourceException("Unable to convert to path: " + url, e);
        }
    }

    private FileChannel open(URL resource, OpenOption... options) {
        final Path path = toPath(resource);
        logger.debug("Opening file channel {}...", path);
        ensureParentFoldersExists(path);
        try {
            return FileChannel.open(path, options);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to open " + path, e);
        }
    }

    private void ensureParentFoldersExists(Path path) {
        final Path parent = path.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static boolean hasOption(OpenOption[] options, OpenOption option) {
        for (OpenOption opt : options) {
            if (option.equals(opt)) {
                return true;
            }
        }
        return false;
    }

    private Optional<ResourceFolder> resolveResourceFolder(boolean writable) {
        final Predicate<ResourceFolder> predicate = writable
                ? ResourceFolder::isWritable
                : v -> true;
        return folders.stream()
                .filter(predicate)
                .findFirst();
    }

    @Override
    public FileChannel open(String name, OpenOption... options) {
        if (name == null) {
            return null;
        }
        final boolean hasWrite = hasOption(options, StandardOpenOption.WRITE);
        return resolveResourceFolder(hasWrite)
                .flatMap(f -> f.resolve(name, !hasWrite))
                .map(url -> open(url, options))
                .orElseThrow(() -> new IllegalStateException("Unable to open file channel!"));
    }

    @Override
    public FileSystem mapArchive(String archiveName, boolean create, boolean readOnly) {
        if (archiveName == null) {
            return null;
        }
        return resolveResourceFolder(!readOnly)
                .flatMap(f -> f.resolve(archiveName, readOnly))
                .map(url -> ArchiveFileSystem.create(toPath(url), create))
                .orElseThrow(() -> new IllegalStateException("Unable to map archive!"));
    }

    @Override
    public Optional<ReadableByteChannel> open(String resource) throws ResourceException {
        if (resource == null) {
            return Optional.empty();
        }
        return folders.stream()
                .map(f -> f.resolve(resource))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(url -> channel(resource, url))
                .findFirst();
    }

    @Override
    public Stream<ReadableByteChannel> openAll(String resource) throws ResourceException {
        if (resource == null) {
            return Stream.empty();
        }
        return folders.stream()
                .flatMap(f -> f.resolveAll(resource))
                .map(url -> channel(resource, url));
    }

    private ReadableByteChannel channel(String resource, URL url) {
        logger.debug("Resource {} resolved to {}", resource, url);
        try {
            return Channels.newChannel(
                    url.openStream()
            );
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public void close() {
        // no-op for now
    }
}
