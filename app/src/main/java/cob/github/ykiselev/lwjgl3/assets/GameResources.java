package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.Resources;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameResources implements Resources {

    private final Collection<Path> paths;

    public GameResources(Collection<Path> paths) {
        this.paths = requireNonNull(paths);
    }

    @Override
    public ReadableByteChannel open(String resource) throws ResourceException {
        if (resource == null) {
            return null;
        }
        final URL resolved = resolveFileResource(resource)
                .or(() -> resolveClassPathResource(resource))
                .orElseThrow(() -> new ResourceException("Resource not found: " + resource));
        try {
            return Channels.newChannel(
                    resolved.openStream()
            );
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    private Optional<URL> resolveFileResource(String resource) {
        return paths.stream().map(p -> p.resolve(resource))
                .map(Path::toFile)
                .filter(f -> f.exists() && f.isFile())
                .map(File::toURI)
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        throw new UncheckedIOException(e);
                    }
                }).findFirst();
    }

    private Optional<URL> resolveClassPathResource(String resource) {
        return Stream.of(resource, "/" + resource)
                .map(s -> getClass().getResource(s))
                .filter(Objects::nonNull)
                .findFirst();
    }

}
