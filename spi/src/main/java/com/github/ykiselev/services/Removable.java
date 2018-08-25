package com.github.ykiselev.services;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Removable {

    /**
     * Implementing classes may use this method to decide if object is not currently in use and may be safely unloaded.
     *
     * @return {@code true} if service is not used at the moment and may be safely removed.
     */
    boolean canBeRemoved();
}
