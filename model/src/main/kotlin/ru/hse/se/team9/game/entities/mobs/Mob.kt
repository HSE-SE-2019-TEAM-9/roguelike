package ru.hse.se.team9.game.entities.mobs

import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.positions.Position

/**
 * Stores information about one mob.
 * @property hp current health points
 * @property maxHp maximal health points of mob at the birth moment
 * @property armor armor value
 * @property damage damage dealt within one hit
 * @property expGain how much xp will the hero get for killing the mob
 * @property strategy how mob chooses his new position
 */
data class Mob(
    var hp: Int,
    val maxHp: Int,
    val damage: Int,
    val armor: Int,
    val expGain: Int,
    private var strategy: MobStrategy
) {
    private val id: Int = mobId++

    /** Creates new "desired" position for that mob according to strategy. */
    fun makeMove(position: Position, gameMap: GameMap): Position {
        val (newPosition, newStrategy) = strategy.makeMove(position, gameMap)
        strategy = newStrategy
        return newPosition
    }

    /** Applies modifier to current strategy. */
    fun applyStrategyModifier(modifier: (MobStrategy) -> MobStrategy) {
        strategy = modifier(strategy)
    }

    /** All special information about that mob. Currently all special information comes from mobs strategy. */
    fun getProperties() = strategy.getProperties()

    companion object {
        var mobId: Int = 0
    }
}