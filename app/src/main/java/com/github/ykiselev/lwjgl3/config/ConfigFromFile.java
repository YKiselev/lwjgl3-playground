package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.configuration.values.Values;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ConfigFromFile implements Supplier<Map<String, Object>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FileSystem fileSystem;

    ConfigFromFile(FileSystem fileSystem) {
        this.fileSystem = requireNonNull(fileSystem);
    }

    @Override
    public Map<String, Object> get() {
        logger.info("Loading config...");
        return transform(
                readConfigObject()
        );
    }

    private Map<String, Object> transform(ConfigObject obj) {
        return obj.keySet()
                .stream()
                .flatMap(k -> denormalize(k, obj.get(k).unwrapped()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> value(e.getValue())
                ));
    }

    private ConfigObject readConfigObject() {
        return readFromFile()
                .withFallback(ConfigFactory.parseURL(
                        getClass().getResource("/fallback/app.conf")
                )).resolve()
                .root();
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
            @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) value;
            return Stream.concat(
                    Stream.of(
                            new AbstractMap.SimpleImmutableEntry<>(
                                    key,
                                    new Section(
                                            new HashSet<>(map.keySet())
                                    )
                            )
                    ),
                    map.entrySet()
                            .stream()
                            .flatMap(e -> denormalize(key + "." + e.getKey(), e.getValue()))
            );
        }
        return Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(key, value)
        );
    }

    @SuppressWarnings("unchecked")
    public static Object value(Object value) {
        if (value instanceof Section) {
            return value;
        }
        if (value instanceof List) {
            return new ConstantList((List<?>) value);
        }
        return Values.toSimpleValue(value);
    }

}
