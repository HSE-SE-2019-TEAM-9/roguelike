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
        gameMap = SimpleTestMap.gameMap()
        map = SimpleTestMap.map
        startPosition = SimpleTestMap.startPosition
        startMobPosition = SimpleTestMap.startMobPosition
        width = SimpleTestMap.width
        height = SimpleTestMap.height
        gameCycleLogic = GameCycleLogic(gameMap, generator, false)
    }

    @Test
    fun testItemPicksUpCorrectly() {
        /*
         * .#...#
         * .#.#..
         * .@&#.#
         * ####U#
         */

        gameMap.heroOnMap.position = Position(1, 2)
        gameMap.heroOnMap.hero.stats.hp = 1
        val underwear = Underwear(5, 5, 5, "UNDERPANTS")
        gameMap.items[Position(2, 2)] = underwear
        assertEquals(
            InProgress,
            gameCycleLogic.makeMove(Right)
        )
        assertEquals(0, gameMap.items.size)
        assertEquals(1, gameMap.heroOnMap.hero.inventory.size)
        assertEquals(underwear, gameMap.heroOnMap.hero.inventory[0])
    }

    @Test
    fun testMobsCannotEatItem() {
        /*
         * .#...#
         * .#.#@.
         * ...#&#
         * ####U#
         */

        gameCycleLogic.map.heroOnMap.position = Position(4, 1)
        gameMap.items[Position(4, 2)] = Weapon(name = "HELLO")
        val mob = gameCycleLogic.map.mobs[startMobPosition]!!
        val newMob = mob.copy(hp = 300, armor = 0, damage = 300, maxHp = 300)
        gameCycleLogic.map.mobs[startMobPosition] = newMob

        for (i in 0..15) {
            assertEquals(InProgress, gameCycleLogic.makeMove(Up))
            assertEquals(InProgress, gameCycleLogic.makeMove(Down))
        }
        assertEquals(1, gameMap.mobs.size)
        assertNotNull(gameMap.mobs[startMobPosition])
    }
}