package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.sceneMovement.ObjectMovement
import cga.exercise.components.sceneMovement.FloorMovement
import org.joml.Vector3f

class RenderableList(var objects : ArrayList<Renderable> = arrayListOf()){

    fun renderListOfObjects(shaderProgram: ShaderProgram){
        objects.forEach{it.render(shaderProgram)}
    }

    fun isFixObjectOnCam(floorMovement: FloorMovement){
        objects.forEach { floorMovement.isFloorOnCam(it) }
    }

    fun speedUpFixObject(speedMultiplier : Double){
        objects.forEach { it.translateGlobal(Vector3f(0f,0f,(speedMultiplier * 0.01).toFloat())) }
    }

    fun isObstacleOnCam(objectMovement: ObjectMovement){
        objects.forEach { objectMovement.respawnObject(it) }
    }

    fun speedUpObstacles(speedMultiplier : Double){
        objects.forEach { it.translateGlobal(Vector3f(0f,0f,(speedMultiplier * 0.01).toFloat())) }
    }
}