package com.github.ykiselev.services;

import com.github.ykiselev.services.configuration.Config;
import com.github.ykiselev.services.configuration.values.ConfigValue;
import com.github.ykiselev.services.configuration.ConfigurationException.ConfigNotFoundException;

import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface PersistedConfiguration {

    Config root() throws ConfigNotFoundException;

    AutoCloseable wire(Map<String, ConfigValue> values);
}
