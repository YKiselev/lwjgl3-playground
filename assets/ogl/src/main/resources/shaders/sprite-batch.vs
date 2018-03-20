#version 150 core

in vec2 in_Position;

in vec2 in_TexCoord;

out vec2 vs_TexCoord;

out vec4 vs_Color;

uniform mat4 mvp;

uniform vec4 colors[32];

void main()
{
    int index = gl_VertexID / 4;

    gl_Position = mvp * vec4(in_Position, 0.0, 1.0);

    vs_TexCoord = in_TexCoord;

    vs_Color = colors[index];
}