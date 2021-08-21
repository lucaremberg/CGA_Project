#version 330 core
layout (location = 0) in vec3 aPos;

out vec3 textureCoords;

uniform mat4 perspective;
uniform mat4 view;

void main(void)
{
    textureCoords = aPos;
	vec4 pos = perspective * view * vec4(aPos, 1.0);
	gl_Position = pos.xyww;
}