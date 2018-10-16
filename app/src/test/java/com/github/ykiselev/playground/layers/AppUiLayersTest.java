/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.playground.layers;

import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.window.WindowEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppUiLayersTest {

    private UiLayers layers = new AppUiLayers();

    private UiLayer layer1 = mock(UiLayer.class, "popup1");

    private UiLayer layer2 = mock(UiLayer.class, "popup2");

    private UiLayer layer3 = mock(UiLayer.class, "layer3");

    @BeforeEach
    void setUp() {
        when(layer1.kind()).thenReturn(UiLayer.Kind.POPUP);
        when(layer1.isPopup()).thenReturn(true);

        when(layer2.kind()).thenReturn(UiLayer.Kind.POPUP);
        when(layer2.isPopup()).thenReturn(true);

        when(layer3.kind()).thenReturn(UiLayer.Kind.GAME);
        when(layer3.isPopup()).thenReturn(false);
    }

    @Test
    void shouldAddAndPopInOrder() {
        layers.add(layer1);
        layers.add(layer2);
        verify(layer1, times(1)).onPush();
        verify(layer2, times(1)).onPush();

        layers.pop(layer2);
        layers.pop(layer1);
        verify(layer2, times(1)).onPop();
        verify(layer1, times(1)).onPop();
    }

    @Test
    void shouldAddPopupOnTop() {
        layers.add(layer1);
        layers.add(layer3);
        layers.pop(layer1);
    }

    @Test
    void shouldFailIfAddAndPopInWrongOrder() {
        layers.add(layer1);
        layers.add(layer2);
        verify(layer1, times(1)).onPush();
        verify(layer2, times(1)).onPush();

        assertThrows(
                IllegalArgumentException.class,
                () -> layers.pop(layer1)
        );
    }

    @Test
    void shouldDrawBottomUp() {
        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
        layers.draw();

        InOrder inOrder = inOrder(layer1, layer2, layer3);

        inOrder.verify(layer3, times(1)).draw(0, 0);
        inOrder.verify(layer1, times(1)).draw(0, 0);
        inOrder.verify(layer2, times(1)).draw(0, 0);
    }

    @Test
    void shouldDispatchFromTopBottom() {
        WindowEvents events = mock(WindowEvents.class);

        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);

        when(layer1.events()).thenReturn(events);
        when(layer2.events()).thenReturn(events);
        when(layer3.events()).thenReturn(events);

        layers.events().keyEvent(1, 2, 3, 4);

        InOrder inOrder = inOrder(layer1, layer2, layer3);

        inOrder.verify(layer2, times(1)).events();
        inOrder.verify(layer1, times(1)).events();
        inOrder.verify(layer3, times(1)).events();

        verify(events, times(3)).keyEvent(1, 2, 3, 4);
    }

    @Test
    void shouldRemovePopups() {
        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
        layers.removePopups();

        verify(layer1, times(1)).onPush();
        verify(layer2, times(1)).onPush();
        verify(layer3, times(1)).onPush();

        verify(layer1, times(1)).onPop();
        verify(layer2, times(1)).onPop();
        verify(layer3, times(0)).onPop();
    }

    @Test
    void shouldRemove() {
        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
        layers.remove(layer2);

        verify(layer1, times(0)).onPop();
        verify(layer2, times(1)).onPop();
        verify(layer3, times(0)).onPop();
    }
}