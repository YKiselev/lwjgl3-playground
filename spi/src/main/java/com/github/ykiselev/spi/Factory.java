package com.github.ykiselev.spi;

import com.github.ykiselev.services.Services;

/**
 * Implementing classes should have public no-arg constructor.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Factory<T> {

    T create(Services services);
}
