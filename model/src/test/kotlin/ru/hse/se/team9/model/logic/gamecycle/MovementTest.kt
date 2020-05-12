package ru.hse.se.team9.model.logic.gamecycle

import arrow.core.Right
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.model.random.GameGenerator
import ru.hse.se.team9.model.random.confusion.RandomStrategyModifier
import ru.hse.se.team9.model.random.consumables.RandomConsumable
import ru.hse.se.team9.model.random.items.RandomItem
import ru.hse.se.team9.model.random.mobs.RandomMob
import ru.hse.se.team9.model.random.positions.RandomPosition
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.util.SimpleTestMap

internal class MovementTest {
    private lateinit var map: List<MutableList<MapObject>>
    private lateinit var startPosition: Position
    private lateinit var startMobPosition: Position
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var gameCycleLogicImpl: GameCycleLogicImpl

    @BeforeEach
    fun init() {
        val generator = GameGenerator(
            SimpleTestMap.upDirectionGenerator,
            RandomPosition,
            RandomMob(SimpleTestMap.upDirectionGenerator),
            RandomStrategyModifier(SimpleTestMap.upDirectionGenerator),
            RandomItem,
            RandomConsumable
        )

        map = SimpleTestMap.map
        startPosition = SimpleTestMap.startPosition
        startMobPosition = SimpleTestMap.startMobPosition
        width = SimpleTestMap.width
        height = SimpleTestMap.height
        gameCycleLogicImpl = GameCycleLogicImpl(SimpleTestMap.gameMap(), generator)
    }

    @Test
    fun testMovePlayer() {
        /*   ??
         * .x++.#
         * .#+#..
         * ..@x.#
         * ##x#.#
         */
        assertEquals(Right(InProgress), gameCycleLogicImpl.movePlayer(0, Down))
        assertEquals(startPosition, gameCycleLogicImpl.map.heroes[0]!!.position)
        assertEquals(Right(InProgress), gameCycleLogicImpl.movePlayer(0, Right))
        assertEquals(startPosition, gameCycleLogicImpl.map.heroes[0]!!.position)
        assertEquals(Right(InProgress), gameCycleLogicImpl.movePlayer(0, Up))
        assertEquals(Position(startPosition.x, startPosition.y - 1), gameCycleLogicImpl.map.heroes[0]!!.position)
        assertEquals(Right(InProgress), gameCycleLogicImpl.movePlayer(0, Up))
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameCycleLogicImpl.map.heroes[0]!!.position)
        assertEquals(Right(InProgress), gameCycleLogicImpl.movePlayer(0, Left))
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameCycleLogicImpl.map.heroes[0]!!.position)
        assertEquals(Right(InProgress), gameCycleLogicImpl.movePlayer(0, Up))
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameCycleLogicImpl.map.heroes[0]!!.position)
        assertEquals(Right(InProgress), gameCycleLogicImpl.movePlayer(0, Right))
        assertEquals(Position(startPosition.x + 1, startPosition.y - 2), gameCycleLogicImpl.map.heroes[0]!!.position)
        assertEquals(Right(InProgress), gameCycleLogicImpl.movePlayer(0, Up))
        assertEquals(Position(startPosition.x + 1, startPosition.y - 2), gameCycleLogicImpl.map.heroes[0]!!.position)
    }
}