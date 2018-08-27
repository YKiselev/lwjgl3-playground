package com.github.ykiselev.services.layers;


import com.github.ykiselev.window.WindowEvents;

/**
 * Methods of this class may only be called from UI thread.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiLayers {

    /**
     * @return window events handler
     */
    WindowEvents events();

    /**
     * Clears current stack of layers and then pushes passed value.
     *
     * @param layer the layer to push after clearing of current layer stack.
     * @throws NullPointerException if layer is {@code null}
     */
    void replace(UiLayer layer);

    /**
     * Adds passed value on top of current layer stack.
     *
     * @param layer the layer to add on top of current stack.
     * @throws IllegalArgumentException if layer already on stack
     * @throws NullPointerException     if layer is {@code null}
     */
    void push(UiLayer layer);

    /**
     * Removes passed value from the top of the stack
     *
     * @param layer the layer to remove from the top of the stack.
     * @throws IllegalArgumentException if layer is not on top of stack.
     * @throws NullPointerException     if layer is {@code null}
     */
    void pop(UiLayer layer);

    /**
     * This method should draw current stack of layers.
     */
    void draw();
}