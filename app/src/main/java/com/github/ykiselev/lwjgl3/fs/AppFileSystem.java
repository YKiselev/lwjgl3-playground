package com.github.ykiselev.lwjgl3.fs;

import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.io.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppFileSystem implements FileSystem {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Path home;

    private final Collection<ResourceFolder> folders;

    public AppFileSystem(Path home, Collection<ResourceFolder> folders) {
        this.home = requireNonNull(home);
        this.folders = requireNonNull(folders);
    }

    private FileChannel open(String name, OpenOption... options) {
        final Path path = home.resolve(name);
        logger.info("Opening file {}...", path);
        try {
            return FileChannel.open(path, options);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to open " + path, e);
        }
    }

    @Override
    public WritableByteChannel openForWriting(String name, boolean append) {
        return open(
                name,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING
        );
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

    private ReadableByteChannel channel(String resource, URL url) {
        logger.debug("Resource {} resolved into {}", resource, url);
        try {
            return Channels.newChannel(
                    url.openStream()
            );
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }
}
