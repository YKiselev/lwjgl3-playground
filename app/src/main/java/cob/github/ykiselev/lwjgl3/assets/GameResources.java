package cob.github.ykiselev.lwjgl3.assets;

import com.github.ykiselev.assets.ResourceException;
import com.github.ykiselev.assets.Resources;

import java.io.IOException;
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
    public ReadableByteChannel open(String resource) throws ResourceException {
        if (resource == null) {
            return null;
        }
        // todo check override places first, then classpath, then try raw resource uri
        final URL resolved = Optional.<URL>empty()
                .or(() -> resolveClassPathResource(resource))
                .orElseThrow(() -> new ResourceException("Resource not found: " + resource));
        try {
            return FileChannel.open(Paths.get(resolved.toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new ResourceException(e);
        }
    }

    private Optional<URL> resolveClassPathResource(String resource) {
        return Stream.of(resource, "/" + resource)
                .map(s -> getClass().getResource(s))
                .filter(Objects::nonNull)
                .findFirst();
    }

}
