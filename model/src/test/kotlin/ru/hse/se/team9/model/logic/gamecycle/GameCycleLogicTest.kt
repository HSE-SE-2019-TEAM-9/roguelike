package ru.hse.se.team9.model.logic.gamecycle

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import ru.hse.se.team9.entities.EmptySpace
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.HeroStats
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.model.random.RandomPosition
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
        assertEquals(GameStatus.IN_PROGRESS, gameCycleLogic.movePlayer(Down))
        assertEquals(startPosition, gameCycleLogic.map.hero.position)
        assertEquals(GameStatus.IN_PROGRESS, gameCycleLogic.movePlayer(Right))
        assertEquals(startPosition, gameCycleLogic.map.hero.position)
        assertEquals(GameStatus.IN_PROGRESS, gameCycleLogic.movePlayer(Up))
        assertEquals(Position(startPosition.x, startPosition.y - 1), gameCycleLogic.map.hero.position)
        assertEquals(GameStatus.IN_PROGRESS, gameCycleLogic.movePlayer(Up))
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameCycleLogic.map.hero.position)
        assertEquals(GameStatus.IN_PROGRESS, gameCycleLogic.movePlayer(Left))
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameCycleLogic.map.hero.position)
        assertEquals(GameStatus.IN_PROGRESS, gameCycleLogic.movePlayer(Up))
        assertEquals(Position(startPosition.x, startPosition.y - 2), gameCycleLogic.map.hero.position)
        assertEquals(GameStatus.IN_PROGRESS, gameCycleLogic.movePlayer(Right))
        assertEquals(Position(startPosition.x + 1, startPosition.y - 2), gameCycleLogic.map.hero.position)
        assertEquals(GameStatus.IN_PROGRESS, gameCycleLogic.movePlayer(Up))
        assertEquals(Position(startPosition.x + 1, startPosition.y - 2), gameCycleLogic.map.hero.position)
    }
}