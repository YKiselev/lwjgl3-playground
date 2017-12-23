package cob.github.ykiselev.lwjgl3.layers;

import cob.github.ykiselev.lwjgl3.playground.FrameBufferEvents;
import cob.github.ykiselev.lwjgl3.playground.WindowEvents;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiLayers extends WindowEvents, FrameBufferEvents, AutoCloseable {

    enum Kind {

        GAME, MENU
    }

    void show(Kind kind);

    void push(UiLayer layer);

    void pop(UiLayer layer);

    void draw();
}
