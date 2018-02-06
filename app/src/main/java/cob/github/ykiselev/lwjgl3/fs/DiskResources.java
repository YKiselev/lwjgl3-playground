package cob.github.ykiselev.lwjgl3.fs;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DiskResources implements ResourceFolder {

    private final Collection<Path> paths;

    public DiskResources(Collection<Path> paths) {
        this.paths = requireNonNull(paths);
    }

    @Override
    public Optional<URL> resolve(String resource) {
        return paths.stream()
                .map(p -> p.resolve(resource))
                .filter(p -> Files.exists(p) && Files.isRegularFile(p))
                .map(Path::toUri)
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        throw new UncheckedIOException(e);
                    }
                }).findFirst();
    }
}
