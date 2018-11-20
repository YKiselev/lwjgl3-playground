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

package com.github.ykiselev.memory.scrap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ScrapMemoryTest {

    private final byte[] data = new byte[2000];

    @BeforeEach
    void setUp() {
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
    }

    private static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        final Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (Unsafe) field.get(null);
    }

    @Test
    void shouldSomething() throws Exception {
        VarHandle vh = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.nativeOrder());
        for (int i = 0; i < 10; i++) {
            int v = (int) vh.get(data, i);
            System.out.println("v[" + i + "]=" + v);
        }

        // copy memory?
        //Unsafe unsafe = getUnsafe();
        int[] ints = new int[8];
        //unsafe.copyMemory(data, 8, ints, 0, 8 * 4);

        //System.arraycopy(data,8, ints, 0, 8*4);

        System.out.println(Arrays.toString(ints));
    }
}