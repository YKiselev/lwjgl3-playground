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
 * Fast on-heap array allocator. Each instance should be used from single thread. Allocated arrays are freed upon call to {@link ScrapMemory#close()}.
 * This class is gc-free after initial allocation of required amount of reusable arrays.
 * <p>
 * Note: this class is not thread-safe!
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ScrapMemory implements AutoCloseable {

    private final Scrap<? extends ByteArray> byteArrayScrap;

    private final Scrap<? extends IntArray> intArrayScrap;

    private final Scrap[] scraps;

    /**
     * @param byteSize the size (in KB) of byte array scrap
     * @param intSize  the size (in KB) of int array scrap
     */
    public ScrapMemory(int byteSize, int intSize) {
        this.byteArrayScrap = new ByteArrayScrap(byteSize * 1024);
        this.intArrayScrap = new IntArrayScrap(intSize * 1024);
        this.scraps = new Scrap[]{byteArrayScrap, intArrayScrap};
    }

    public IntArray allocateInts(int size) {
        return intArrayScrap.allocate(size);
    }

    public ByteArray allocate(int size) {
        return byteArrayScrap.allocate(size);
    }

    @SuppressWarnings({"ForLoopReplaceableByForEach"})
    public void push() {
        for (int i = 0; i < scraps.length; i++) {
            scraps[i].push();
        }
    }

    @SuppressWarnings({"ForLoopReplaceableByForEach"})
    public void pop() {
        for (int i = 0; i < scraps.length; i++) {
            scraps[i].pop();
        }
    }

    @Override
    public void close() {
        pop();
    }
}
