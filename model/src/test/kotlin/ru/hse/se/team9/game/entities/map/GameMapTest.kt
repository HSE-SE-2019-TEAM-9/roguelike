package ru.hse.se.team9.game.entities.map

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.util.SimpleTestMap
import ru.hse.se.team9.util.getResourceFile

internal class GameMapTest {
    private lateinit var map: List<MutableList<MapObject>>
    private lateinit var startPosition: Position
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var gameMap: GameMap

    @BeforeEach
    fun init() {
        map = SimpleTestMap.map
        startPosition = SimpleTestMap.startPosition
        width = SimpleTestMap.width
        height = SimpleTestMap.height
        gameMap = SimpleTestMap.gameMap
    }

    @Test
    fun testMoveHeroToDirection() {
        /*   ??
         * .x++.#
         * .#+#..
         * ..@x.#
         * ##x#.#
         */
        gameMap.moveHero(Direction.DOWN)
        assertEquals(startPosition, gameMap.heroOnMap.position)
        gameMap.moveHero(Direction.RIGHT)
        assertEquals(startPosition, gameMap.heroOnMap.position)
        gameMap.moveHero(Direction.UP)
        assertEquals(Position(startPosition.x, startPosition.y - 1), gameMap.heroOnMap.position)
        gameMap.moveHero(Direction.UP)
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameMap.heroOnMap.position)
        gameMap.moveHero(Direction.LEFT)
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameMap.heroOnMap.position)
        gameMap.moveHero(Direction.UP)
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameMap.heroOnMap.position)
        gameMap.moveHero(Direction.RIGHT)
        assertEquals(Position(startPosition.x + 1, startPosition.y - 2), gameMap.heroOnMap.position)
        gameMap.moveHero(Direction.UP)
        assertEquals(Position(startPosition.x + 1, startPosition.y - 2), gameMap.heroOnMap.position)
    }

    @Test
    fun testSerialize() {
        val expected = getResourceFile(this::class.java, "/serializedMap.txt").readText()
        assertEquals(expected, gameMap.serialize())
    }
}