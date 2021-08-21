package cga.exercise.components.sceneMovement

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.RenderableList
import org.joml.Vector3f

class ObjectMovement {

    fun isObjectOnCam(obstacle : Renderable)
    {
        if (obstacle.getPosition().z >= 13f)
        {
            val spawnRandom = (1..100).random()
            if(spawnRandom == 5) {
                //obstacle.translateGlobal(Vector3f(-obstacle.getWorldPosition().x - 2.25f, 0f, 0f)
                val laneRandom = (1..3).random()
                when (laneRandom) {
                    1 -> {
                        obstacle.translateGlobal(Vector3f(-4.5f - 0.449f, 0f, 0f))
                    }
                    2 -> {
                         //obstacle.translateGlobal(Vector3f(-2.25f,0f,0f))
                    }
                    3 -> {
                        obstacle.translateGlobal(Vector3f(4.5f + 0.449f, 0f, 0f))
                    }
                }
                obstacle.translateGlobal(Vector3f(0f, 0f, -16 * 5.5f - obstacle.getPosition().z + 10f))
            }
        }
    }

    fun respawnObject(obstacle : Renderable):Boolean {
        val spawnRandom = (1..300).random()
        if(spawnRandom == 10) {
            if (!isObstacleOnCam(obstacle)) {
                val offsetList = listOf(0, -20, -40, -60).shuffled()
                obstacle.translateGlobal(Vector3f(0f, 0f, -16 * 5.5f - obstacle.getPosition().z + offsetList[0]))
                return true
            }
        }
        return false
    }

    fun respawnPowerUp(obstacle : Renderable) {
        val spawnRandom = (1..1000).random()
        if(spawnRandom == 69) {
            if (!isObstacleOnCam(obstacle)) {

                obstacle.translateGlobal(Vector3f(-obstacle.getWorldPosition().x - 2.25f, 0f, 0f))

                val offsetList = listOf(0, -20, -40, -60).shuffled()
                val laneRandom = (1..3).random()
                when (laneRandom) {
                    1 -> {
                        obstacle.translateGlobal(Vector3f(-4.5f +2.8f , 0f, -16 * 5.5f - obstacle.getPosition().z + offsetList[0]))
                    }
                    2 -> {
                        obstacle.translateGlobal(Vector3f(3.3f,0f,-16 * 5.5f - obstacle.getPosition().z + offsetList[0]))
                    }
                    3 -> {
                        obstacle.translateGlobal(Vector3f(8.2f, 0f, -16 * 5.5f - obstacle.getPosition().z + offsetList[0]))
                    }
                }
            }
        }

    }

    fun isObstacleOnCam(obstacle: Renderable) : Boolean{
        if (obstacle.getPosition().z > 10f) {
            return false
        }
        return true
    }
}
