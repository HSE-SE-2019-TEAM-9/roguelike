package ru.hse.se.team9.model.logic.gamecycle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.model.generators.GameGenerator
import ru.hse.se.team9.model.generators.confusion.RandomStrategyModifier
import ru.hse.se.team9.model.generators.consumables.RandomConsumable
import ru.hse.se.team9.model.generators.heroes.DefaultHeroCreator
import ru.hse.se.team9.model.generators.items.RandomItem
import ru.hse.se.team9.model.generators.mobs.RandomMob
import ru.hse.se.team9.model.generators.positions.RandomPosition
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.util.SimpleTestMap

internal class BattleTest {
    private lateinit var map: List<MutableList<MapObject>>
    private lateinit var startPosition: Position
    private lateinit var startMobPosition: Position
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var gameCycleProcessor: GameCycleProcessor

    @BeforeEach
    fun init() {
        val generator = GameGenerator(
            SimpleTestMap.upDirectionGenerator,
            RandomPosition,
            RandomMob(SimpleTestMap.upDirectionGenerator),
            RandomStrategyModifier(SimpleTestMap.upDirectionGenerator),
            RandomItem,
            RandomConsumable,
            DefaultHeroCreator
        )

        map = SimpleTestMap.map
        startPosition = SimpleTestMap.startPosition
        startMobPosition = SimpleTestMap.startMobPosition
        width = SimpleTestMap.width
        height = SimpleTestMap.height
        gameCycleProcessor = GameCycleProcessor(SimpleTestMap.gameMap(), generator, false)
    }

    @Test
    fun testHeroAndMobDieOnHeroesMove() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 2)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 10, maxHp = 1)
        gameCycleProcessor.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleProcessor.makeMove(0, Down))
        assertEquals(10, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp)
        assertTrue(gameCycleProcessor.map.mobs.isEmpty())
    }

    @Test
    fun testHeroDiesOnHeroesMove() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 2)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 11, armor = 0, damage = 10, maxHp = 11)
        gameCycleProcessor.map.mobs[startMobPosition] = newMob

        assertEquals(Loss, gameCycleProcessor.makeMove(0, Down))
        assertEquals(0, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp)
        assertEquals(1, gameCycleProcessor.map.mobs[startMobPosition]!!.hp)
    }

    @Test
    fun testMobDiesOnHeroesMove() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 2)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 9, maxHp = 1)
        gameCycleProcessor.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleProcessor.makeMove(0, Down))
        assertEquals(10, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp)
        assertTrue(gameCycleProcessor.map.mobs.isEmpty())
    }

    @Test
    fun testHeroAndMobDieOnMobsMove() {
        /*
         * .#...#
         * .#.#@.
         * ...#.#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 1)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 10, maxHp = 1)
        gameCycleProcessor.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleProcessor.makeMove(0, Down))
        assertEquals(10, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp)
        assertTrue(gameCycleProcessor.map.mobs.isEmpty())
    }

    @Test
    fun testHeroDiesOnMobsMove() {
        /*
         * .#...#
         * .#.#@.
         * ...#.#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 1)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 11, armor = 0, damage = 10, maxHp = 11)
        gameCycleProcessor.map.mobs[startMobPosition] = newMob

        assertEquals(Loss, gameCycleProcessor.makeMove(0, Down))
        assertEquals(0, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp)
        assertEquals(1, gameCycleProcessor.map.mobs[startMobPosition]!!.hp)
    }

    @Test
    fun testMobDiesOnMobsMove() {
        /*
         * .#...#
         * .#.#@.
         * ...#.#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 2)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 1, armor = 0, damage = 9, maxHp = 1)
        gameCycleProcessor.map.mobs[startMobPosition] = newMob

        assertEquals(Win, gameCycleProcessor.makeMove(0, Down))
        assertEquals(10, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp)
        assertTrue(gameCycleProcessor.map.mobs.isEmpty())
    }

    @Test
    fun testHeroAndMobDieAfterLongBattle() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 2)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 15, armor = 0, damage = 5, maxHp = 15) // will die after two rounds
        gameCycleProcessor.map.mobs[startMobPosition] = newMob
        gameCycleProcessor

        assertEquals(Win, gameCycleProcessor.makeMove(0, Down)) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(5, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp) // hp after first round
        assertTrue(gameCycleProcessor.map.mobs.isEmpty())
    }

    @Test
    fun testHeroDiesAfterLongBattle() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 2)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 21, armor = 0, damage = 5, maxHp = 21) // will not die
        gameCycleProcessor.map.mobs[startMobPosition] = newMob
        gameCycleProcessor

        assertEquals(
            Loss,
            gameCycleProcessor.makeMove(0, Down)
        ) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(0, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp) // hp after second round
        assertEquals(1, gameCycleProcessor.map.mobs[startMobPosition]!!.hp)
    }

    @Test
    fun testMobDiesAfterLongBattle() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 2)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 15, armor = 0, damage = 3, maxHp = 21) // will die after second round
        gameCycleProcessor.map.mobs[startMobPosition] = newMob
        gameCycleProcessor

        assertEquals(Win, gameCycleProcessor.makeMove(0, Down)) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(7, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp) // hp after first round
        assertTrue(gameCycleProcessor.map.mobs.isEmpty())
    }

    @Test
    fun testNoOneDiesAfterLongBattle() {
        /*
         * .#...#
         * .#.#..
         * ...#@#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 2)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 23, armor = 0, damage = 3, maxHp = 21) // will not die
        gameCycleProcessor.map.mobs[startMobPosition] = newMob
        gameCycleProcessor

        assertEquals(
            InProgress,
            gameCycleProcessor.makeMove(0, Down)
        ) // DirectionGenerator is mocked and confused mob always goes up
        assertEquals(4, gameCycleProcessor.map.heroes[0]!!.hero.stats.hp) // hp after second round
        assertEquals(3, gameCycleProcessor.map.mobs[startMobPosition]!!.hp)
    }
}