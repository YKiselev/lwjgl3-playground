/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
