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
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 06.04.2019
 */
public class CodePointsTest {

    @Test
    public void shouldCollectDenseRanges() {
        List<CodePoints.DenseRange> ranges = CodePoints.collectDenseRanges(IntStream.of(1, 3, 4, 7, 8, 10).toArray());
        assertEquals(4, ranges.size());
        List<CodePoints.Range> merged = CodePoints.mergeDegenerates(ranges);
        assertEquals(1, merged.size());
    }

    @Test
    public void shouldRefine() {
        final CodePoints codePoints = CodePoints.of(
                IntStream.concat(
                        IntStream.concat(
                                IntStream.concat(
                                        IntStream.range(1, 11),
                                        IntStream.range(22, 31)
                                ),
                                IntStream.concat(
                                        IntStream.range(11, 22),
                                        IntStream.range(18, 21)
                                )
                        ),
                        IntStream.range(32, 46)
                )
        );
        assertEquals(2, codePoints.numRanges());
        assertTrue(
                IntStream.range(1, 31)
                        .map(codePoints::indexOf)
                        .allMatch(idx -> idx > -1)
        );
        assertTrue(
                IntStream.range(32, 46)
                        .map(codePoints::indexOf)
                        .allMatch(idx -> idx > -1)
        );
        assertTrue(
                IntStream.range(46, 100)
                        .map(codePoints::indexOf)
                        .allMatch(idx -> idx == -1)
        );
    }

    @Test
    public void shouldResolveIndex() {
        final CodePoints codePoints = CodePoints.of(
                IntStream.concat(
                        IntStream.range(100, 111),
                        IntStream.range(220, 231)
                )
        );

        assertEquals(-1, codePoints.indexOf(99));
        assertEquals(-1, codePoints.indexOf(11));

        assertEquals(-1, codePoints.indexOf(219));
        assertEquals(-1, codePoints.indexOf(231));

        assertEquals(0, codePoints.indexOf(100));
        assertEquals(10, codePoints.indexOf(110));

        assertEquals(11, codePoints.indexOf(220));
        assertEquals(21, codePoints.indexOf(230));

        assertEquals(3, codePoints.indexOf(99, 3));
        assertEquals(5, codePoints.indexOf(11, 5));

        assertEquals(7, codePoints.indexOf(219, 7));
        assertEquals(8, codePoints.indexOf(231, 8));
    }
}