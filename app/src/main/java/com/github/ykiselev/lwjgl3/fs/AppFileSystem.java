package com.github.ykiselev.lwjgl3.fs;

import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.services.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppFileSystem implements FileSystem {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Collection<ResourceFolder> folders;

    /**
     * @param folders the read-only resource folders sorted by priority.
     */
    public AppFileSystem(ResourceFolder... folders) {
        this.folders = Arrays.asList(folders.clone());
    }

    private FileChannel open(URL resource, boolean append) {
        final Path path;
        try {
            path = Paths.get(resource.toURI()).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new ResourceException("Unable to convert to URI: " + resource, e);
        }
        logger.debug("Opening {}...", path);
        ensureParentFoldersExists(path);
        try {
            return FileChannel.open(
                    path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING
            );
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

    @Override
    public WritableByteChannel openForWriting(String name, boolean append) {
        if (name == null) {
            return null;
        }
        final ResourceFolder folder = folders.stream()
                .filter(ResourceFolder::isWritable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No writable folders!"));
        return folder.resolve(name, false)
                .map(url -> open(url, append))
                .orElseThrow(() -> new IllegalStateException("Unknown error!"));
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
}
