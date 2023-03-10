package com.github.ykiselev.base.game.client;

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.materials.Material;
import com.github.ykiselev.opengl.materials.MaterialAtlas;
import com.github.ykiselev.opengl.models.Block;
import com.github.ykiselev.spi.camera.Frustum;
import com.github.ykiselev.spi.camera.FrustumClippingPredicate;
import com.github.ykiselev.spi.world.Leaf;
import com.github.ykiselev.spi.world.Visitor;
import com.github.ykiselev.spi.world.World;
import com.github.ykiselev.spi.world.predicates.CountingPredicate;

import java.nio.FloatBuffer;

public final class WorldRenderer implements AutoCloseable {

    private final Frustum frustum = new Frustum();

    private final FrustumClippingPredicate frustumClippingPredicate = new FrustumClippingPredicate(frustum);

    private final CountingPredicate countingPredicate = new CountingPredicate(frustumClippingPredicate);

    private final MaterialAtlas materialAtlas;

    private final Block block;

    private final AutoCloseable ac;

    public WorldRenderer(Assets assets) {
        try (var guard = Closeables.newGuard()) {
            block = guard.add(new Block(assets, 50_000));
            materialAtlas = guard.add(assets.load("materials/materials.conf", OglRecipes.MATERIAL_ATLAS));

            ac = guard.detach();
        }
    }

    @Override
    public void close() {
        Closeables.close(ac);
    }

    public void draw(World world, FloatBuffer vp) {
        if (world == null) {
            return;
        }

//        try (MemoryStack ms = MemoryStack.stackPush()) {
//            final FloatBuffer mat = ms.mallocFloat(16);
//            Matrix.inverse(vp, mat);
//        }
        frustum.setFromMatrix(vp);


        //glActiveTexture(GL_TEXTURE0);
        materialAtlas.texture().bind();
        block.begin(vp, materialAtlas.sScale(), materialAtlas.tScale());

        final float blockSize = 1f;
        frustumClippingPredicate.blockSize(blockSize);
        countingPredicate.reset();

        world.visit(frustumClippingPredicate, new Visitor() {

            @Override
            public void visit(Leaf leaf) {
                leaf.visit(data -> {
                    float x0 = leaf.iorg() * blockSize;
                    float y0 = leaf.jorg() * blockSize;
                    float z0 = leaf.korg() * blockSize;
                    int i = 0, j = 0, k = 0;
                    float x = x0, y = y0, z = z0;
                    for (int idx = 0; idx < data.length; idx++, i++, x += blockSize) {
                        if (i > 15) {
                            i = 0;
                            j++;
                            x = x0;
                            y += blockSize;
                        }
                        if (j > 15) {
                            j = 0;
                            k++;
                            y = y0;
                            z += blockSize;
                        }
                        if (k > 15) {
                            throw new IndexOutOfBoundsException(idx);
                        }

                        Material material = materialAtlas.get(data[idx]);
                        if (material != null) {
                            block.draw(x, y, z, material.ds(), material.dt());
                        }
                    }
                });
            }
        });

        block.end();
        materialAtlas.texture().unbind();
    }

    public String formatStats() {
        return String.format("inst: %d, nrej: %d, npass: %d, lrej: %d, lpass: %d",
                block.totalInstances(),
                countingPredicate.nodesRejected(),
                countingPredicate.nodesPassed(),
                countingPredicate.leafsRejected(),
                countingPredicate.leafsPassed());
    }
}
