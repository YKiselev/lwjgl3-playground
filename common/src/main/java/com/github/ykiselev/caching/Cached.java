package com.github.ykiselev.caching;

/**
 * Idea here is that instead of getting direct reference to value of type {@code V} user gets instance of this class
 * and uses method {@link Cached#get()} to get actual reference only when it's needed (not saving it anywhere). This
 * gives implementations opportunity to release least recently used underlying objects and thus free system resources.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class Cached<V> {

    /**
     * Returns the reference to the underlying object.
     *
     * @return the value reference or {@code null}
     */
    public abstract V get();
}
