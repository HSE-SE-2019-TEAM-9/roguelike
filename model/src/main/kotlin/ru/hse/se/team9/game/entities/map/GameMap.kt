package ru.hse.se.team9.game.entities.map

import ru.hse.se.team9.game.entities.map.objects.EmptySpace
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.map.objects.MapObject
import ru.hse.se.team9.game.entities.map.objects.Wall
import ru.hse.se.team9.model.random.RandomPositionGenerator

class GameMap(private val heroes: List<HeroOnMap>, private val width: Int, private val height: Int) {
    private var map: MutableList<MutableList<MapObject>> =
        MutableList(width) { MutableList<MapObject>(height) { EmptySpace } }

    fun moveHero(heroToMove: HeroOnMap, newPosition: Position) {
        for (hero in heroes) {
            if (hero == heroToMove) {
                hero.position = newPosition
                break
            }
        }
    }

    fun placeAtRandomPosition(mapObject: MapObject) {
        val (x, y) = getRandomNotWallPosition()
        map[x][y] = mapObject
    }

    private tailrec fun getRandomNotWallPosition(): Position {
        val (x, y) = RandomPositionGenerator.createPosition(width, height)
        return if (map[x][y] is Wall) {
            getRandomNotWallPosition()
        } else {
            Position(x, y)
        }
    }
}