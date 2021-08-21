package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class CubeMapMaterial(var viewMat: Matrix4f,
                      var projectionMat: Matrix4f,
                      var cubeMapIndex: Int){

    fun bind(shaderProgram: ShaderProgram){
        shaderProgram.setUniform("cubeMap", cubeMapIndex)
        shaderProgram.setUniform("view", viewMat, false)
        shaderProgram.setUniform("perspective", projectionMat, false)
    }
}