package com.github.ykiselev.lwjgl3.layers;

import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.services.layers.UiLayers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppUiLayersTest {

    private UiLayers layers = new AppUiLayers();

    @Test
    void shouldPushAndPopInOrder() {
        UiLayer layer1 = mock(UiLayer.class);
        UiLayer layer2 = mock(UiLayer.class);

        layers.push(layer1);
        layers.push(layer2);
        verify(layer1, times(1)).onPush();
        verify(layer2, times(1)).onPush();

        layers.pop(layer2);
        layers.pop(layer1);
        verify(layer2, times(1)).onPop();
        verify(layer1, times(1)).onPop();
    }

    @Test
    void shouldFailPushAndPopInWrongOrder() {
        UiLayer layer1 = mock(UiLayer.class);
        UiLayer layer2 = mock(UiLayer.class);

        layers.push(layer1);
        layers.push(layer2);
        verify(layer1, times(1)).onPush();
        verify(layer2, times(1)).onPush();

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> layers.pop(layer1)
        );
    }

    @Test
    void shouldBringToFront() {
        UiLayer layer1 = mock(UiLayer.class);
        UiLayer layer2 = mock(UiLayer.class);
        UiLayer layer3 = mock(UiLayer.class);

        when(layer1.isPopup()).thenReturn(false);
        when(layer2.isPopup()).thenReturn(false);
        when(layer3.isPopup()).thenReturn(true);

        layers.push(layer1);
        layers.push(layer2);
        layers.push(layer3);
        layers.bringToFront(layer1);

        verify(layer1, times(1)).onPop();
        verify(layer1, times(2)).onPush();
        verify(layer3, times(1)).onPop();
    }
}