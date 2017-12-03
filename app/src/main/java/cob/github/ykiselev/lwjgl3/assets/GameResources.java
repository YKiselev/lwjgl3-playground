package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.Resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameResources implements Resources {

    @Override
    public ReadableByteChannel open(URI resource) throws ResourceException {
        if (resource == null) {
            return null;
        }
        final URI resolved;
        if (!resource.isAbsolute()) {
            // todo check override places first, then classpath, then try raw resource uri
            resolved = Optional.<URI>empty().or(() -> resolveClassPathResource(resource))
                    .orElse(resource);
        } else {
            resolved = resource;
        }
        try {
            return FileChannel.open(Paths.get(resolved));
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    private Optional<URI> resolveClassPathResource(URI resource) {
        final String str = resource.toString();
        return Stream.of(str, "/" + str)
                .map(s -> getClass().getResource(s))
                .filter(Objects::nonNull)
                .findFirst()
                .map(this::toUri);
    }

    private URI toUri(URL url) throws ResourceException {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new ResourceException(e);
        }
    }
}
