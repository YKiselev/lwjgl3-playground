package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.io.FileSystem;
import com.github.ykiselev.lwjgl3.events.Events;
import com.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import com.github.ykiselev.lwjgl3.events.config.InvalidValueException;
import com.github.ykiselev.lwjgl3.events.config.ValueChangingEvent;
import com.github.ykiselev.lwjgl3.services.Services;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConfig implements PersistedConfiguration, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Services services;

    private Config config;

    private final CompositeAutoCloseable group;

    public AppConfig(Services services) {
        this.services = requireNonNull(services);
        this.config = load();
        group = new SubscriptionsBuilder()
                .with(ValueChangingEvent.class, this::onValueChangingEvent)
                .build(services.resolve(Events.class));
    }

    private void onValueChangingEvent(ValueChangingEvent event) {
        // we don't need this event but there should be at least one subscriber or exception will be thrown
    }

    @Override
    public Config root() {
        return config;
    }

    @Override
    public void set(String path, Object value) {
        final Object oldValue = getValue(path);
        if (Objects.equals(oldValue, value)) {
            logger.debug("Skipping setting \"{}\" to the same value \"{}\"...", path, value);
        } else {
            logger.debug("Setting \"{}\" to \"{}\"", path, value);
            try {
                services.resolve(Events.class).fire(
                        new ValueChangingEvent(path, oldValue, value)
                );
            } catch (InvalidValueException e) {
                logger.error("Unable to set \"{}\" to supplied value \"{}\"", path, value, e);
                return;
            }
            config = config.withValue(
                    path,
                    ConfigValueFactory.fromAnyRef(value)
            );
        }
    }

    @Override
    public void close() throws Exception {
        group.close();
        persist();
    }

    private Object getValue(String path) {
        return config.hasPath(path) ? config.getValue(path).unwrapped() : null;
    }

    private Config readFromFile() {
        return services.resolve(FileSystem.class)
                .open("app.conf")
                .map(this::readFromFile)
                .orElse(ConfigFactory.empty());
    }

    private Config readFromFile(ReadableByteChannel channel) {
        try {
            try (Reader reader = Channels.newReader(channel, "utf-8")) {
                return ConfigFactory.parseReader(reader);
            } finally {
                channel.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Config load() {
        logger.info("Loading config...");
        return readFromFile()
                .withFallback(ConfigFactory.parseResources("fallback/app.conf"))
                .resolve();
    }

    private String asString() {
        return config.root()
                .render(
                        ConfigRenderOptions.defaults()
                                .setOriginComments(false)
                                .setJson(false)
                );
    }

    private void persist() throws IOException {
        logger.info("Saving config...");
        final FileSystem fs = services.resolve(FileSystem.class);
        try (WritableByteChannel channel = fs.openForWriting("app.conf", false)) {
            try (Writer writer = Channels.newWriter(channel, "utf-8")) {
                writer.write(asString());
            }
        }
    }
}
