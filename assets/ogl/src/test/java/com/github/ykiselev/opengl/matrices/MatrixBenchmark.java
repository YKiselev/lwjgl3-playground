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

package com.github.ykiselev.opengl.matrices;

import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.ykiselev.opengl.matrices.MathKt.*;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MatrixBenchmark {

    public static final int WAIT_MSEC = 120_000;

    interface Operation {

        void apply(FloatBuffer a, FloatBuffer b, FloatBuffer c);
    }

    private final Operation op1 = MatrixOps.Companion::multiply;


    public static void main(String[] args) {
        new MatrixBenchmark().run();
    }

    private void run() {
        final long t0 = System.currentTimeMillis();
        long ops = 0;
        final FloatBuffer[] matrices = new FloatBuffer[32];
        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        final Operation op = op1;
        try (MemoryStack ms = MemoryStack.stackPush()) {
            for (int i = 0; i < matrices.length; i++) {
                matrices[i] = ms.mallocFloat(16);
                MatrixOps.Companion.rotation(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), matrices[i]);
            }
            System.out.println("Starting benchmark, please wait " + (WAIT_MSEC / 1000) + " seconds...");
            while (!Thread.currentThread().isInterrupted()) {
                final long delta = System.currentTimeMillis() - t0;
                if (delta > WAIT_MSEC) {
                    System.out.println("ops: " + ops + " in " + delta + " msec, speed is (ops/sec): " + (1000.0 * ops / delta));
                    break;
                }
                FloatBuffer a = matrices[rnd.nextInt(0, matrices.length)], b = null, c = null;
                while (b == null || c == null) {
                    b = matrices[rnd.nextInt(0, matrices.length)];
                    if (b == a) {
                        b = null;
                        continue;
                    }
                    c = matrices[rnd.nextInt(0, matrices.length)];
                    if (c == a || c == b) {
                        c = null;
                    }
                }
                op.apply(a, b, c);
                ops++;
            }
        }
    }

}
