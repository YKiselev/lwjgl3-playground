package cob.github.ykiselev.lwjgl3.layers;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiLayers {

    enum Kind {

        GAME, MENU
    }

    void show(Kind kind);

    void push(UiLayer layer);

    void pop(UiLayer layer);
}
