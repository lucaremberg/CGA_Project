#version 330 core
#extension GL_NV_shadow_samplers_cube: enable
out vec4 FragColor;

in vec3 textureCoords;

uniform samplerCube cubeMap;

void main(void){
    FragColor = textureCube(cubeMap, textureCoords);
}