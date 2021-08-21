#version 330 core

//input from vertex shader

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 BrightColor;

in vec2 tc0;
in vec3 Normal, toLight, toCamera, spotLight;

struct Material{
    sampler2D diff;
    sampler2D emit;
    sampler2D specular;
    sampler2D normalMap;
    float shininess;
};

uniform Material material;
uniform vec3 PointLightColor, PointLightAttenuationFactors, colorization;
uniform vec3 SpotLightColor, SpotLightDirection, SpotLightAttenuationFactors;
uniform float SpotLightOuterAngle, SpotLightInnerAngle;

//fragment shader output
out vec4 color;

vec3 BRDF(vec3 N, vec3 L, vec3 V, vec3 R, float shininess){
    vec3 halfDir = normalize(L + V);
    return texture(material.diff, tc0).xyz * max(dot(N, L), 0.0f) + texture(material.specular, tc0).xyz * pow(max(dot(N, halfDir), 0.0), shininess);
}

float calculateAttenuation(vec3 attenuationFactors, vec3 toLight){
    float distance = length(toLight);
    return 1.0f / (attenuationFactors.x + attenuationFactors.y * distance + attenuationFactors.z * (distance * distance));
}

vec3 calcSpotLight(vec3 normal, vec3 sLight, vec3 vDir, vec3 direction, float innerAngle, float outerAngle, vec3 color, float shininess, vec3 attenuationFactor){
    vec3 N = normalize(normal);
    vec3 L = normalize(sLight);
    vec3 V = normalize(vDir);
    vec3 R = reflect(-L, N);

    //Spotlight
    float theta = dot(L, normalize(-direction));
    float epsilon = (innerAngle - outerAngle);
    float intensity = clamp((theta - outerAngle) / epsilon, 0.0f, 1.0f);

    return BRDF(N,L,V,R, shininess) * intensity * color * calculateAttenuation(attenuationFactor, sLight);
}

vec3 calculatePointLights(vec3 normal, vec3 pLight, vec3 vDir, vec3 color, vec3 attenuationFactor, float shininess){
    vec3 N = normalize(normal);
    vec3 L = normalize(pLight);
    vec3 V = normalize(vDir);
    vec3 R = normalize(reflect(-L, N));

    vec3 EmmisiveTerm = texture(material.emit, tc0).xyz * colorization;

    return (EmmisiveTerm + BRDF(N,L,V,R, shininess) * color * calculateAttenuation(attenuationFactor, pLight));
}

void main(){
    vec3 rgbNormal = Normal * 0.5 + 0.5;
    rgbNormal = texture(material.normalMap, tc0).rgb;
    rgbNormal = normalize(rgbNormal * 2.0 - 1.0);

    color = vec4(calculatePointLights(rgbNormal, toLight, toCamera, PointLightColor, PointLightAttenuationFactors, material.shininess), 1.0f);
    color += vec4(calcSpotLight(rgbNormal, spotLight, toCamera, SpotLightDirection, SpotLightInnerAngle, SpotLightOuterAngle, SpotLightColor, material.shininess, SpotLightAttenuationFactors), 1.0f);

    color = pow(color, vec4(1.8 / 2.2)); //color correction
}
