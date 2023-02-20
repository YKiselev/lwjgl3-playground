package com.github.ykiselev.spi.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NormalNodeTest {

    private final Node node1 = mock(Node.class);

    private final NodeFactory factory = mock(NodeFactory.class);

    private final NormalNode block = new NormalNode().init(0, 0, 0, 1, 4);

    @BeforeEach
    void setUp() {
        when(factory.create(anyInt(), anyInt(), anyInt(), eq(1), eq(3))).thenReturn(node1);
    }

    @Test
    void shouldReturnZeroForNonExistingIndex() {
        assertEquals(0, block.get(0, 0, 0));
    }

    @Test
    void shouldPut() {
        block.put(0, 0, 0, 1, factory);
        verify(node1, times(1)).put(0, 0, 0, 1, factory);
        verify(factory, times(1)).create(0, 0, 0, 1, 3);

        block.put(15, 0, 0, 2, factory);
        verify(node1, times(1)).put(15, 0, 0, 2, factory);
        verify(factory, times(1)).create(8, 0, 0, 1, 3);

        block.put(1, 15, 0, 3, factory);
        verify(node1, times(1)).put(1, 15, 0, 3, factory);
        verify(factory, times(1)).create(0, 8, 0, 1, 3);

        block.put(1, 2, 15, 4, factory);
        verify(node1, times(1)).put(1, 2, 15, 4, factory);
        verify(factory, times(1)).create(0, 0, 8, 1, 3);

        block.put(15, 15, 15, 5, factory);
        verify(node1, times(1)).put(15, 15, 15, 5, factory);
        verify(factory, times(1)).create(8, 8, 8, 1, 3);
    }
}