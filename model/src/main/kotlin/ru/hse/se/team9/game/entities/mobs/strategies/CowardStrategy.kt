package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.entities.MobModifier
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.plus

class CowardStrategy(private val directionGenerator: DirectionGenerator) : MobStrategy {
    override fun makeMove(position: Position, map: GameMap): Pair<Position, MobStrategy> =
        if (map.distance(position, map.heroOnMap.position) <= FEAR_DISTANCE) {
            val direction = Direction.values()
                .toList()
                .filter { map.canMoveTo(position + it) }
                .maxBy { map.distance(position + it, map.heroOnMap.position) } ?: Direction.UP
            Pair(position + direction, this)
        } else {
            Pair(position + directionGenerator.createDirection(), this)
        }

    override fun getModifiers(): List<MobModifier> = emptyList()

    companion object {
        private const val FEAR_DISTANCE: Int = 5
    }
}
