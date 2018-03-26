package com.github.ykiselev.lwjgl3.layers.ui.models.checkbox;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface CheckBoxModel {

    boolean checked();

    void checked(boolean value);

    default void flip() {
        checked(!checked());
    }
}
