package cob.github.ykiselev.lwjgl3.host;

import cob.github.ykiselev.lwjgl3.events.Events;
import cob.github.ykiselev.lwjgl3.services.Services;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Host {

    Events events();

    Services services();
}
