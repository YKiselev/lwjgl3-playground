package com.github.ykiselev.services.configuration.values;

/**
 * Mutable value.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ConfigValue {

    String getString();

    void setString(String value);

    Object boxed();
}
