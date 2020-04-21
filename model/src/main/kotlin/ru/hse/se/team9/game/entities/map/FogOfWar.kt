package ru.hse.se.team9.game.entities.map

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.plus
import java.util.*

class FogOfWar(
    private val distance: Distance,
    private val map: List<MutableList<MapObject>>,
    private val width: Int,
    private val height: Int,
    private val radius: Int
) {
    val hidden: List<MutableList<Boolean>> = List(height) { MutableList(width) { true } }
    private val directions = listOf(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT)

    fun updateVision(position: Position) {
        val queue: Queue<Position> = ArrayDeque<Position>()
        val used: List<MutableList<Boolean>> = List(height) { MutableList(width) { false } }
        queue.add(position)
        while (!queue.isEmpty()) {
            val currentPosition = queue.poll()
            val (x, y) = currentPosition
            hidden[y][x] = false
            used[y][x] = true

            for (direction in directions) {
                val newPosition = currentPosition + direction
                if (!isOnMap(newPosition) || used[newPosition.y][newPosition.x]) {
                    continue
                }
                if (distance(position, newPosition) <= radius && canExploreFrom(currentPosition, newPosition)) {
                    queue.add(newPosition)
                }
            }
        }
    }

    private fun canExploreFrom(from: Position, position: Position): Boolean {
        val (x1, y1) = from
        val (x2, y2) = position
        return !(map[y1][x1] is Wall && map[y2][x2] !is Wall)
    }

    private fun isOnMap(position: Position): Boolean {
        val (x, y) = position
        return x >= 0 && y >= 0 && x < width && y < height
    }
}
