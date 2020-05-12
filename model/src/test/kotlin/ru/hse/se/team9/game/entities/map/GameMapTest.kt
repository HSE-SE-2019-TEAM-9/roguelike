package ru.hse.se.team9.game.entities.map

import arrow.core.getOrHandle
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.util.SimpleTestMap

internal class GameMapTest {
    private lateinit var map: List<MutableList<MapObject>>
    private lateinit var startPositionHero: Position
    private lateinit var startMobPosition: Position
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var gameMap: GameMap

    @BeforeEach
    fun init() {
        map = SimpleTestMap.map
        startPositionHero = SimpleTestMap.startPosition
        startMobPosition = SimpleTestMap.startMobPosition
        width = SimpleTestMap.width
        height = SimpleTestMap.height
        gameMap = SimpleTestMap.gameMap()
    }

    @Test
    fun testMoveHeroToDirection() {
        /*   ??
         * .x++.#
         * .#+#..
         * ..@x.#
         * ##x#.#
         *
         * x = can't go there because it is wall
         * ? = can't go there because of empty space
         * + = can go there
         */
        gameMap.moveHero(0, Direction.DOWN)
        assertEquals(startPositionHero, gameMap.heroes[0]!!.position)
        gameMap.moveHero(0, Direction.RIGHT)
        assertEquals(startPositionHero, gameMap.heroes[0]!!.position)
        gameMap.moveHero(0, Direction.UP)
        assertEquals(Position(startPositionHero.x, startPositionHero.y - 1), gameMap.heroes[0]!!.position)
        gameMap.moveHero(0, Direction.UP)
        assertEquals(Position(startPositionHero.x, startPositionHero.y - 2), gameMap.heroes[0]!!.position)
        gameMap.moveHero(0, Direction.LEFT)
        assertEquals(Position(startPositionHero.x, startPositionHero.y - 2), gameMap.heroes[0]!!.position)
        gameMap.moveHero(0, Direction.UP)
        assertEquals(Position(startPositionHero.x, startPositionHero.y - 2), gameMap.heroes[0]!!.position)
        gameMap.moveHero(0, Direction.RIGHT)
        assertEquals(Position(startPositionHero.x + 1, startPositionHero.y - 2), gameMap.heroes[0]!!.position)
        gameMap.moveHero(0, Direction.UP)
        assertEquals(Position(startPositionHero.x + 1, startPositionHero.y - 2), gameMap.heroes[0]!!.position)
    }

    @Test
    fun testMoveMobToPosition() {
        fun getPos(): Position = gameMap.mobs.keys.first()
        /*   ?
         * +x++.#
         * .#+#..
         * ...x.#
         * ##.#U#?
         *
         * x = can't go there because it is wall
         * ? = can't go there because of empty space
         * + = can go there
         */
        gameMap.moveMob(startMobPosition, Position(3, 2))
        assertEquals(startMobPosition, getPos())
        gameMap.moveMob(startMobPosition, Position(6, 3))
        assertEquals(startMobPosition, getPos())
        gameMap.moveMob(startMobPosition, Position(2, -1))
        assertEquals(startMobPosition, getPos())
        gameMap.moveMob(startMobPosition, Position(0, 0))
        assertEquals(Position(0, 0), getPos())
        gameMap.moveMob(getPos(), Position(2, 0))
        assertEquals(Position(2, 0), getPos())
        gameMap.moveMob(getPos(), Position(3, 0))
        assertEquals(Position(3, 0), getPos())
        gameMap.moveMob(getPos(), Position(2, 1))
        assertEquals(Position(2, 1), getPos())
    }

    @Test
    fun testSerialize() {
        val deserializedMap = GameMap.State(gameMap.getCurrentState().serialize()).restore().getOrHandle {
            throw it
        }

        assertEquals(gameMap.heroes[0], deserializedMap.heroes[0])
        assertEquals(gameMap.mobs.keys, deserializedMap.mobs.keys)
        assertEquals(gameMap.map, deserializedMap.map)
        assertEquals(gameMap.items, deserializedMap.items)
        assertEquals(gameMap.consumables, deserializedMap.consumables)
    }
}