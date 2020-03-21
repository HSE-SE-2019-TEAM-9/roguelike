package ru.hse.se.team9.utils

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.model.random.PositionGenerator
import ru.hse.se.team9.positions.Position

operator fun Position.plus(direction: Direction) =
    when (direction) {
        Direction.LEFT -> Position(x - 1, y)
        Direction.RIGHT -> Position(x + 1, y)
        Direction.DOWN -> Position(x, y + 1)
        Direction.UP -> Position(x, y - 1)
    }

tailrec fun getRandomNotWallPosition(
    positionGenerator: PositionGenerator,
    map: List<List<MapObject>>
): Position {
    val (x, y) = positionGenerator.createPosition(map[0].size, map.size)
    return if (map[y][x] is Wall) {
        getRandomNotWallPosition(positionGenerator, map)
    } else {
        Position(x, y)
    }
}