package com.github.ykiselev.services;

import com.github.ykiselev.services.configuration.Config;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface PersistedConfiguration {

    Config root();

    void setString(String path, String value);

    void setBoolean(String path, boolean value);

    void setInt(String path, int value);

    void setLong(String path, long value);

    void setFloat(String path, float value);

    void setDouble(String path, double value);
}
