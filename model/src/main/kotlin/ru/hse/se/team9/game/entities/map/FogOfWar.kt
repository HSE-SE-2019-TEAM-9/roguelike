package ru.hse.se.team9.game.entities.map

import ru.hse.se.team9.entities.FogType
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.plus
import java.util.*

/** Determines which part of the map is hidden for the hero. */
class FogOfWar(
    private val distance: Distance,
    private val map: List<MutableList<MapObject>>,
    private val width: Int,
    private val height: Int,
    private val radius: Int
) {
    val fog: List<MutableList<FogType>> = List(height) { MutableList(width) { FogType.INVISIBLE } }
    private val directions = listOf(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT)
    private val visibleCells: MutableSet<Position> = mutableSetOf()

    /** Opens new map cells for hero base on current hero position */
    fun updateVision(position: Position) {
        makeVisibleCellsShadowed()
        val queue: Queue<Position> = ArrayDeque<Position>()
        val used: MutableMap<Int, Boolean> = mutableMapOf()
        queue.add(position)
        while (!queue.isEmpty()) {
            val currentPosition = queue.poll()
            val (x, y) = currentPosition
            fog[y][x] = FogType.VISIBLE
            used[y * width + x] = true
            visibleCells.add(currentPosition)

            for (direction in directions) {
                val newPosition = currentPosition + direction
                if (!isOnMap(newPosition) || used[newPosition.y * width + newPosition.x] == true) {
                    continue
                }
                if (distance(position, newPosition) <= radius && canExploreFrom(currentPosition, newPosition)) {
                    queue.add(newPosition)
                }
            }
        }
    }

    private fun makeVisibleCellsShadowed() {
        for ((x, y) in visibleCells) {
            fog[y][x] = FogType.SHADOWED
        }
        visibleCells.clear()
    }

    private fun canExploreFrom(from: Position, position: Position): Boolean {
        val (x1, y1) = from
        val (x2, y2) = position
        return !(map[y1][x1] == MapObject.WALL && map[y2][x2] != MapObject.WALL)
    }

    private fun isOnMap(position: Position): Boolean {
        val (x, y) = position
        return x >= 0 && y >= 0 && x < width && y < height
    }
}
