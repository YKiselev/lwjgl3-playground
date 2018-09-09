package com.github.ykiselev.services.configuration;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface LongValue extends ConfigValue {

    long value();

    void value(long value);
}
