package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.services.FileSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ConfigFromFile implements Supplier<Map<String, ConfigValue>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FileSystem fileSystem;

    ConfigFromFile(FileSystem fileSystem) {
        this.fileSystem = requireNonNull(fileSystem);
    }

    @Override
    public Map<String, ConfigValue> get() {
        logger.info("Loading config...");
        return readFromFile()
                .withFallback(ConfigFactory.parseResources("fallback/app.conf"))
                .resolve()
                .root()
                .entrySet()
                .stream()
                .flatMap(e -> denormalize(e.getKey(), e.getValue().unwrapped()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> value(e.getValue())
                ));
    }

    private Config readFromFile() {
        return fileSystem.openAll("app.conf")
                .map(this::readFromFile)
                .reduce(ConfigFactory.empty(), Config::withFallback);
    }

    private Config readFromFile(ReadableByteChannel channel) {
        try {
            try (Reader reader = Channels.newReader(channel, "utf-8")) {
                return ConfigFactory.parseReader(reader);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Stream<Map.Entry<String, Object>> denormalize(String key, Object value) {
        if (value instanceof Map) {
            return ((Map<String, Object>) value).entrySet()
                    .stream()
                    .flatMap(e -> denormalize(key + "." + e.getKey(), e.getValue()));
        } else {
            return Stream.of(
                    new AbstractMap.SimpleImmutableEntry<>(
                            key,
                            value
                    )
            );
        }
    }

    public static ConfigValue value(Object value) {
        if (value instanceof List) {
            return new ListValue((List<String>) value);
        }
        final SimpleValue result = new SimpleValue();
        if (value == null || value instanceof String) {
            result.setString((String) value);
        } else if (value instanceof Long) {
            result.setLong((long) value);
        } else if (value instanceof Integer) {
            result.setInt((int) value);
        } else if (value instanceof Double) {
            result.setDouble((double) value);
        } else if (value instanceof Boolean) {
            result.setBoolean((boolean) value);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value);
        }
        return result;
    }

}
