package ru.hse.se.team9.game.entities.map

import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.model.random.PositionGenerator

class GameMap(
    private val hero: HeroOnMap,
    private val map: MutableList<MutableList<MapObject>>,
    private val width: Int,
    private val height: Int,
    private val positionGenerator: PositionGenerator
) {

    fun moveHero(newPosition: Position) {
        hero.position = newPosition
    }

    fun moveHero(direction: Direction) {
        val (x, y) = hero.position
        hero.position = when (direction) {
            Direction.LEFT -> Position(x - 1, y)
            Direction.RIGHT -> Position(x + 1, y)
            Direction.DOWN -> Position(x, y - 1)
            Direction.UP -> Position(x, y + 1)
        }
    }

    fun placeAtRandomPosition(mapObject: MapObject) {
        val (x, y) = getRandomNotWallPosition()
        map[x][y] = mapObject
    }

    private tailrec fun getRandomNotWallPosition(): Position {
        val (x, y) = positionGenerator.createPosition(width, height)
        return if (map[x][y] is Wall) {
            getRandomNotWallPosition()
        } else {
            Position(x, y)
        }
    }
}