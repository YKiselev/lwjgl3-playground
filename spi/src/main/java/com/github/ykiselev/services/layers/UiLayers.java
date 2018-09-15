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
     * Removes all popup layers from top and then makes passed layer a topmost.
     *
     * @param layer the layer to push after clearing of current layer stack.
     * @throws NullPointerException if layer is {@code null}
     */
    void bringToFront(UiLayer layer);

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
     * Removes layer from the current stack. Does nothing if layer is not the part of stack.
     *
     * @param layer the layer to remove
     */
    void remove(UiLayer layer);

    /**
     * This method should draw current stack of layers.
     */
    void draw();
}
