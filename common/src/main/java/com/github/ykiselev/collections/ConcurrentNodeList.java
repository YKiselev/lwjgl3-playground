package com.github.ykiselev.collections;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * Simple double linked list with ability to add/remove list nodes (instead of values) and thus to use custom node class.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 * @deprecated this class is broken (at least remove operation is not thread-safe)
 */
@Deprecated
public final class ConcurrentNodeList<N extends ConcurrentNodeList.Node<N>> implements NodeList<N> {

    private volatile N head;

    private volatile N tail;

    private static final VarHandle HEAD;

    private static final VarHandle TAIL;

    private static final VarHandle PREV;

    private static final VarHandle NEXT;

    @Override
    public N head() {
        return head;
    }

    @Override
    public N tail() {
        return tail;
    }

    static {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            HEAD = lookup.findVarHandle(ConcurrentNodeList.class, "head", Node.class);
            TAIL = lookup.findVarHandle(ConcurrentNodeList.class, "tail", Node.class);
            PREV = lookup.findVarHandle(Node.class, "prev", Node.class);
            NEXT = lookup.findVarHandle(Node.class, "next", Node.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    @Override
    public N addFirst(N ref) {
        ref.prev = null;
        for (; ; ) {
            final N curHead = this.head;
            ref.next = curHead;
            if (curHead != null) {
                // curHead.prev = ref;
                if (!PREV.compareAndSet(curHead, null, ref)) {
                    continue;
                }
            }
            if (HEAD.compareAndSet(this, curHead, ref)) {
                break;
            }
        }
        TAIL.compareAndSet(this, null, ref);
        return ref;
    }

    @Override
    public N addLast(N ref) {
        ref.next = null;
        for (; ; ) {
            final N curTail = this.tail;
            ref.prev = curTail;
            if (curTail != null) {
                // curTail.next = ref;
                if (!NEXT.compareAndSet(curTail, null, ref)) {
                    continue;
                }
            }
            if (TAIL.compareAndSet(this, curTail, ref)) {
                break;
            }
        }
        HEAD.compareAndSet(this, null, ref);
        return ref;
    }

    @Override
    public N remove(N ref) {
        if (ref == null) {
            return null;
        }
        for (; ; ) {
            final N next = ref.next;
            final N prev = ref.prev;
            // We can't amend ref node's prev and next fields because that may break concurrent list traversals.
            // Instead we only re-route prev.next->next and next.prev->prev to bypass ref node.
            // Bypass ref node when iteration from head (previous.next->ref.next)
            if (next != null) {
                PREV.compareAndSet(next, ref, ref.prev);
            }
            // Bypass ref node when iteration from tail (next.prev->ref.prev)
            if (prev != null) {
                NEXT.compareAndSet(prev, ref, ref.next);
            }
            if (ref.next == next && ref.prev == prev) {
                if (prev != null && prev.next == ref) {
                    continue;
                }
                if (next != null && next.prev == ref) {
                    continue;
                }
                break;
            }
        }
        HEAD.compareAndSet(this, ref, ref.next);
        TAIL.compareAndSet(this, ref, ref.prev);
        return ref;
    }

    /**
     * Base node class without actual payload.
     *
     * @param <N> the type of node's implementing class
     */
    public static abstract class Node<N extends Node<N>> implements NodeList.Node<N> {

        volatile N next;

        volatile N prev;

        @Override
        public final N next() {
            return next;
        }

        @Override
        public final N prev() {
            return prev;
        }
    }

    /**
     * Immutable node class
     *
     * @param <V> the type of node's value
     */
    public static class ImmutableNode<V> extends Node<ImmutableNode<V>> {

        private final V value;

        public V value() {
            return value;
        }

        ImmutableNode(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "ImmutableNode{" +
                    "value=" + value +
                    '}';
        }
    }

}
