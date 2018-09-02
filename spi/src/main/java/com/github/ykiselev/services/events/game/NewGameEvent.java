package com.github.ykiselev.services.events.game;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class NewGameEvent {

    public static final NewGameEvent INSTANCE = new NewGameEvent();

    private NewGameEvent() {
    }
}
