package com.github.ykiselev.lwjgl3.services;

/**
 * Interface should be implemented by services which may be dynamically added/removed during run-time.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Removable {

    /**
     * Implementing classes may use this method to decide if service is not currently in use and may be safely unloaded.
     *
     * @return {@code true} if service is not used at the moment and may be safely removed.
     */
    boolean canBeRemoved();
}
