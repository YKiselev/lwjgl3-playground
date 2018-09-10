package com.github.ykiselev.lwjgl3.sound;

import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.configuration.WiredValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class SoundSettings {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int effectsLevel;

    int effectsLevel() {
        return effectsLevel;
    }

    void effectsLevel(int effectsLevel) {
        this.effectsLevel = effectsLevel;
    }

    AutoCloseable register(PersistedConfiguration configuration) {
        return configuration.wire(
                new WiredValues()
                        .withInt("sound.effects.level", () -> effectsLevel, this::setEffectsLevel)
                        .build()
        );
    }

    private void setEffectsLevel(int value) {
        logger.info("Setting sound level to {}", value);
        effectsLevel(value);
    }
}
