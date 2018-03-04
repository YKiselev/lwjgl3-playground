package com.github.ykiselev.lifetime;

import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Manageable<T extends Manageable<T> & AutoCloseable> {

    T manage(Consumer<T> onClose);
}
