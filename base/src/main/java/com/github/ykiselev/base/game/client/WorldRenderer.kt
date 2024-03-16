package com.github.ykiselev.base.game.client

import com.github.ykiselev.assets.Assets
import com.github.ykiselev.common.closeables.Closeables.close
import com.github.ykiselev.common.closeables.Closeables.newGuard
import com.github.ykiselev.opengl.OglRecipes
import com.github.ykiselev.opengl.materials.MaterialAtlas
import com.github.ykiselev.opengl.models.Block
import com.github.ykiselev.spi.camera.Frustum
import com.github.ykiselev.spi.camera.FrustumClippingPredicate
import com.github.ykiselev.spi.world.Leaf
import com.github.ykiselev.spi.world.Visitor
import com.github.ykiselev.spi.world.World
import com.github.ykiselev.spi.world.predicates.CountingPredicate
import java.nio.FloatBuffer

class WorldRenderer(assets: Assets) : AutoCloseable {

    private val frustum = Frustum()

    private val frustumClippingPredicate = FrustumClippingPredicate(frustum)

    private val countingPredicate = CountingPredicate(frustumClippingPredicate)

    private var materialAtlas: MaterialAtlas? = null

    private var block: Block? = null

    private var ac: AutoCloseable? = null

    init {
        newGuard().use { guard ->
            block = guard.add(Block(assets, 50000))
            materialAtlas = guard.add(assets.load("materials/materials.conf", OglRecipes.MATERIAL_ATLAS))
            ac = guard.detach()
        }
    }

    override fun close() {
        close(ac!!)
    }

    fun draw(world: World?, vp: FloatBuffer?) {
        if (world == null) {
            return
        }

        //        try (MemoryStack ms = MemoryStack.stackPush()) {
//            final FloatBuffer mat = ms.mallocFloat(16);
//            Matrix.inverse(vp, mat);
//        }
        frustum.setFromMatrix(vp)

        //glActiveTexture(GL_TEXTURE0);
        materialAtlas!!.texture().bind()
        block!!.begin(vp, materialAtlas!!.sScale(), materialAtlas!!.tScale())

        val blockSize = 1f
        frustumClippingPredicate.blockSize(blockSize)
        countingPredicate.reset()

        world.visit(countingPredicate, object : Visitor {
            override fun visit(leaf: Leaf) {
                leaf.visit { data: ByteArray ->
                    val x0 = leaf.iorg() * blockSize
                    val y0 = leaf.jorg() * blockSize
                    val z0 = leaf.korg() * blockSize
                    var i = 0
                    var j = 0
                    var k = 0
                    var x = x0
                    var y = y0
                    var z = z0
                    var idx = 0
                    while (idx < data.size) {
                        if (i > 15) {
                            i = 0
                            j++
                            x = x0
                            y += blockSize
                        }
                        if (j > 15) {
                            j = 0
                            k++
                            y = y0
                            z += blockSize
                        }
                        if (k > 15) {
                            throw IndexOutOfBoundsException(idx)
                        }

                        val material = materialAtlas!![data[idx].toInt()]
                        if (material != null) {
                            block!!.draw(x, y, z, material.ds, material.dt)
                        }
                        idx++
                        i++
                        x += blockSize
                    }
                }
            }
        })

        block!!.end()
        materialAtlas!!.texture().unbind()
    }

    fun formatStats(): String {
        return String.format(
            "inst: %d, nrej: %d, npass: %d, lrej: %d, lpass: %d",
            block!!.totalInstances(),
            countingPredicate.nodesRejected(),
            countingPredicate.nodesPassed(),
            countingPredicate.leafsRejected(),
            countingPredicate.leafsPassed()
        )
    }
}
