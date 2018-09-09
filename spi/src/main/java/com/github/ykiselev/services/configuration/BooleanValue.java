package com.github.ykiselev.services.configuration;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface BooleanValue extends ConfigValue {

    boolean value();

    void value(boolean value);
}
