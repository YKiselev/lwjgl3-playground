package com.github.ykiselev.lwjgl3.services;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(String message) {
        super(message);
    }

    public ServiceNotFoundException(Class<?> clazz) {
        this(clazz.toString());
    }
}