package com.github.ykiselev.spi.world.predicates;

import com.github.ykiselev.spi.world.Leaf;
import com.github.ykiselev.spi.world.Node;
import com.github.ykiselev.spi.world.NodePredicate;
import com.github.ykiselev.spi.world.NormalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DebugPredicate implements NodePredicate {

    private record Level(Node node, boolean flag) {

    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final NodePredicate delegate;

    private final List<Level> branch = new ArrayList<>();

    public DebugPredicate(NodePredicate delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public boolean test(NormalNode node) {
        var r = delegate.test(node);
        branch.add(new Level(node, r));
        return true;
    }

    @Override
    public boolean test(Leaf leaf) {
        var r = delegate.test(leaf);
        branch.add(new Level(leaf, r));
        long count = branch.stream().filter(Level::flag).count();
        if (r && count != branch.size()) {
            logger.info("Branch {}", branch);
        }
        return r;
    }

    @Override
    public void beforeChild() {

    }

    @Override
    public void afterChild() {
        branch.remove(branch.size() - 1);
    }
}
