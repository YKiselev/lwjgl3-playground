package com.github.ykiselev.playground.layers

import com.github.ykiselev.opengl.sprites.SpriteBatch
import com.github.ykiselev.spi.services.layers.DrawingContext
import com.github.ykiselev.spi.services.layers.UiLayer
import com.github.ykiselev.spi.services.layers.UiLayers
import com.github.ykiselev.spi.window.WindowEvents
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class AppUiLayersTest {

    private val layers: UiLayers = AppUiLayers()
    private val layer1 = mock<UiLayer>(name = "popup1")
    private val layer2 = mock<UiLayer>(name = "popup2")
    private val layer3 = mock<UiLayer>(name = "layer3")
    private val batch = mock<SpriteBatch>()
    private val dc = DrawingContext(batch = batch)

    @BeforeEach
    fun setUp() {
        Mockito.`when`(layer1.kind()).thenReturn(UiLayer.Kind.POPUP)
        Mockito.`when`(layer1.isPopup).thenReturn(true)
        Mockito.`when`(layer2.kind()).thenReturn(UiLayer.Kind.POPUP)
        Mockito.`when`(layer2.isPopup).thenReturn(true)
        Mockito.`when`(layer3.kind()).thenReturn(UiLayer.Kind.GAME)
        Mockito.`when`(layer3.isPopup).thenReturn(false)
    }

    @Test
    fun shouldAddAndPopInOrder() {
        layers.add(layer1)
        layers.add(layer2)
        verify(layer1, times(1)).onPush()
        verify(layer2, times(1)).onPush()
        layers.pop(layer2)
        layers.pop(layer1)
        verify(layer2, times(1)).onPop()
        verify(layer1, times(1)).onPop()
    }

    @Test
    fun shouldAddPopupOnTop() {
        layers.add(layer1)
        layers.add(layer3)
        layers.pop(layer1)
    }

    @Test
    fun shouldFailIfAddAndPopInWrongOrder() {
        layers.add(layer1)
        layers.add(layer2)
        verify(layer1, times(1)).onPush()
        verify(layer2, times(1)).onPush()
        Assertions.assertThrows(
            IllegalArgumentException::class.java
        ) { layers.pop(layer1) }
    }

    @Test
    fun shouldDrawBottomUp() {
        layers.add(layer1)
        layers.add(layer2)
        layers.add(layer3)
        layers.draw(dc)
        val inOrder = inOrder(layer1, layer2, layer3)
        inOrder.verify(layer3, times(1)).draw(0, 0, dc)
        inOrder.verify(layer1, times(1)).draw(0, 0, dc)
        inOrder.verify(layer2, times(1)).draw(0, 0, dc)
    }

    @Test
    fun shouldDispatchFromTopBottom() {
        val events = mock<WindowEvents>()
        layers.add(layer1)
        layers.add(layer2)
        layers.add(layer3)
        Mockito.`when`(layer1.events()).thenReturn(events)
        Mockito.`when`(layer2.events()).thenReturn(events)
        Mockito.`when`(layer3.events()).thenReturn(events)
        layers.events().keyEvent(1, 2, 3, 4)
        val inOrder = inOrder(layer1, layer2, layer3)
        inOrder.verify(layer2, times(1)).events()
        inOrder.verify(layer1, times(1)).events()
        inOrder.verify(layer3, times(1)).events()
        verify(events, times(3)).keyEvent(1, 2, 3, 4)
    }

    @Test
    fun shouldRemovePopups() {
        layers.add(layer1)
        layers.add(layer2)
        layers.add(layer3)
        layers.removePopups()
        verify(layer1, times(1)).onPush()
        verify(layer2, times(1)).onPush()
        verify(layer3, times(1)).onPush()
        verify(layer1, times(1)).onPop()
        verify(layer2, times(1)).onPop()
        verify(layer3, times(0)).onPop()
    }

    @Test
    fun shouldRemove() {
        layers.add(layer1)
        layers.add(layer2)
        layers.add(layer3)
        layers.remove(layer2)
        verify(layer1, times(0)).onPop()
        verify(layer2, times(1)).onPop()
        verify(layer3, times(0)).onPop()
    }
}