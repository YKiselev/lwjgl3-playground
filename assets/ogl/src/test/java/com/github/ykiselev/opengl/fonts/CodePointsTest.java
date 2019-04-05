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

package com.github.ykiselev.opengl.fonts;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 06.04.2019
 */
public class CodePointsTest {

    @Test
    public void shouldRefine() {
        final List<CodePoints.Range> refined = CodePoints.refine(
                new CodePoints.Range(1, 10),
                new CodePoints.Range(22, 30),
                new CodePoints.Range(11, 21),
                new CodePoints.Range(18, 20),
                new CodePoints.Range(32, 45)
        );
        assertEquals(2, refined.size());
        assertEquals(new CodePoints.Range(1, 30), refined.get(0));
        assertEquals(new CodePoints.Range(32, 45), refined.get(1));
    }

    @Test
    public void shouldResolveIndex() {
        final CodePoints codePoints = CodePoints.of(
                new CodePoints.Range(100, 110),
                new CodePoints.Range(220, 230)
        );

        assertEquals(-1, codePoints.indexOf(99));
        assertEquals(-1, codePoints.indexOf(11));

        assertEquals(-1, codePoints.indexOf(219));
        assertEquals(-1, codePoints.indexOf(231));

        assertEquals(0, codePoints.indexOf(100));
        assertEquals(10, codePoints.indexOf(110));

        assertEquals(11, codePoints.indexOf(220));
        assertEquals(21, codePoints.indexOf(230));
    }
}