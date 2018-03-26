package com.github.ykiselev.lwjgl3.layers;

import com.github.ykiselev.lwjgl3.playground.WindowEvents;

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