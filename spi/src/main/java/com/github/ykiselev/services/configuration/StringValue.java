package com.github.ykiselev.services.configuration;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface StringValue extends ConfigValue {

    String value();

    void value(String value);
}
