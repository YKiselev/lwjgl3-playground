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

package com.github.ykiselev.opengl.textures;

import com.github.ykiselev.opengl.Bindable;
import com.github.ykiselev.opengl.Identified;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Texture2d extends Identified, Bindable, AutoCloseable {

    @Override
    default void bind() {
        glBindTexture(GL_TEXTURE_2D, id());
    }

    @Override
    default void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    default void close() {
        glDeleteTextures(id());
    }
}
