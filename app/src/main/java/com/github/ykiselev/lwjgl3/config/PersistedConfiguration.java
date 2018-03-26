package com.github.ykiselev.lwjgl3.config;

import com.typesafe.config.Config;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface PersistedConfiguration {

    Config root();

    void set(String path, Object value);
}
