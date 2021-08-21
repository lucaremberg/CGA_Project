#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoords;
layout(location = 2) in vec3 normal;

//uniforms
// translation object to world
uniform mat4 model_matrix;
uniform mat4 view;
uniform mat4 perspective;

uniform vec2 tcMultiplier;

uniform vec3 PointLightPosition, SpotLightPosition;

out vec2 tc0;
out vec3 Normal, toLight, toCamera, spotLight;

//
void main(){
    mat4 modelview = view * model_matrix;
    vec4 pos = modelview * vec4(position, 1.0f);

    vec4 norm = transpose(inverse(modelview)) *  vec4(normal, 0.0f);
    Normal = norm.xyz;

    vec4 lp = view * vec4(PointLightPosition, 1.0f);
    toLight = (lp - pos).xyz;

    vec4 sp = view * vec4(SpotLightPosition, 1.0f);
    spotLight = (sp - pos).xyz;

    toCamera = -pos.xyz;

    tc0 = tcMultiplier * texCoords;
    gl_Position = perspective * pos;
}
