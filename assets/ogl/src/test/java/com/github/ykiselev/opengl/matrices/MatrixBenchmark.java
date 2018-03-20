package com.github.ykiselev.opengl.matrices;

import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MatrixBenchmark {

    public static final int WAIT_MSEC = 120_000;

    interface Operation {

        void apply(FloatBuffer a, FloatBuffer b, FloatBuffer c);
    }

    private final Operation op1 = Matrix::multiply;

    private final Operation op2 = Matrix::multiplyUsingLoops;

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
                Matrix.rotation(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), matrices[i]);
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
