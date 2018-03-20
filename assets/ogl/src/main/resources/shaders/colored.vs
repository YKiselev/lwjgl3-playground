#version 150 core

in vec3 in_Position;

in vec3 in_Color;

out vec3 vs_Color;

uniform mat4 mvp;

void main()
{
    gl_Position = mvp * vec4(in_Position, 1.0);

    vs_Color = in_Color;
}