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

package com.github.ykiselev.playground.services.console.appender;

import com.github.ykiselev.common.circular.ArrayCircularBuffer;
import com.github.ykiselev.common.circular.CircularBuffer;
import com.github.ykiselev.common.circular.SynchronizedCircularBuffer;
import com.github.ykiselev.playground.services.console.CommandLine;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

import java.io.Serializable;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@Plugin(name = "AppConsole", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public final class AppConsoleLog4j2Appender extends AbstractAppender {

    private final CircularBuffer<String> buffer;

    public CircularBuffer<String> buffer() {
        return buffer;
    }

    private AppConsoleLog4j2Appender(String name, Filter filter, Layout<? extends Serializable> layout, int bufferSize) {
        super(name, filter, layout, true, Property.EMPTY_ARRAY);
        this.buffer = new SynchronizedCircularBuffer<>(
                new ArrayCircularBuffer<>(String.class, bufferSize)
        );
    }

    @Override
    public void append(LogEvent logEvent) {
        final Marker marker = logEvent.getMarker();
        if (marker != null) {
            if (marker.isInstanceOf(CommandLine.MARKER.getName())) {
                buffer.write(logEvent.getMessage().getFormattedMessage());
                return;
            }
        }
        buffer.write(getLayout().toSerializable(logEvent).toString());
    }

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return new Builder<B>().asBuilder();
    }

    public static final class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<AppConsoleLog4j2Appender> {

        @PluginBuilderAttribute
        private int bufferSize = 100;

        @Override
        public AppConsoleLog4j2Appender build() {
            return new AppConsoleLog4j2Appender(getName(), getFilter(), getOrCreateLayout(), bufferSize);
        }

        public B withBufferSize(int bufferCapacity) {
            this.bufferSize = bufferCapacity;
            return asBuilder();
        }
    }
}
