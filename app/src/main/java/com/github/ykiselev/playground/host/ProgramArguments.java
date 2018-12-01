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

package com.github.ykiselev.playground.host;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Supported program arguments includes:
 * <ul>
 * <li>Base folder, containing base mod assets (read-only)</li>
 * <li>User folder, to store persisted configs (writable)</li>
 * </ul>
 * </pre>
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ProgramArguments {

    private final String[] args;

    public ProgramArguments(String[] args) {
        this.args = args.clone();
    }

    /**
     * Searches arguments for key=value pair.
     *
     * @param key the key name
     * @return the value or {@code null}
     */
    private Optional<String> value(String key) {
        for (String arg : args) {
            if (arg != null) {
                final int k = arg.indexOf("=");
                if (k >= 0) {
                    if (key.equals(arg.substring(0, k))) {
                        return Optional.of(
                                arg.substring(k + 1)
                        );
                    }
                }
            }
        }
        return Optional.empty();
    }

    private boolean hasSwitch(String key) {
        for (String arg : args) {
            if (arg != null && arg.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ordered list of asset directories. First element is always {@link ProgramArguments#home()} path, the rest is
     * directories specified via "asset.paths" program argument.
     *
     * @return ordered list of directories to search for assets.
     */
    public Collection<Path> assetPaths() {
        return Stream.concat(
                Stream.of(home()),
                value("assets")
                        .stream()
                        .flatMap(v -> Arrays.stream(v.split(",")))
                        .map(Paths::get)
        ).filter(Files::exists)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
    }

    public boolean fullScreen() {
        return hasSwitch("-fullscreen");
    }

    public int swapInterval() {
        return Integer.valueOf(
                value("swap.interval")
                        .orElse("1")
        );
    }

    /**
     * Writable folder to store mod configs, caches, etc.
     *
     * @return the path to write-enabled folder.
     */
    public Path home() {
        final Path path = value("home")
                .map(Paths::get)
                .orElseGet(this::getDefaultHomePath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return path;
    }

    /**
     * @return default home path - ${user.home}/${app.folder}/${mod}
     */
    private Path getDefaultHomePath() {
        return Paths.get(
                System.getProperty("user.home"),
                System.getProperty("app.folder", "lwjgl3-playground"),
                System.getProperty("mod", "base")
        );
    }
}
