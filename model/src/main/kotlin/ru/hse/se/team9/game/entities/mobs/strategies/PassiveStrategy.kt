package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.entities.MobModifier
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.positions.Position

object PassiveStrategy : MobStrategy {
    override fun makeMove(position: Position, map: GameMap): Pair<Position, MobStrategy> = Pair(position, this)
    override fun getModifiers(): List<MobModifier> = emptyList()
}