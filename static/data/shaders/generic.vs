#version 150 core

in vec3 in_Position;

in vec2 in_TexCoord;

in vec3 in_Normal;

out vec2 vs_TexCoord;

out vec4 vs_Normal;

uniform mat4 mvp;

void main()
{
    gl_Position = mvp * vec4(in_Position, 1.0);

    vs_TexCoord = in_TexCoord;

    vs_Normal = vec4(in_Normal, 1.0);
}