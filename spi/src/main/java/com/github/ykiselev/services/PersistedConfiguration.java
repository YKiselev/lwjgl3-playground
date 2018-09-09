package com.github.ykiselev.services;

import com.github.ykiselev.services.configuration.Config;
import com.github.ykiselev.services.configuration.ConfigurationException.ConfigNotFoundException;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface PersistedConfiguration {

    Config root() throws ConfigNotFoundException;

}
