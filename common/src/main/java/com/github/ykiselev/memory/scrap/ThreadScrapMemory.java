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

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ThreadScrapMemory {

    public static final String BYTE_ARRAY__SIZE = "scrap.memory.byteArraySize";

    public static final String INT_ARRAY__SIZE = "scrap.memory.intArraySize";

    private static final ThreadLocal<ScrapMemory> TLS = ThreadLocal.withInitial(ThreadScrapMemory::newInstance);

    private static ScrapMemory newInstance() {
        return new ScrapMemory(
                getSize(BYTE_ARRAY__SIZE, 32),
                getSize(INT_ARRAY__SIZE, 32)
        );
    }

    private static int getSize(String name, int defValue) {
        final String property = System.getProperty(name);
        int result = defValue;
        if (property != null) {
            result = Integer.parseUnsignedInt(property);
        }
        return result * 1024;
    }

    /**
     * Retrieves this thread's instance of {@link ScrapMemory} (creates new instance if current thread doesn't have one),
     * calls it's {@link ScrapMemory#push()} method and then returns retrieved instance.
     *
     * @return the thread-local scrap memory instance
     */
    public static ScrapMemory push() {
        final ScrapMemory result = TLS.get();
        result.push();
        return result;
    }
}
