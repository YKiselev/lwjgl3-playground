package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.configuration.ConfigValue;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ConfigToFile implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Object> map;

    private final FileSystem fileSystem;

    ConfigToFile(Map<String, Object> map, FileSystem fileSystem) {
        this.map = requireNonNull(map);
        this.fileSystem = requireNonNull(fileSystem);
    }

    @Override
    public void run() {
        logger.info("Saving config...");
        try (WritableByteChannel channel = fileSystem.openForWriting("app.conf", false)) {
            try (Writer writer = Channels.newWriter(channel, "utf-8")) {
                writer.write(asString());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String asString() {
        return ConfigFactory.parseMap(
                transform(map)
        ).root()
                .render(
                        ConfigRenderOptions.defaults()
                                .setFormatted(true)
                                .setOriginComments(false)
                                .setJson(false)
                );
    }

    private Map<String, Object> transform(Map<String, Object> src) {
        final Predicate<Map.Entry<String, Object>> valuesOnly = e ->
                e.getValue() instanceof ConfigValue;
        return src.entrySet()
                .stream()
                .filter(valuesOnly)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> ((ConfigValue) e.getValue()).boxed()
                ));
    }
}
