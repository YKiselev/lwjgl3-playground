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

package com.github.ykiselev.services.commands;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class is a collection of command handler adaptors.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class Handlers {

    private static void assertArgs(List<String> args, int max) {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("At least one argument is expected!");
        }
        if (args.size() > max) {
            throw new IllegalArgumentException("Too many arguments!");
        }
    }

    static Consumer<List<String>> consumer(Runnable handler) {
        return args -> {
            assertArgs(args, 1);
            handler.run();
        };
    }

    static Consumer<List<String>> consumer(Commands.H1 handler) {
        return args -> {
            assertArgs(args, 1);
            handler.handle(args.get(0));
        };
    }

    static Consumer<List<String>> consumer(Commands.H2 handler) {
        return args -> {
            assertArgs(args, 2);
            String a2 = null;
            if (args.size() > 1) {
                a2 = args.get(1);
            }
            handler.handle(args.get(0), a2);
        };
    }

    static Consumer<List<String>> consumer(Commands.H3 handler) {
        return args -> {
            assertArgs(args, 3);
            String a2 = null, a3 = null;
            if (args.size() > 1) {
                a2 = args.get(1);
                if (args.size() > 2) {
                    a3 = args.get(2);
                }
            }
            handler.handle(args.get(0), a2, a3);
        };
    }

    static Consumer<List<String>> consumer(Commands.H4 handler) {
        return args -> {
            assertArgs(args, 4);
            String a2 = null, a3 = null, a4 = null;
            if (args.size() > 1) {
                a2 = args.get(1);
                if (args.size() > 2) {
                    a3 = args.get(2);
                    if (args.size() > 3) {
                        a4 = args.get(3);
                    }
                }
            }
            handler.handle(args.get(0), a2, a3, a4);
        };
    }

    static Consumer<List<String>> consumer(Commands.H5 handler) {
        return args -> {
            assertArgs(args, 5);
            String a2 = null, a3 = null, a4 = null, a5 = null;
            if (args.size() > 1) {
                a2 = args.get(1);
                if (args.size() > 2) {
                    a3 = args.get(2);
                    if (args.size() > 3) {
                        a4 = args.get(3);
                        if (args.size() > 4) {
                            a5 = args.get(4);
                        }
                    }
                }
            }
            handler.handle(args.get(0), a2, a3, a4, a5);
        };
    }

    static Consumer<List<String>> consumer(Commands.H6 handler) {
        return args -> {
            assertArgs(args, 6);
            String a2 = null, a3 = null, a4 = null, a5 = null, a6 = null;
            if (args.size() > 1) {
                a2 = args.get(1);
                if (args.size() > 2) {
                    a3 = args.get(2);
                    if (args.size() > 3) {
                        a4 = args.get(3);
                        if (args.size() > 4) {
                            a5 = args.get(4);
                            if (args.size() > 5) {
                                a6 = args.get(5);
                            }
                        }
                    }
                }
            }
            handler.handle(args.get(0), a2, a3, a4, a5, a6);
        };
    }
}
