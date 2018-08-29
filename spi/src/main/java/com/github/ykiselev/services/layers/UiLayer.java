package com.github.ykiselev.services.layers;


import com.github.ykiselev.window.WindowEvents;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiLayer {

    enum Kind {
        NORMAL, POPUP
    }

    WindowEvents events();

    default void onPush() {
    }

    void draw(int width, int height);

    default void onPop() {
    }

    Kind kind();

    default boolean isPopup() {
        return kind() == Kind.POPUP;
    }

    default boolean isNormal() {
        return kind() == Kind.NORMAL;
    }
}
