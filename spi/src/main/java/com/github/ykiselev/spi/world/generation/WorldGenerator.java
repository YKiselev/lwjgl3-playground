package com.github.ykiselev.spi.world.generation;

import com.github.ykiselev.spi.world.DefaultNodeFactory;
import com.github.ykiselev.spi.world.World;

public final class WorldGenerator {

    public World generate(int dimSize) {
        World world = new World(new DefaultNodeFactory(), Integer.numberOfTrailingZeros(dimSize));

        for (int k = 0; k < Math.min(8, dimSize / 8); k++) {
            for (int j = 0; j < dimSize; j++) {
                for (int i = 0; i < dimSize; i++) {
                    world.put(i, j, k, 1);
                }
            }
        }
        return world;
    }
}
