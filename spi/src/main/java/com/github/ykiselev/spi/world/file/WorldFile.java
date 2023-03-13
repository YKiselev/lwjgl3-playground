package com.github.ykiselev.spi.world.file;

import com.github.ykiselev.spi.services.FileSystem;
import com.github.ykiselev.spi.world.Leaf;
import com.github.ykiselev.spi.world.NodeFactory;
import com.github.ykiselev.spi.world.NodePredicate;
import com.github.ykiselev.spi.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public final class WorldFile {

    private static final short FILE_VERSION = 1;

    private static final byte[] FILE_SIGNATURE = new byte[]{'w', 'r', 'l', 'd'};

    static final byte[] LEAF_SIGNATURE = new byte[]{'l', 'e', 'a', 'f'};
    public static final int FILE_HEADER_SIZE = 4 + 2 + 1 + 1;
    public static final int LEAF_CHUNK_SIZE = 4 + 4 + 4 + 4 + (1 << (3 * Leaf.SIDE_SHIFT));

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SaveLeafVisitor visitor = new SaveLeafVisitor();

    private final ByteBuffer buffer = ByteBuffer.allocate(8 * 1024)
            .order(ByteOrder.LITTLE_ENDIAN);

    private String path(String name) {
        return "universe/" + name;
    }

    private static void write(FileChannel file, ByteBuffer buf) {
        final int remaining = buf.remaining();
        final int written;
        try {
            written = file.write(buf);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (written != remaining) {
            throw new RuntimeException("Incomplete write (" + written + " of " + remaining + " bytes)!");
        }
    }

    private static void read(FileChannel file, ByteBuffer buf) {
        final int remaining = buf.remaining();
        try {
            final int read = file.read(buf);
            if (read != remaining) {
                throw new RuntimeException("Incomplete read (" + read + " of " + remaining + " bytes)!");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    public void save(FileSystem fileSystem, World world, String name) {
        try (FileSystem zipFs = fileSystem.mapArchive(path(name), true, false)) {
            try (FileChannel file = zipFs.truncate("world")) {
                buffer.clear();
                buffer.put(FILE_SIGNATURE)
                        .putShort(FILE_VERSION)
                        .put(world.rangeShift())
                        .put(world.leafRangeShift())
                        .flip();
                write(file, buffer);

                visitor.init(buf -> write(file, buf), buffer);
                world.visit(NodePredicate.DEFAULT, visitor);

                logger.info("World \"{}\" has been saved.", name);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public static void checkSignature(ByteBuffer buf, byte[] expected) {
        for (byte b : expected) {
            byte actual = buf.get();
            if (b != actual) {
                throw new RuntimeException("Signature mismatch! Need " + (char) b + " but got " + (char) actual);
            }
        }
    }

    public World load(FileSystem fileSystem, String name, NodeFactory factory) {
        try (FileSystem zipFs = fileSystem.mapArchive(path(name), false, true)) {
            try (FileChannel file = zipFs.open("world", StandardOpenOption.READ)) {
                buffer.clear().limit(FILE_HEADER_SIZE);
                read(file, buffer);
                buffer.flip();
                checkSignature(buffer, FILE_SIGNATURE);
                final short version = buffer.getShort();
                if (version <= 0 || version > FILE_VERSION) {
                    throw new RuntimeException("Unsupported version: " + version);
                }
                final byte rangeShift = buffer.get();
                final byte leafRangeShift = buffer.get();
                if (leafRangeShift != Leaf.SIDE_SHIFT) {
                    throw new RuntimeException("Incompatible leaf range shift: " + leafRangeShift);
                }
                final World world = new World(factory, rangeShift);
                buffer.clear().limit(LEAF_CHUNK_SIZE);
                int leafs = 0;
                while (file.read(buffer) == buffer.limit()) {
                    buffer.flip();
                    checkSignature(buffer, LEAF_SIGNATURE);
                    final int iorg = buffer.getInt();
                    final int jorg = buffer.getInt();
                    final int korg = buffer.getInt();
                    final Leaf leaf = world.leafForIndices(iorg, jorg, korg, true);
                    leaf.visit(buffer::get);
                    leafs++;
                    buffer.clear().limit(LEAF_CHUNK_SIZE);
                }
                logger.info("World \"{}\" has been loaded ({} leafs).", name, leafs);
                return world;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
