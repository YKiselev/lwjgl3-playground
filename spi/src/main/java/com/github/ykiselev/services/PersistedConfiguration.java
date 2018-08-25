package com.github.ykiselev.services;

import com.typesafe.config.Config;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface PersistedConfiguration {

    Config root();

    void set(String path, Object value);
}
