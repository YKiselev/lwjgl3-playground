#version 330 core

layout(location = 0) in vec3 in_Position;

layout(location = 1) in vec2 in_TexCoord;

layout(location = 2) in vec3 in_Normal;

layout(location = 3) in vec3 in_BlockPos;

layout(location = 4) in vec2 in_TexOffset;

out vec2 vs_TexCoord;

out vec4 vs_Normal;

uniform mat4 mvp;

uniform vec2 texScale;

void main()
{
    gl_Position = mvp * vec4(in_Position + in_BlockPos, 1.0);

    vs_TexCoord = in_TexCoord * texScale + in_TexOffset;

    vs_Normal = vec4(in_Normal, 1.0);
}