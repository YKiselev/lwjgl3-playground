package cob.github.ykiselev.lwjgl3.config;

import cob.github.ykiselev.lwjgl3.events.SubscriberGroup;
import cob.github.ykiselev.lwjgl3.events.SubscriberGroupBuilder;
import cob.github.ykiselev.lwjgl3.events.config.ValueChangingEvent;
import cob.github.ykiselev.lwjgl3.host.Host;
import com.github.ykiselev.io.FileSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
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

    private final Host host;

    private Config config;

    private final SubscriberGroup group;

    public AppConfig(Host host) throws IOException {
        this.host = requireNonNull(host);
        this.config = load();
        group = new SubscriberGroupBuilder()
                .add(ValueChangingEvent.class, this::onValueChangingEvent)
                .build(host.events());
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
            host.events().send(
                    new ValueChangingEvent(path, oldValue, value)
            );
            config = config.withValue(
                    path,
                    ConfigValueFactory.fromAnyRef(value)
            );
        }
    }

    @Override
    public void close() throws IOException {
        group.close();
        persist();
    }

    private Object getValue(String path) {
        return config.hasPath(path) ? config.getValue(path).unwrapped() : null;
    }

    private Config readFromFile() throws IOException {
        final FileSystem fs = host.services().resolve(FileSystem.class);
        final Config cfg;
        if (fs.exists("app.conf")) {
            try (ReadableByteChannel channel = fs.openForReading("app.conf")) {
                try (Reader reader = Channels.newReader(channel, "utf-8")) {
                    cfg = ConfigFactory.parseReader(reader);
                }
            }
        } else {
            cfg = ConfigFactory.empty();
        }
        return cfg;
    }

    private Config load() throws IOException {
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
        final FileSystem fs = host.services().resolve(FileSystem.class);
        try (WritableByteChannel channel = fs.openForWriting("app.conf", false)) {
            try (Writer writer = Channels.newWriter(channel, "utf-8")) {
                writer.write(asString());
            }
        }
    }
}
