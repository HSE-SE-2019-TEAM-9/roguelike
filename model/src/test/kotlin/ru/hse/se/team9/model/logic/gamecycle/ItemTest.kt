package ru.hse.se.team9.model.logic.gamecycle

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.random.GameGenerator
import ru.hse.se.team9.model.random.confusion.RandomStrategyModifier
import ru.hse.se.team9.model.random.consumables.RandomConsumable
import ru.hse.se.team9.model.random.items.RandomItem
import ru.hse.se.team9.model.random.mobs.RandomMob
import ru.hse.se.team9.model.random.positions.RandomPosition
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.util.SimpleTestMap

internal class ItemTest {
    private lateinit var gameMap: GameMap
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
        gameMap = SimpleTestMap.gameMap()
        map = SimpleTestMap.map
        startPosition = SimpleTestMap.startPosition
        startMobPosition = SimpleTestMap.startMobPosition
        width = SimpleTestMap.width
        height = SimpleTestMap.height
        gameCycleLogicImpl = GameCycleLogicImpl(gameMap, generator, false)
    }

    @Test
    fun testItemPicksUpCorrectly() {
        /*
         * .#...#
         * .#.#..
         * .@&#.#
         * ####U#
         */

        gameMap.heroes[0]!!.position = Position(1, 2)
        gameMap.heroes[0]!!.hero.stats.hp = 1
        val underwear = Underwear(5, 5, 5, "UNDERPANTS")
        gameMap.items[Position(2, 2)] = underwear
        assertEquals(
            InProgress,
            gameCycleLogicImpl.makeMove(0, Right)
        )
        assertEquals(0, gameMap.items.size)
        assertEquals(1, gameMap.heroes[0]!!.hero.inventory.size)
        assertEquals(underwear, gameMap.heroes[0]!!.hero.inventory[0])
    }

    @Test
    fun testMobsCannotEatItem() {
        /*
         * .#...#
         * .#.#@.
         * ...#&#
         * ####U#
         */

        gameCycleLogicImpl.map.heroes[0]!!.position = Position(4, 1)
        gameMap.items[Position(4, 2)] = Weapon(name = "HELLO")
        val mob = gameCycleLogicImpl.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 300, armor = 0, damage = 300, maxHp = 300)
        gameCycleLogicImpl.map.mobs[startMobPosition] = newMob

        for (i in 0..15) {
            assertEquals(InProgress, gameCycleLogicImpl.makeMove(0, Up))
            assertEquals(InProgress, gameCycleLogicImpl.makeMove(0, Down))
        }
        assertEquals(1, gameMap.mobs.size)
        assertNotNull(gameMap.mobs[startMobPosition])
    }
}