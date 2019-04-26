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

package com.github.ykiselev.spi.window;

import static java.util.Objects.requireNonNull;

/**
 * This class can be used as a boilerplate in cases when you only need to override some of {@link WindowEvents} methods.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class DelegatingWindowEvents implements WindowEvents {

    private final WindowEvents delegate;

    public DelegatingWindowEvents(WindowEvents delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public boolean keyEvent(int key, int scanCode, int action, int mods) {
        return delegate.keyEvent(key, scanCode, action, mods);
    }

    @Override
    public void cursorEvent(double x, double y) {
        delegate.cursorEvent(x, y);
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        return delegate.mouseButtonEvent(button, action, mods);
    }

    @Override
    public void frameBufferResized(int width, int height) {
        delegate.frameBufferResized(width, height);
    }

    @Override
    public boolean scrollEvent(double dx, double dy) {
        return delegate.scrollEvent(dx, dy);
    }
}
