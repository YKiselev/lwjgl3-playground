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

package com.github.ykiselev.common.conversion;

import com.github.ykiselev.common.memory.scrap.ScrapMemory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class PowersOfFiveTest {

    private static final BigInteger FIVE = BigInteger.valueOf(5);

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 107, 503, 911, 1022, 1074})
    public void shouldReturnProperValue(int exp) {
        try (ScrapMemory scrap = new ScrapMemory(64, 64).push()) {
            assertEquals(
                    FIVE.pow(exp).toString(10),
                    Unsigned.toString(PowersOfFive.valueOf(exp, scrap), scrap)
            );
        }
    }
}