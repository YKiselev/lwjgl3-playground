package com.github.ykiselev.components;

import com.github.ykiselev.services.layers.UiLayer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Game extends UiLayer, AutoCloseable {

    @Override
    default Kind kind() {
        return Kind.NORMAL;
    }
}
