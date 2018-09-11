package com.github.ykiselev.services;

import com.github.ykiselev.services.configuration.Config;
import com.github.ykiselev.services.configuration.values.ConfigValue;

import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface PersistedConfiguration {

    /**
     * @return the configuration root.
     */
    Config root();

    /**
     * Wires specified config values to this configuration.
     *
     * @param values the config values to vire to this config.
     * @return the handle to the wired variable.
     */
    AutoCloseable wire(Map<String, ConfigValue> values);
}
