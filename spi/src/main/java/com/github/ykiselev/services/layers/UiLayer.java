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
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface UiLayer {

    enum Kind {
        NORMAL, POPUP
    }

    WindowEvents events();

    default void onPush() {
    }

    void draw(int width, int height);

    default void onPop() {
    }

    Kind kind();

    default boolean isPopup() {
        return kind() == Kind.POPUP;
    }

    default boolean isNormal() {
        return kind() == Kind.NORMAL;
    }
}
