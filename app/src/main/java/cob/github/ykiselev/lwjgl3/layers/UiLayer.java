package cob.github.ykiselev.lwjgl3.layers;

import cob.github.ykiselev.lwjgl3.playground.WindowEvents;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiLayer extends WindowEvents {

    void draw(int width, int height);

}
