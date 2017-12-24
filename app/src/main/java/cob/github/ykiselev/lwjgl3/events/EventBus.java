package cob.github.ykiselev.lwjgl3.events;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface EventBus {

    void send(Object message);

    void deliver(Object message);

}
