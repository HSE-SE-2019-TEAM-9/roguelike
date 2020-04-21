package ru.hse.se.team9.util

import ru.hse.se.team9.entities.EmptySpace
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.HeroStats
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.distance.Manhattan
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.model.random.positions.RandomPosition
import ru.hse.se.team9.positions.Position

/**
 * Represents specified map:
 * .#...#
 * .#.#..
 * ..@#.#
 * ####.#
 */
internal object SimpleTestMap {
    val width = 6
    val height = 4
    val startPosition = Position(2, 2)
    val map: List<MutableList<MapObject>>
        get() = listOf(
            mutableListOf(EmptySpace, Wall, EmptySpace, EmptySpace, EmptySpace, Wall),
            mutableListOf(EmptySpace, Wall, EmptySpace, Wall, EmptySpace, EmptySpace),
            mutableListOf(EmptySpace, EmptySpace, EmptySpace, Wall, EmptySpace, Wall),
            mutableListOf(Wall, Wall, Wall, Wall, EmptySpace, Wall)
        )
    val gameMap: GameMap
        get() = GameMap(
            HeroOnMap(
                Hero(HeroStats(10, 10, 10, 10, 10, 10)),
                startPosition
            ),
            map,
            width,
            height,
            RandomPosition,
            listOf(),
            Manhattan,
            10
        )

}