package cob.github.ykiselev.lwjgl3.fs;

import com.github.ykiselev.io.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppFileSystem implements FileSystem {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Path home;

    public AppFileSystem(Path home) {
        this.home = requireNonNull(home);
    }

    @Override
    public WritableByteChannel open(String name, boolean append) {
        final Path path = home.resolve(name);
        logger.info("Opening file {}...", path);
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
}
