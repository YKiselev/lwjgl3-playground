package cob.github.ykiselev.lwjgl3.services;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Services extends AutoCloseable {

    <T> void add(Class<T> clazz, T instance);

    <T> void remove(Class<T> clazz, Object instance);

    <T> T resolve(Class<T> clazz);
}
