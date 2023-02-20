package com.github.ykiselev.spi.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldTest {

    @Test
    void shouldPutAndGet() {
        final int globalRangeShift = 7;

        World world = new World(new DefaultNodeFactory(), globalRangeShift);

        final int maxIndex = (1 << globalRangeShift) - 1;

        world.put(0, 0, 0, 1);
        world.put(maxIndex - 10, maxIndex / 3, maxIndex / 5, 2);
        world.put(maxIndex - 9, maxIndex / 3, 1 + maxIndex / 5, 3);
        world.put(maxIndex, maxIndex / 2, maxIndex / 3, 4);

        assertEquals(1, world.get(0, 0, 0));
        assertEquals(2, world.get(maxIndex - 10, maxIndex / 3, maxIndex / 5));
        assertEquals(3, world.get(maxIndex - 9, maxIndex / 3, 1 + maxIndex / 5));
        assertEquals(4, world.get(maxIndex, maxIndex / 2, maxIndex / 3));

        assertNotNull(world.leafForIndices(0, 0, 0, false));
        assertNotNull(world.leafForIndices(maxIndex - 10, maxIndex / 3, maxIndex / 5, false));
        assertNotNull(world.leafForIndices(maxIndex - 9, maxIndex / 3, 1 + maxIndex / 5, false));
        assertNotNull(world.leafForIndices(maxIndex, maxIndex / 2, maxIndex / 3, false));

        assertNull(world.leafForIndices(0, 0, maxIndex, false));
        assertNotNull(world.leafForIndices(0, 0, maxIndex, true));

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