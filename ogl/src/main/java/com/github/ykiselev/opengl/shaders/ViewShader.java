package com.github.ykiselev.opengl.shaders;

import com.github.ykiselev.opengl.Bindable;
import com.github.ykiselev.opengl.shaders.uniforms.Matrix4Variable;
import com.github.ykiselev.opengl.shaders.uniforms.UniformVariableAdapter;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Created by Y.Kiselev on 12.05.2016.
 */
public final class ViewShader implements Bindable {

    private final ProgramObject program;

    private final Matrix4Variable projectionMatrix;

    private final Matrix4Variable viewMatrix;

    private final Matrix4Variable modelMatrix;

    private final UniformVariableAdapter variableAdapter;

    public ViewShader(ProgramObject program, Matrix4Variable projectionMatrix, Matrix4Variable viewMatrix,
                      Matrix4Variable modelMatrix, UniformVariableAdapter variableAdapter) {
        this.program = program;
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
        this.modelMatrix = modelMatrix;
        this.variableAdapter = variableAdapter;
    }

    public ViewShader(ProgramObject program, UniformVariableAdapter variableAdapter) throws ShaderException {
        this(
                program,
                new Matrix4Variable(program.lookup("projectionMatrix")),
                new Matrix4Variable(program.lookup("viewMatrix")),
                new Matrix4Variable(program.lookup("modelMatrix")),
                variableAdapter
        );
    }

    @Override
    public void bind() {
        this.program.bind();
    }

    @Override
    public void unbind() {
        this.program.unbind();
    }

    @Override
    public int id() {
        return program.id();
    }

    public void projectionMatrix(Matrix4f src) {
        this.variableAdapter.copy(src, false, this.projectionMatrix);
    }

    public void viewMatrix(Matrix4f src) {
        this.variableAdapter.copy(src, false, this.viewMatrix);
    }

    public void modelMatrix(Matrix4f src) {
        this.variableAdapter.copy(src, false, this.modelMatrix);
    }

    public Matrix4Variable projectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4Variable viewMatrix() {
        return viewMatrix;
    }

    public Matrix4Variable modelMatrix() {
        return modelMatrix;
    }
}
