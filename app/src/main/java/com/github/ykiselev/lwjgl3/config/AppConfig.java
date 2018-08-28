package com.github.ykiselev.lwjgl3.config;

import com.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import com.github.ykiselev.services.FileSystem;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.config.ValueChangingEvent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
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

    private volatile Config config;

    private final AutoCloseable group;

    private static final VarHandle CH;

    static {
        try {
            CH = MethodHandles.lookup()
                    .in(AppConfig.class)
                    .findVarHandle(AppConfig.class, "config", Config.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public AppConfig(Services services) {
        this.services = requireNonNull(services);
        this.config = load();
        group = new SubscriptionsBuilder(services.resolve(Events.class))
                .with(ValueChangingEvent.class, this::onValueChangingEvent)
                .build();
    }

    private ValueChangingEvent onValueChangingEvent(ValueChangingEvent event) {
        // we don't need this event but there should be at least one subscriber or exception will be thrown
        return event;
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
            final ValueChangingEvent result = services.resolve(Events.class).fire(
                    new ValueChangingEvent(path, oldValue, value)
            );
            if (result != null) {
                updateConfig(path, ConfigValueFactory.fromAnyRef(value));
            }
        }
    }

    private void updateConfig(String path, ConfigValue value) {
        for (; ; ) {
            final Config before = this.config;
            final Config after = before.withValue(path, value);
            if (CH.compareAndSet(this, before, after)) {
                break;
            }
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
                .openAll("app.conf")
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
                                .setFormatted(true)
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
