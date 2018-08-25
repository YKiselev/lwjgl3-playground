package com.github.ykiselev.spi;

import com.github.ykiselev.services.Services;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ServiceFactory<T> {

    T create(Services services);
}
