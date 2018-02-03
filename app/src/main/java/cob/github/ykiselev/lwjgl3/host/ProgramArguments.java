package cob.github.ykiselev.lwjgl3.host;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ProgramArguments {

    private final String[] args;

    public ProgramArguments(String[] args) {
        this.args = args.clone();
    }

    /**
     * Searches arguments for key=value pair.
     *
     * @param key the key name
     * @return the value or {@code null}
     */
    private Optional<String> value(String key) {
        for (String arg : args) {
            if (arg != null) {
                final int k = arg.indexOf("=");
                if (k >= 0) {
                    if (key.equals(arg.substring(0, k))) {
                        return Optional.of(
                                arg.substring(k + 1)
                        );
                    }
                }
            }
        }
        return Optional.empty();
    }

    private boolean hasSwitch(String key) {
        for (String arg : args) {
            if (arg != null && arg.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public Collection<Path> assetPaths() {
        return value("asset.paths").stream()
                .flatMap(v -> Arrays.stream(v.split(",")))
                .map(Paths::get)
                .filter(this::exists)
                .collect(Collectors.toList());
    }

    private boolean exists(Path path) {
        final File file = path.toFile();
        return file.exists() && file.isDirectory();
    }

    public boolean fullScreen() {
        return hasSwitch("-fullscreen");
    }

    public int swapInterval() {
        return Integer.valueOf(
                value("swap.interval")
                        .orElse("1")
        );
    }

    public Path home() {
        final Path path = value("app.home")
                .map(Paths::get)
                .orElse(
                        Paths.get(
                                System.getProperty("user.home"),
                                System.getProperty("app.folder", "lwjgl3-playground")
                        )
                );
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return path;
    }
}
