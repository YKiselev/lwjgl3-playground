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

package com.github.ykiselev.common.memory.scrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
abstract class Scrap<T> {

    private final List<T> free = new ArrayList<>();

    private final List<T> allocated = new ArrayList<>();

    private int[] stack = new int[8];

    private int depth;

    final int free() {
        return free.size();
    }

    final int allocated() {
        return allocated.size();
    }

    final int depth() {
        return depth;
    }

    abstract T allocate(T reuse, int size);

    abstract void onPop(T item);

    final T allocate(int size) {
        if (depth == 0) {
            throw new IllegalStateException("Call push method first!");
        }
        final T result = allocate(getFree(), size);
        allocated.add(result);
        return result;
    }

    private T getFree() {
        return free.isEmpty() ? null : free.remove(free.size() - 1);
    }

    private void ensureStackSize() {
        if (depth + 1 == stack.length) {
            stack = Arrays.copyOf(stack, stack.length * 2);
        }
    }

    final void push() {
        ensureStackSize();
        stack[depth++] = allocated.size();
    }

    final void pop() {
        if (depth <= 0) {
            throw new IllegalStateException("Nothing to pop!");
        }
        final int downTo = stack[--depth];
        stack[depth] = 0;
        if (downTo > allocated.size()) {
            throw new IllegalStateException("Unable to pop!");
        }
        freeAllocated(downTo);
    }

    private void freeAllocated(int downTo){
        for (int i = allocated.size() - 1; i >= downTo; i--) {
            final T item = allocated.remove(i);
            free.add(item);
            onPop(item);
        }
    }
}