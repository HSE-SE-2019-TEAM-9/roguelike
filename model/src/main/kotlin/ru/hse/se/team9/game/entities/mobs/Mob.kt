package ru.hse.se.team9.game.entities.mobs

import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.positions.Position

data class Mob(
    var hp: Int,
    val maxHp: Int,
    val damage: Int,
    val armor: Int,
    val expGain: Int,
    private var strategy: MobStrategy
) {
    private val id: Int = mobId++

    fun makeMove(position: Position, gameMap: GameMap): Position {
        val (newPosition, newStrategy) = strategy.makeMove(position, gameMap)
        strategy = newStrategy
        return newPosition
    }

    fun applyStrategyModifier(modifier: (MobStrategy) -> MobStrategy) {
        strategy = modifier(strategy)
    }

    fun getModifiers() = strategy.getModifiers()

    companion object {
        var mobId: Int = 0
    }
}