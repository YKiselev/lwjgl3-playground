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
package com.github.ykiselev.common.closeables

import com.github.ykiselev.common.closeables.Closeables.compose
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class CloseablesTest {

    private val ac1 = mock<AutoCloseable>()
    private val ac2 = mock<AutoCloseable>()
    private val ac3 = mock<AutoCloseable>()
    private val ac = compose(ac1, ac2, ac3)

    @Test
    fun shouldCallInDirectOrder() {
        ac.close()
        val inOrder = inOrder(ac1, ac2, ac3)
        inOrder.verify(ac1).close()
        inOrder.verify(ac2).close()
        inOrder.verify(ac3).close()
    }

    @Test
    fun shouldCallInDirectOrderAfterAdd() {
        compose(ac1).and(ac2).and(ac3).close()
        val inOrder = inOrder(ac1, ac2, ac3)
        inOrder.verify(ac1).close()
        inOrder.verify(ac2).close()
        inOrder.verify(ac3).close()
    }

    @Test
    fun shouldCallInReversedOrder() {
        ac.reverse().close()
        val inOrder = inOrder(ac1, ac2, ac3)
        inOrder.verify(ac3).close()
        inOrder.verify(ac2).close()
        inOrder.verify(ac1).close()
    }

    @Test
    fun shouldCallInReversedOrderAfterAdd() {
        compose(ac1).and(ac2).and(ac3).reverse().close()
        val inOrder = inOrder(ac1, ac2, ac3)
        inOrder.verify(ac3).close()
        inOrder.verify(ac2).close()
        inOrder.verify(ac1).close()
    }

}