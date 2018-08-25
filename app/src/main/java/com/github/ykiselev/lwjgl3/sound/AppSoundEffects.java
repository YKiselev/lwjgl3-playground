package com.github.ykiselev.lwjgl3.sound;

import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.SoundEffects;
import com.github.ykiselev.services.events.EventHandler;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.config.ValueChangingEvent;
import com.github.ykiselev.tree.PrefixTree;
import com.github.ykiselev.tree.PrefixTreeBuilder;
import com.typesafe.config.Config;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;
import java.util.List;

import static com.github.ykiselev.openal.Errors.assertNoAlErrors;
import static java.util.Objects.requireNonNull;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.ALC_FREQUENCY;
import static org.lwjgl.openal.ALC10.ALC_REFRESH;
import static org.lwjgl.openal.ALC10.ALC_SYNC;
import static org.lwjgl.openal.ALC10.ALC_TRUE;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.ALC11.ALC_MONO_SOURCES;
import static org.lwjgl.openal.ALC11.ALC_STEREO_SOURCES;
import static org.lwjgl.openal.ALUtil.getStringList;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppSoundEffects implements SoundEffects, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Services services;

    private final long device;

    private final long context;

    private final CompositeAutoCloseable subscriptions;

    public AppSoundEffects(Services services) {
        this.services = requireNonNull(services);
        final Config config = services.resolve(PersistedConfiguration.class)
                .root()
                .getConfig("sound");

        device = alcOpenDevice(config.getString("device"));
        if (device == NULL) {
            throw new IllegalArgumentException("No device found!");
        }
        final ALCCapabilities capabilities = createCapabilities(device);
        if (!capabilities.OpenALC10) {
            throw new IllegalArgumentException("OpenAL 1.0 is not supported!");
        }
        logger.info("Is OpenALC11 ? {}, has ALC_EXT_EFX ? {}", capabilities.OpenALC11, capabilities.ALC_EXT_EFX);

        if (capabilities.OpenALC11) {
            List<String> devices = getStringList(NULL, ALC11.ALC_ALL_DEVICES_SPECIFIER);
            if (devices == null) {
                assertNoAlErrors();
            } else {
                logger.info("All devices: {}", devices);
            }
        }
        final String defaultDeviceSpecifier = alcGetString(NULL, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        logger.info("Default device: {}", defaultDeviceSpecifier);
        final String deviceSpecifier = alcGetString(device, ALC10.ALC_DEVICE_SPECIFIER);
        logger.info("Device: {}", deviceSpecifier);

        context = alcCreateContext(device, (IntBuffer) null);
        EXTThreadLocalContext.alcSetThreadContext(context);
        AL.createCapabilities(capabilities);

        logger.info(
                "ALC_FREQUENCY={} Hz, ALC_REFRESH={} Hz, ALC_SYNC={}, ALC_MONO_SOURCES={}, ALC_STEREO_SOURCES={}",
                alcGetInteger(device, ALC_FREQUENCY),
                alcGetInteger(device, ALC_REFRESH),
                (alcGetInteger(device, ALC_SYNC) == ALC_TRUE),
                alcGetInteger(device, ALC_MONO_SOURCES),
                alcGetInteger(device, ALC_STEREO_SOURCES)
        );

        final PrefixTree<EventHandler<ValueChangingEvent>> tree = new PrefixTreeBuilder<EventHandler<ValueChangingEvent>>("\\.")
                .add("sound.effects.level", this::onSoundEffectsLevelChanging)
                .build();
        final EventHandler<ValueChangingEvent> handler = event ->
                tree.find(event.path())
                        .map(h -> h.handle(event))
                        .orElse(event);

        subscriptions = new SubscriptionsBuilder(services.resolve(Events.class))
                .with(ValueChangingEvent.class, handler)
                .build();
    }

    private ValueChangingEvent onSoundEffectsLevelChanging(ValueChangingEvent event) {
        logger.info("Setting sound level to {}", event.newValue());
        return event;
    }

    @Override
    public void close() throws Exception {
        alcMakeContextCurrent(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);
        subscriptions.close();
    }
}
