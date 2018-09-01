package com.github.ykiselev.collections;

import com.github.ykiselev.collections.ConcurrentNodeList.ImmutableNode;
import com.github.ykiselev.test.ParallelRunner;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ConcurrentNodeListTest {

    private final NodeList<ImmutableNode<Integer>> list = new ConcurrentNodeList<>();

    @Test
    void shouldAddFirst() {
        ImmutableNode<Integer> node = list.addFirst(new ImmutableNode<>(1));
        assertEquals(node, list.head());
        assertEquals(node, list.tail());
    }

    @Test
    void shouldAddLast() {
        ImmutableNode<Integer> node = list.addLast(new ImmutableNode<>(2));
        assertEquals(node, list.head());
        assertEquals(node, list.tail());
    }

    @Test
    void shouldRemove() {
        assertNull(list.head());
        assertNull(list.tail());
        ImmutableNode<Integer> n1 = list.addFirst(new ImmutableNode<>(1));
        ImmutableNode<Integer> n2 = list.addFirst(new ImmutableNode<>(2));
        ImmutableNode<Integer> n3 = list.addFirst(new ImmutableNode<>(3));
        assertEquals(n3, list.head());
        assertEquals(n1, list.tail());
        list.remove(n1);
        list.remove(n3);
        assertEquals(n2, list.head());
        assertEquals(n2, list.tail());
    }

    private int countForward() {
        int k = 0;
        ImmutableNode<Integer> node = list.head();
        while (node != null) {
            k++;
            node = node.next();
        }
        return k;
    }

    private int countBackward() {
        int k = 0;
        ImmutableNode<Integer> node = list.tail();
        while (node != null) {
            k++;
            node = node.prev();
        }
        return k;
    }

    @RepeatedTest(100)
    void addShouldBeThreadSafe() throws Exception {
        final AtomicInteger seq = new AtomicInteger();
        final Supplier<Callable<Collection<ImmutableNode<Integer>>>> s = () -> () -> {
            final List<ImmutableNode<Integer>> nodes = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                nodes.add(
                        list.addFirst(
                                new ImmutableNode<>(
                                        seq.incrementAndGet()
                                )
                        )
                );
                nodes.add(
                        list.addLast(
                                new ImmutableNode<>(
                                        seq.incrementAndGet()
                                )
                        )
                );
            }
            return nodes;
        };
        final List<ImmutableNode<Integer>> nodes = ParallelRunner.fromCallable(10, s)
                .call()
                .stream()
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        final int forward = countForward();
        assertEquals(nodes.size(), forward);
        assertEquals(forward, countBackward());
    }

    @RepeatedTest(100)
    void removeShouldBeThreadSafe() throws Exception {
        final List<ImmutableNode<Integer>> nodes = IntStream.range(0, 5_000)
                .boxed()
                .map(ImmutableNode::new)
                .collect(Collectors.toList());
        nodes.forEach(list::addFirst);
        Collections.shuffle(nodes);
        final Supplier<Runnable> s = () -> () -> {
            ThreadLocalRandom rnd = ThreadLocalRandom.current();
            while (list.head() != null) {
                list.remove(
                        nodes.get(
                                rnd.nextInt(0, nodes.size())
                        )
                );
            }
        };
        ParallelRunner.fromRunnable(1, s)
                .call();
        final int forward = countForward();
        final int backward = countBackward();
        assertEquals(0, forward, "List should be empty!");
        assertEquals(forward, backward, "Tail <> Head!");
    }

    @RepeatedTest(100)
    void compareWithConcurrentLinkedDeque() throws Exception {
        final ConcurrentLinkedDeque<Object> deque = new ConcurrentLinkedDeque<>();
        final List<Integer> nodes = IntStream.range(0, 5_000)
                .boxed()
                .collect(Collectors.toList());
        nodes.forEach(deque::addFirst);
        Collections.shuffle(nodes);
        final Supplier<Runnable> s = () -> () -> {
            ThreadLocalRandom rnd = ThreadLocalRandom.current();
            while (!deque.isEmpty()) {
                deque.remove(
                        nodes.get(
                                rnd.nextInt(0, nodes.size())
                        )
                );
            }
        };
        ParallelRunner.fromRunnable(1, s)
                .call();
        assertTrue(deque.isEmpty(), "Deque should be empty!");
    }

}