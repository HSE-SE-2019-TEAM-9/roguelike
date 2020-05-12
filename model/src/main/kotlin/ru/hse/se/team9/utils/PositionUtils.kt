package ru.hse.se.team9.utils

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.model.generators.positions.PositionGenerator
import ru.hse.se.team9.positions.Position

/**
 * Adds direction to position. This operation is equivalent to taking one step in the specified direction from
 * receiver-position.
 */
operator fun Position.plus(direction: Direction) =
    when (direction) {
        Direction.LEFT -> Position(x - 1, y)
        Direction.RIGHT -> Position(x + 1, y)
        Direction.DOWN -> Position(x, y + 1)
        Direction.UP -> Position(x, y - 1)
    }

/** Generates position with PositionGenerator until generated position is not wall. */
tailrec fun getNotWallPosition(
    positionGenerator: PositionGenerator,
    map: List<List<MapObject>>
): Position {
    val (x, y) = positionGenerator.createPosition(map[0].size, map.size)
    return if (map[y][x] == MapObject.WALL) {
        getNotWallPosition(positionGenerator, map)
    } else {
        Position(x, y)
    }
}