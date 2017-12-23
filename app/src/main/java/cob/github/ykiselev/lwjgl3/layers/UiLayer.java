package cob.github.ykiselev.lwjgl3.layers;

import cob.github.ykiselev.lwjgl3.playground.WindowEvents;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiLayer extends WindowEvents, AutoCloseable {

    void attach(UiLayers layers);

    void draw(int width, int height);
}
