package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.positions.Position

/** A common interface for strategies which determine mob moves. */
interface MobStrategy {
    /**
     * Make one move.
     * @returns new desired position for mob and new Strategy (strategy may change)
     */
    fun makeMove(position: Position, map: GameMap): Pair<Position, MobStrategy>

    /** Gets strategy-properties for mob. */
    fun getProperties(): List<MobProperty>
}
