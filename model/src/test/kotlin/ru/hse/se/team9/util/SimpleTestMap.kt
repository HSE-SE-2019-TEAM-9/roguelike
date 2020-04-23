package ru.hse.se.team9.util

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.MapObject.*
import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.HeroStats
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.distance.Manhattan
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.mobs.strategies.AggressiveStrategy
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.model.random.mobs.RandomMob
import ru.hse.se.team9.model.random.positions.RandomPosition
import ru.hse.se.team9.positions.Position

/**
 * Represents specified map:
 * .#...#
 * .#.#..
 * ..@#.#
 * ####U#
 */
internal object SimpleTestMap {
    const val width = 6
    const val height = 4
    val startPosition = Position(2, 2)
    val startMobPosition = Position(4, 3)
    val map: List<MutableList<MapObject>>
        get() = listOf(
            mutableListOf(EMPTY_SPACE, WALL,        EMPTY_SPACE, EMPTY_SPACE, EMPTY_SPACE, WALL),
            mutableListOf(EMPTY_SPACE, WALL,        EMPTY_SPACE, WALL,        EMPTY_SPACE, EMPTY_SPACE),
            mutableListOf(EMPTY_SPACE, EMPTY_SPACE, EMPTY_SPACE, WALL,        EMPTY_SPACE, WALL),
            mutableListOf(WALL,        WALL,        WALL,        WALL,        EMPTY_SPACE, WALL)
        )

    val upDirectionGenerator = object : DirectionGenerator {
        override fun createDirection(allowedDirections: List<Direction>): Direction = Direction.UP
    }

    val gameMap: GameMap
        get() = GameMap(
            HeroOnMap(
                Hero(HeroStats(10, 10, 0, 10, 10, 10)),
                startPosition
            ),
            map,
            width,
            height,
            RandomPosition,
            mutableMapOf(
                startMobPosition to RandomMob(upDirectionGenerator).createMob().copy(
                    strategy = AggressiveStrategy(
                        upDirectionGenerator
                    )
                )
            ),
            Manhattan,
            10
        )

}