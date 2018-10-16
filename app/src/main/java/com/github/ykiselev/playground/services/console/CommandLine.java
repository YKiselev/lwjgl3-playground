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

package com.github.ykiselev.playground.services.console;

import com.github.ykiselev.services.layers.DrawingContext;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface CommandLine {

    Marker MARKER = MarkerFactory.getMarker("COMMAND_LINE");

    void add(int codePoint);

    void removeLeft();

    void remove();

    void left();

    void right();

    void draw(DrawingContext ctx, int x0, int y0, int width, int height);

    void begin();

    void end();

    void complete();

    /**
     * Clears command buffer, moves caret to initial position, cancels history search, clears fragments
     */
    void reset();

    void execute();

    void searchHistoryBackward();

    void searchHistory();
}
