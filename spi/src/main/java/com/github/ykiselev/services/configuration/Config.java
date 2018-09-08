package com.github.ykiselev.services.configuration;

import java.util.Collection;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Config {

    Config getConfig(String path);

    boolean hasPath(String path);

    String getString(String path);

    boolean getBoolean(String path);

    int getInt(String path);

    long getLong(String path);

    float getFloat(String path);

    double getDouble(String path);

    Collection<String> getStringList(String path);
}
