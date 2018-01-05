package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Collection<Path> paths;

    public GameResources(Collection<Path> paths) {
        this.paths = requireNonNull(paths);
    }

    @Override
    public Optional<ReadableByteChannel> open(String resource) throws ResourceException {
        if (resource == null) {
            return Optional.empty();
        }
        return resolveFileResource(resource)
                .or(() -> resolveClassPathResource(resource))
                .map(url -> channel(resource, url));
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
