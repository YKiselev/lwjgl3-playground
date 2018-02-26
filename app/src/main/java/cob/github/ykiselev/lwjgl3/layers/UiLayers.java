package cob.github.ykiselev.lwjgl3.layers;

import cob.github.ykiselev.lwjgl3.playground.WindowEvents;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiLayers {

    WindowEvents events();

    void replace(UiLayer layer);

    void push(UiLayer layer);

    void pop(UiLayer layer);

    void draw();
}
