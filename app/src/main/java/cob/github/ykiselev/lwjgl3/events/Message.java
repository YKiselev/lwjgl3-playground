package cob.github.ykiselev.lwjgl3.events;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Message<R> {

    Class<R> responseType();
}
