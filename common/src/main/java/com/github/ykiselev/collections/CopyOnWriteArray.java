package com.github.ykiselev.collections;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 * @deprecated not generic enough
 */
@Deprecated
public final class CopyOnWriteArray<T> {

    private volatile T[] items;

    private static final VarHandle handle;

    static {
        try {
            handle = MethodHandles.lookup()
                    .findVarHandle(CopyOnWriteArray.class, "items", Object[].class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public CopyOnWriteArray(T[] items) {
        this.items = items.clone();
    }

    /**
     * Adds new element to array if there is no such element (elements are compared by reference).
     *
     * @param item the item to add to the end of array
     * @return the handle which may be used to remove item later
     */
    public AutoCloseable add(T item) {
        for (; ; ) {
            final T[] prevArray = array();
            final int existing = indexOf(prevArray, item);
            if (existing >= 0) {
                return handle(item);
            }
            final T[] newArray = Arrays.copyOf(prevArray, prevArray.length + 1);
            newArray[prevArray.length] = item;
            if (handle.compareAndSet(this, prevArray, newArray)) {
                return handle(item);
            }
        }
    }

    private AutoCloseable handle(T handler) {
        return () -> remove(handler);
    }

    private void remove(T handler) {
        for (; ; ) {
            final T[] prevArray = array();
            final int idx = indexOf(prevArray, handler);
            if (idx < 0) {
                break;
            }
            final T[] newArray = Arrays.copyOf(prevArray, prevArray.length - 1);
            if (idx < newArray.length) {
                System.arraycopy(prevArray, idx + 1, newArray, idx, newArray.length - idx);
            }
            if (handle.compareAndSet(CopyOnWriteArray.this, prevArray, newArray)) {
                break;
            }
        }
    }

    private static <T> int indexOf(T[] array, T value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public T[] array() {
        return items;
    }
}
