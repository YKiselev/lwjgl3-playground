package com.github.ykiselev.collections;

/**
 * Double linked list with ability to add/remove list nodes (instead of values) and thus to use custom node class.
 * Note: if not stated otherwise consecutive attempts to add already linked node will lead to undefined behaviour.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface NodeList<N extends NodeList.Node<N>> {

    N head();

    N tail();

    /**
     * Adds unlinked node to the head of the list
     *
     * @param ref the node
     * @return the linked node
     */
    N addFirst(N ref);

    /**
     * Adds unlinked node to the tail of the list
     *
     * @param ref the node
     * @return the linked node
     */
    N addLast(N ref);

    /**
     * Removes node from the list
     *
     * @param ref the node to remove
     * @return the unlinked node
     */
    N remove(N ref);

    /**
     * Clears list
     */
    default void clear() {
        N n;
        while ((n = tail()) != null) {
            remove(n);
        }
    }

    interface Node<N extends Node<N>> {

        N next();

        N prev();
    }

}
