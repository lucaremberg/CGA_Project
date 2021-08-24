package cga.exercise.components.sceneMovement

import cga.exercise.components.geometry.Renderable
import org.joml.Vector3f

class FloorMovement {

    fun isFloorOnCam(floor : Renderable)
    {
        if (floor.getPosition().z >= 8f)
        {
            floor.translateGlobal(Vector3f(0f,0f,-20*11f))
        }
    }
}