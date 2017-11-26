package com.github.ykiselev.opengl.shaders;

import com.github.ykiselev.opengl.Identified;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class ShaderObject implements Identified, AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int id;

    @Override
    public int id() {
        return id;
    }

    public ShaderObject(int type, String source) throws ShaderException {
        this.id = glCreateShader(type);
        Util.checkGLError();
        if (this.id == 0) {
            throw new ShaderException("Unable to create shader, GL error: " + glGetError());
        }
        glShaderSource(this.id, source);
        Util.checkGLError();
        glCompileShader(this.id);
        Util.checkGLError();
        final int status = glGetShaderi(this.id, GL_COMPILE_STATUS);
        final String log = glGetShaderInfoLog(this.id, 8 * 1024);
        if (status != GL_TRUE) {
            throw new ShaderException(log);
        } else {
            if (StringUtils.isNotEmpty(log)) {
                this.logger.warn("Shader log: {}", log);
            }
        }
    }

    public ShaderObject(int type, URL resource, Charset charset) throws ShaderException, IOException {
        this(type, Resources.toString(resource, charset));
    }

    public ShaderObject(int type, URL resource) throws ShaderException, IOException {
        this(type, resource, StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        glDeleteShader(this.id);
    }
}
