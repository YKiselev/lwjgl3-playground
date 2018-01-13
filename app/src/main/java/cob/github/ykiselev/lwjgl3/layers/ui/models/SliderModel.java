package cob.github.ykiselev.lwjgl3.layers.ui.models;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SliderModel {

    SliderDefinition definition();

    int value();

    void value(int value);

    void increase();

    void decrease();
}
