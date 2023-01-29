package com.github.ykiselev.spi.world;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorldTest {

    @Test
    void shouldPutAndGet() {
        World world = new World(10);

        world.put(0, 0, 0, 1);
        world.put(901, 333, 105, 2);
        world.put(902, 334, 106, 3);
        world.put(1023, 511, 255, 4);

        assertEquals(1, world.get(0, 0, 0));
        assertEquals(2, world.get(901, 333, 105));
        assertEquals(3, world.get(902, 334, 106));
        assertEquals(4, world.get(1023, 511, 255));
/*
        // 13 seconds, Nodes: 513, leaves: 262144
        for (int k = 0; k < 1024; k++) {
            for (int j = 0; j < 1024; j++) {
                for (int i = 0; i < 1024; i++) {
                    world.put(i, j, k, i & 255);
                }
            }
        }
        var leaves = new AtomicLong();
        var nodes = new AtomicLong();
        world.visit(NodePredicate.DEFAULT, new Visitor() {
            @Override
            public void visit(NormalNode node) {
                //System.out.println("Node: " + node);
                nodes.incrementAndGet();
            }

            @Override
            public void visit(Leaf leaf) {
                //System.out.println("Leaf: " + leaf);
                leaves.incrementAndGet();
            }
        });
        System.out.println("Nodes: " + nodes.get() + ", leaves: " + leaves.get());*/
    }
}