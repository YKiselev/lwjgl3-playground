package com.github.ykiselev.services.layers;


import com.github.ykiselev.window.WindowEvents;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiLayer {

    WindowEvents events();

    default void onPush() {
    }

    void draw(int width, int height);

    default void onPop() {
    }
}
