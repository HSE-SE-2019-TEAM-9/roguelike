package ru.hse.se.team9.model.logic.gamecycle

import arrow.core.Right
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.util.SimpleTestMap

internal class GameCycleLogicTest {
    private lateinit var map: List<MutableList<MapObject>>
    private lateinit var startPosition: Position
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var gameCycleLogic: GameCycleLogic

    @BeforeEach
    fun init() {
        map = SimpleTestMap.map
        startPosition = SimpleTestMap.startPosition
        width = SimpleTestMap.width
        height = SimpleTestMap.height
        gameCycleLogic = GameCycleLogic(SimpleTestMap.gameMap)
    }

    @Test
    fun testMovePlayer() {
        /*   ??
         * .x++.#
         * .#+#..
         * ..@x.#
         * ##x#.#
         */
        assertEquals(Right(InProgress), gameCycleLogic.movePlayer(Down))
        assertEquals(startPosition, gameCycleLogic.map.heroOnMap.position)
        assertEquals(Right(InProgress), gameCycleLogic.movePlayer(Right))
        assertEquals(startPosition, gameCycleLogic.map.heroOnMap.position)
        assertEquals(Right(InProgress), gameCycleLogic.movePlayer(Up))
        assertEquals(Position(startPosition.x, startPosition.y - 1), gameCycleLogic.map.heroOnMap.position)
        assertEquals(Right(InProgress), gameCycleLogic.movePlayer(Up))
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameCycleLogic.map.heroOnMap.position)
        assertEquals(Right(InProgress), gameCycleLogic.movePlayer(Left))
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameCycleLogic.map.heroOnMap.position)
        assertEquals(Right(InProgress), gameCycleLogic.movePlayer(Up))
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameCycleLogic.map.heroOnMap.position)
        assertEquals(Right(InProgress), gameCycleLogic.movePlayer(Right))
        assertEquals(Position(startPosition.x + 1, startPosition.y - 2), gameCycleLogic.map.heroOnMap.position)
        assertEquals(Right(InProgress), gameCycleLogic.movePlayer(Up))
        assertEquals(Position(startPosition.x + 1, startPosition.y - 2), gameCycleLogic.map.heroOnMap.position)
    }
}