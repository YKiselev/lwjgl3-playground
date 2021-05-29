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

package com.github.ykiselev.common.recursion;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 10.05.2019
 */
public class RecursionTest {

    private Recursion.Call<Integer> calculate(int num, int accumulator) {
        if (num == 0) {
            return Recursion.done(accumulator);
        }
        return Recursion.call(() -> calculate(num - 1, accumulator + 1));
    }

    @Test
    public void shouldEmulateTailRecursion() {
        assertEquals(100_000, (int) Recursion.invoke(calculate(100_000, 0)).orElse(0));
        //jmap();
    }

    private static int jmap() throws IOException, InterruptedException {
        final String name = ManagementFactory.getRuntimeMXBean().getName();
        final String pid = name.substring(0, name.indexOf("@"));
        final Process process = new ProcessBuilder("jmap.exe", "-histo", pid)
                .redirectOutput(new File("d:\\jmap.txt"))
                .start();
        return process.waitFor();
    }
}
