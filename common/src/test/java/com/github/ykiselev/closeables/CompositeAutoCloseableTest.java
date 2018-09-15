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

package com.github.ykiselev.closeables;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.inOrder;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CompositeAutoCloseableTest {

    private final AutoCloseable ac1 = Mockito.mock(AutoCloseable.class);

    private final AutoCloseable ac2 = Mockito.mock(AutoCloseable.class);

    private final AutoCloseable ac3 = Mockito.mock(AutoCloseable.class);

    private final CompositeAutoCloseable ac = new CompositeAutoCloseable(ac1, ac2, ac3);

    @Test
    void shouldCallInDirectOrder() throws Exception {
        ac.close();
        InOrder inOrder = inOrder(ac1, ac2, ac3);
        inOrder.verify(ac1).close();
        inOrder.verify(ac2).close();
        inOrder.verify(ac3).close();
    }

    @Test
    void shouldCallInReversedOrder() throws Exception {
        ac.reverse().close();
        InOrder inOrder = inOrder(ac1, ac2, ac3);
        inOrder.verify(ac3).close();
        inOrder.verify(ac2).close();
        inOrder.verify(ac1).close();
    }
}