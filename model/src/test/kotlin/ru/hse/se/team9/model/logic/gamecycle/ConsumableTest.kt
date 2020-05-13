package ru.hse.se.team9.model.logic.gamecycle

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.game.entities.hero.consumables.Consumable
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.generators.GameGenerator
import ru.hse.se.team9.model.generators.confusion.RandomStrategyModifier
import ru.hse.se.team9.model.generators.consumables.RandomConsumable
import ru.hse.se.team9.model.generators.heroes.DefaultHeroCreator
import ru.hse.se.team9.model.generators.items.RandomItem
import ru.hse.se.team9.model.generators.mobs.RandomMob
import ru.hse.se.team9.model.generators.positions.RandomPosition
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.util.SimpleTestMap

internal class ConsumableTest {
    private lateinit var gameMap: GameMap
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
        gameMap = SimpleTestMap.gameMap()
        map = SimpleTestMap.map
        startPosition = SimpleTestMap.startPosition
        startMobPosition = SimpleTestMap.startMobPosition
        width = SimpleTestMap.width
        height = SimpleTestMap.height
        gameCycleProcessor = GameCycleProcessor(gameMap, generator, false)
    }

    @Test
    fun testConsumableHealsCorrectly() {
        /*
         * .#...#
         * .#.#..
         * .@♥#.#
         * ####U#
         */

        gameMap.heroes[0]!!.position = Position(1, 2)
        gameMap.heroes[0]!!.hero.stats.hp = 1
        gameMap.consumables[Position(2, 2)] = Consumable(13)
        assertEquals(
            InProgress,
            gameCycleProcessor.makeMove(0, Right)
        )
        assertEquals(10, gameMap.heroes[0]!!.hero.stats.hp)
        assertEquals(0, gameMap.consumables.size)
    }

    @Test
    fun testConsumableKills() {
        /*
         * .#...#
         * .#.#..
         * .@♥#.#
         * ####U#
         */

        gameMap.heroes[0]!!.position = Position(1, 2)
        gameMap.heroes[0]!!.hero.stats.hp = 1
        gameMap.consumables[Position(2, 2)] = Consumable(-50)
        assertEquals(
            Loss,
            gameCycleProcessor.makeMove(0, Right)
        )
        assertEquals(0, gameMap.heroes[0]!!.hero.stats.hp)
        assertEquals(0, gameMap.consumables.size)
    }

    @Test
    fun testMobsCannotEatConsumable() {
        /*
         * .#...#
         * .#.#@.
         * ...#♥#
         * ####U#
         */

        gameCycleProcessor.map.heroes[0]!!.position = Position(4, 1)
        gameMap.consumables[Position(4, 2)] = Consumable(13)
        val mob = gameCycleProcessor.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 300, armor = 0, damage = 300, maxHp = 300)
        gameCycleProcessor.map.mobs[startMobPosition] = newMob

        for (i in 0..15) {
            assertEquals(InProgress, gameCycleProcessor.makeMove(0, Up))
            assertEquals(InProgress, gameCycleProcessor.makeMove(0, Down))
        }
        assertEquals(1, gameMap.mobs.size)
        assertNotNull(gameMap.mobs[startMobPosition])
    }
}