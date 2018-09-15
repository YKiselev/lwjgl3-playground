/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.lwjgl3.services.config;

import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.configuration.values.ConfigValue;
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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ConfigToFile implements Consumer<Map<String, Object>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FileSystem fileSystem;

    ConfigToFile(FileSystem fileSystem) {
        this.fileSystem = requireNonNull(fileSystem);
    }

    @Override
    public void accept(Map<String, Object> config) {
        logger.info("Saving config...");
        try (WritableByteChannel channel = fileSystem.openForWriting("app.conf", false)) {
            try (Writer writer = Channels.newWriter(channel, "utf-8")) {
                writer.write(asString(config));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String asString(Map<String, Object> config) {
        return ConfigFactory.parseMap(
                transform(config)
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
