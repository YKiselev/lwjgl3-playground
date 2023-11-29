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
package com.github.ykiselev.spi.services.layers

import com.github.ykiselev.spi.window.WindowEvents

/**
 * Methods of this class may only be called from UI thread.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
interface UiLayers {
    /**
     * @return window events handler
     */
    fun events(): WindowEvents

    /**
     * Removes passed layer from the top of the stack.
     *
     * @param layer the layer to remove from the top of the stack.
     * @throws IllegalArgumentException if layer is not on top of stack.
     * @throws NullPointerException     if layer is `null`.
     */
    fun pop(layer: UiLayer)

    /**
     * Removes all popup layers (having [UiLayer.Kind.POPUP]).
     */
    fun removePopups()

    /**
     * Adds layer to the top of current stack and sorts the stack. Does nothing if layer is already on stack.
     *
     * @param layer the layer to add.
     * @throws NullPointerException if layer is `null`.
     */
    fun add(layer: UiLayer)

    /**
     * Removes layer from the current stack. Does nothing if layer is not the part of stack.
     *
     * @param layer the layer to remove.
     */
    fun remove(layer: UiLayer)

    /**
     * This method should draw current stack of layers.
     *
     * @param context the context to use
     */
    fun draw(context: DrawingContext)
}
