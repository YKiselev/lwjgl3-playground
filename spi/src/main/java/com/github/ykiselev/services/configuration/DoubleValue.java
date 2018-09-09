package com.github.ykiselev.services.configuration;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface DoubleValue extends ConfigValue {

    double value();

    void value(double value);
}
