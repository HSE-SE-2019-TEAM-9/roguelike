package ru.hse.se.team9.model.logic.gamecycle

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

internal class BattleTest {
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
        gameCycleLogicImpl = GameCycleLogicImpl(SimpleTestMap.gameMap(), generator, false)
    }

    @Test
    fun testHeroAndMobDieOnHeroesMove() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 10, maxHp = 1)
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleLogicImpl.makeMove(Down))
        assertEquals(10, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp)
        assertTrue(gameCycleLogicImpl.map.mobs.isEmpty())
    }

    @Test
    fun testHeroDiesOnHeroesMove() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 11, armor = 0, damage = 10, maxHp = 11)
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob

        assertEquals(Loss, gameCycleLogicImpl.makeMove(Down))
        assertEquals(0, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp)
        assertEquals(1, gameCycleLogicImpl.map.mobs[startMobPosition]!!.hp)
    }

    @Test
    fun testMobDiesOnHeroesMove() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 9, maxHp = 1)
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleLogicImpl.makeMove(Down))
        assertEquals(10, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp)
        assertTrue(gameCycleLogicImpl.map.mobs.isEmpty())
    }

    @Test
    fun testHeroAndMobDieOnMobsMove() {
        /*
         * .#...#
         * .#.#@.
         * ...#.#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 1)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 10, maxHp = 1)
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleLogicImpl.makeMove(Down))
        assertEquals(10, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp)
        assertTrue(gameCycleLogicImpl.map.mobs.isEmpty())
    }

    @Test
    fun testHeroDiesOnMobsMove() {
        /*
         * .#...#
         * .#.#@.
         * ...#.#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 1)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 11, armor = 0, damage = 10, maxHp = 11)
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob

        assertEquals(Loss, gameCycleLogicImpl.makeMove(Down))
        assertEquals(0, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp)
        assertEquals(1, gameCycleLogicImpl.map.mobs[startMobPosition]!!.hp)
    }

    @Test
    fun testMobDiesOnMobsMove() {
        /*
         * .#...#
         * .#.#@.
         * ...#.#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 9, maxHp = 1)
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleLogicImpl.makeMove(Down))
        assertEquals(10, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp)
        assertTrue(gameCycleLogicImpl.map.mobs.isEmpty())
    }

    @Test
    fun testHeroAndMobDieAfterLongBattle() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 15, armor = 0, damage = 5, maxHp = 15) // will die after two rounds
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob
        gameCycleLogicImpl

        assertEquals(Win, gameCycleLogicImpl.makeMove(Down)) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(5, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp) // hp after first round
        assertTrue(gameCycleLogicImpl.map.mobs.isEmpty())
    }

    @Test
    fun testHeroDiesAfterLongBattle() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 21, armor = 0, damage = 5, maxHp = 21) // will not die
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob
        gameCycleLogicImpl

        assertEquals(
            Loss,
            gameCycleLogicImpl.makeMove(Down)
        ) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(0, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp) // hp after second round
        assertEquals(1, gameCycleLogicImpl.map.mobs[startMobPosition]!!.hp)
    }

    @Test
    fun testMobDiesAfterLongBattle() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 15, armor = 0, damage = 3, maxHp = 21) // will die after second round
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob
        gameCycleLogicImpl

        assertEquals(Win, gameCycleLogicImpl.makeMove(Down)) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(7, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp) // hp after first round
        assertTrue(gameCycleLogicImpl.map.mobs.isEmpty())
    }

    @Test
    fun testNoOneDiesAfterLongBattle() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleLogicImpl.map.heroOnMap.position = Position(4, 2)
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 23, armor = 0, damage = 3, maxHp = 21) // will not die
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob
        gameCycleLogicImpl

        assertEquals(
            InProgress,
            gameCycleLogicImpl.makeMove(Down)
        ) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(4, gameCycleLogicImpl.map.heroOnMap.hero.stats.hp) // hp after second round
        assertEquals(3, gameCycleLogicImpl.map.mobs[startMobPosition]!!.hp)
    }
}