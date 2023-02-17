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

package com.github.ykiselev.playground.services.config;

import com.github.ykiselev.spi.services.FileSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class AppFileConfig implements FileConfig {

    private final ConfigRenderOptions options = ConfigRenderOptions.defaults()
            .setFormatted(true)
            .setOriginComments(false)
            .setJson(false);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FileSystem fileSystem;

    private volatile Config config = ConfigFactory.empty();

    AppFileConfig(FileSystem fileSystem) {
        this.fileSystem = requireNonNull(fileSystem);
    }

    @Override
    public Object getValue(String name) {
        if (config.hasPath(name)) {
            return config.getAnyRef(name);
        }
        return null;
    }

    @Override
    public void persist(String name, Map<String, Object> config) {
        logger.info("Saving config to {}...", name);
        try (WritableByteChannel channel = fileSystem.truncate("app.conf")) {
            try (Writer writer = Channels.newWriter(channel, StandardCharsets.UTF_8)) {
                writer.write(
                        ConfigFactory.parseMap(config)
                                .root()
                                .render(options)
                );
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void load(String name) {
        fileSystem.open(name)
                .map(this::readFromFile)
                .ifPresent(this::set);
    }

    @Override
    public void loadAll(String name) {
        set(fileSystem.openAll(name)
                .map(this::readFromFile)
                .reduce(ConfigFactory.empty(), Config::withFallback));

    }

    private void set(Config value) {
        this.config = value.resolve();
    }

    private Config readFromFile(ReadableByteChannel channel) {
        try {
            try (Reader reader = Channels.newReader(channel, StandardCharsets.UTF_8)) {
                return ConfigFactory.parseReader(reader);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
