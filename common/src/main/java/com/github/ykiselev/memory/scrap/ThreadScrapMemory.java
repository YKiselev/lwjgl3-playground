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
 * Thread local holder of instance of {@link ScrapMemory}.
 * Note: it is user's responsibility to set instance of {@link ScrapMemory} prior to calling {@link ThreadScrapMemory#push()}!
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ThreadScrapMemory {

    private static final ThreadLocal<ScrapMemory> TLS = new ThreadLocal<>();

    public static void set(ScrapMemory scrap){
        TLS.set(scrap);
    }

    /**
     * Retrieves this thread's instance of {@link ScrapMemory} (creates new instance if current thread doesn't have one),
     * calls it's {@link ScrapMemory#push()} method and then returns retrieved instance.
     *
     * @return the thread-local scrap memory instance
     */
    public static ScrapMemory push() {
        final ScrapMemory result = TLS.get();
        if (result == null) {
            throw new IllegalStateException("You should set instance of " + ScrapMemory.class.getSimpleName() + " before using it!");
        }
        result.push();
        return result;
    }
}
