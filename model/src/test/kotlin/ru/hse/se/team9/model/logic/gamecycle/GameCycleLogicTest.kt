package ru.hse.se.team9.model.logic.gamecycle

import arrow.core.Right
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

internal class GameCycleLogicTest {
    private lateinit var map: List<MutableList<MapObject>>
    private lateinit var startPosition: Position
    private lateinit var startMobPosition: Position
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var gameCycleLogic: GameCycleLogic

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
        gameCycleLogic = GameCycleLogic(SimpleTestMap.gameMap, generator)
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

    @Test
    fun testHeroAndMobDieOnHeroesMove() {
        /*
         * .x...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 10, maxHp = 1)
        gameCycleLogic.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleLogic.makeMove(Down))
        assertEquals(10, gameCycleLogic.map.heroOnMap.hero.stats.hp)
        assertTrue(gameCycleLogic.map.mobs.isEmpty())
    }

    @Test
    fun testHeroDiesOnHeroesMove() {
        /*
         * .x...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 11, armor = 0, damage = 10, maxHp = 11)
        gameCycleLogic.map.mobs[startMobPosition] = newMob

        assertEquals(Loss, gameCycleLogic.makeMove(Down))
        assertEquals(0, gameCycleLogic.map.heroOnMap.hero.stats.hp)
        assertEquals(1, gameCycleLogic.map.mobs[startMobPosition]!!.hp)
    }

    @Test
    fun testMobDiesOnHeroesMove() {
        /*
         * .x...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 9, maxHp = 1)
        gameCycleLogic.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleLogic.makeMove(Down))
        assertEquals(10, gameCycleLogic.map.heroOnMap.hero.stats.hp)
        assertTrue(gameCycleLogic.map.mobs.isEmpty())
    }

    @Test
    fun testHeroAndMobDieOnMobsMove() {
        /*
         * .x...#
         * .#.#@.
         * ...#.#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 1)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 10, maxHp = 1)
        gameCycleLogic.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleLogic.makeMove(Down))
        assertEquals(10, gameCycleLogic.map.heroOnMap.hero.stats.hp)
        assertTrue(gameCycleLogic.map.mobs.isEmpty())
    }

    @Test
    fun testHeroDiesOnMobsMove() {
        /*
         * .x...#
         * .#.#@.
         * ...#.#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 1)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 11, armor = 0, damage = 10, maxHp = 11)
        gameCycleLogic.map.mobs[startMobPosition] = newMob

        assertEquals(Loss, gameCycleLogic.makeMove(Down))
        assertEquals(0, gameCycleLogic.map.heroOnMap.hero.stats.hp)
        assertEquals(1, gameCycleLogic.map.mobs[startMobPosition]!!.hp)
    }

    @Test
    fun testMobDiesOnMobsMove() {
        /*
         * .x...#
         * .#.#@.
         * ...#.#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 9, maxHp = 1)
        gameCycleLogic.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleLogic.makeMove(Down))
        assertEquals(10, gameCycleLogic.map.heroOnMap.hero.stats.hp)
        assertTrue(gameCycleLogic.map.mobs.isEmpty())
    }

    @Test
    fun testHeroAndMobDieAfterLongBattle() {
        /*
         * .x...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 15, armor = 0, damage = 5, maxHp = 15) // will die after two rounds
        gameCycleLogic.map.mobs[startMobPosition] = newMob
        gameCycleLogic

        assertEquals(Win, gameCycleLogic.makeMove(Down)) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(5, gameCycleLogic.map.heroOnMap.hero.stats.hp) // hp after first round
        assertTrue(gameCycleLogic.map.mobs.isEmpty())
    }

    @Test
    fun testHeroDiesAfterLongBattle() {
        /*
         * .x...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 21, armor = 0, damage = 5, maxHp = 21) // will not die
        gameCycleLogic.map.mobs[startMobPosition] = newMob
        gameCycleLogic

        assertEquals(Loss, gameCycleLogic.makeMove(Down)) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(0, gameCycleLogic.map.heroOnMap.hero.stats.hp) // hp after second round
        assertEquals(1, gameCycleLogic.map.mobs[startMobPosition]!!.hp)
    }

    @Test
    fun testMobDiesAfterLongBattle() {
        /*
         * .x...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 15, armor = 0, damage = 3, maxHp = 21) // will die after second round
        gameCycleLogic.map.mobs[startMobPosition] = newMob
        gameCycleLogic

        assertEquals(Win, gameCycleLogic.makeMove(Down)) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(7, gameCycleLogic.map.heroOnMap.hero.stats.hp) // hp after first round
        assertTrue(gameCycleLogic.map.mobs.isEmpty())
    }

    @Test
    fun testNoOneDiesAfterLongBattle() {
        /*
         * .x...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 23, armor = 0, damage = 3, maxHp = 21) // will not die
        gameCycleLogic.map.mobs[startMobPosition] = newMob
        gameCycleLogic

        assertEquals(InProgress, gameCycleLogic.makeMove(Down)) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(4, gameCycleLogic.map.heroOnMap.hero.stats.hp) // hp after second round
        assertEquals(3, gameCycleLogic.map.mobs[startMobPosition]!!.hp)
    }
}