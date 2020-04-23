package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.positions.Position

/** Does not makes any moves */
object PassiveStrategy : MobStrategy {
    /** Returns given position */
    override fun makeMove(position: Position, map: GameMap): Pair<Position, MobStrategy> = Pair(position, this)
    override fun getProperties(): List<MobProperty> = emptyList()
}