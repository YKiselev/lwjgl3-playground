package com.github.ykiselev.playground.services.fs;

import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.spi.services.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Wrapper for {@link java.nio.file.FileSystem} created from zip archive
 */
public final class ArchiveFileSystem implements FileSystem {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final java.nio.file.FileSystem fs;

    public ArchiveFileSystem(java.nio.file.FileSystem fs) {
        this.fs = Objects.requireNonNull(fs);
    }

    @Override
    public void close() {
        Closeables.close(fs);
    }

    @Override
    public Optional<ReadableByteChannel> open(String resource) throws ResourceException {
        return Optional.ofNullable(resource)
                .map(name -> {
                    try {
                        return FileChannel.open(fs.getPath(resource), StandardOpenOption.READ);
                    } catch (IOException e) {
                        logger.error("Failed to open file!", e);
                        return null;
                    }
                });
    }

    @Override
    public Stream<ReadableByteChannel> openAll(String resource) throws ResourceException {
        return open(resource).stream();
    }

    @Override
    public FileChannel open(String name, OpenOption... options) {
        try {
            return FileChannel.open(fs.getPath(name), options);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public FileSystem mapArchive(String archiveName, boolean create, boolean readOnly) {
        return ArchiveFileSystem.create(fs.getPath(archiveName), create);
    }

    public static FileSystem create(Path path, boolean create) {
        final java.nio.file.FileSystem fs;
        try {
            fs = FileSystems.newFileSystem(path, Map.of("create", Boolean.toString(create)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new ArchiveFileSystem(fs);
    }
}
