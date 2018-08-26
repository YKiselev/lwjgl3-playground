package com.github.ykiselev.spi;

import com.github.ykiselev.services.Services;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Factory<T> {

    T create(Services services);
}
