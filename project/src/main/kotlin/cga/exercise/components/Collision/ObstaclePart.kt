package cga.exercise.components.Collision

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.RenderableList
import cga.exercise.components.sceneMovement.ObjectMovement

class ObstaclePart() {
    var obstacleList = RenderableList()
    val objectSpawner = ObjectMovement()

    fun respawnObstaclePart() {
        //val spawnRandom = (1..10000).random()
        //if (spawnRandom == 10) {
        if (areAllObstaclesOnCam(obstacleList)) {
        obstacleList.objects.forEach {

                objectSpawner.respawnObject(it)
            }
        }
    }

    fun areAllObstaclesOnCam(obstacles: RenderableList) : Boolean{
        var counter = 0
        obstacles.objects.forEach {
            if (it.getPosition().z < 10f) {
                counter++
            }
        }

        if (counter>0) {
            return false
        }
        return true
    }
}