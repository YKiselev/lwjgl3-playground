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

package com.github.ykiselev.conversion;

import org.lwjgl.system.MemoryStack;

import java.math.BigInteger;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class DoubleConversionPerf {

    public static void main(String[] args) {
        run();
    }

    private static int[] toArray(IntBuffer b) {
        final int[] ints = new int[b.remaining()];
        b.get(ints);
        return ints;
    }
/*
    private static List<int[]> getData(int count) {
        final List<int[]> result = new ArrayList<>(count);
        final char[] tmp = new char[5 * 9];
        Arrays.fill(tmp, 0, tmp.length, '9');
        final String maxNum = new String(tmp);
        final BigInteger n = new BigInteger(maxNum).divide(BigInteger.valueOf(count));
        BigInteger v = BigInteger.ONE;
        for (int i = 0; i < count; i++) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                result.add(toArray(Unsigned.valueOf(v.toString(), stack)));
            }
            v = v.add(n);
        }
        return result;
    }
*/
    private static List<int[]> getData(int count) {
        final List<int[]> result = new ArrayList<>(count);
        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < count; i++) {
            final int[] ints = new int[rnd.nextInt(1, 6)];
            for (int j = 0; j < ints.length; j++) {
                ints[j] = rnd.nextInt();
            }
            result.add(ints);
        }
        return result;
    }

    private static void process(List<int[]> data) {
        //final long t0 = System.nanoTime();
        long count = 0, hash = 0;
        for (; count < 1_000_000_000L; count++) {
//            final long t1 = System.nanoTime();
//            if (TimeUnit.NANOSECONDS.toSeconds(t1 - t0) >= seconds) {
//                break;
//            }
            final int index = (int) (count % data.size());
            final int[] ints = data.get(index);
            try (MemoryStack stack = MemoryStack.stackPush()) {
                final IntBuffer buffer = stack.mallocInt(8);
                buffer.put(ints);
                buffer.flip();
                hash += buffer.hashCode();
            }
        }
        //final long millis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
        //final double speed = 1000.0 * count / millis;
        //System.out.println(count + " iterations in " + millis + " millis, speed (1/sec)=" + speed + ", hash=" + hash);
        System.out.println("hash=" + hash);
    }

    private static void run() {
        final List<int[]> data = getData(10_000);
        //System.out.println("Warm up...");
        for (int i = 0; i < 5; i++) {
            process(data);
        }
//        System.out.println("Iterations...");
//        for (int i = 0; i < 5; i++) {
//            process(data);
//        }
//        System.out.println("Bye!");
    }
}
