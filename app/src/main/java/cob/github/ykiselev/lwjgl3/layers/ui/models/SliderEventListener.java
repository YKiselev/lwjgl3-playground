package cob.github.ykiselev.lwjgl3.layers.ui.models;

import cob.github.ykiselev.lwjgl3.layers.ui.models.SliderModel;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SliderEventListener {

    void onSliderChanged(SliderModel source, int oldValue);

}
