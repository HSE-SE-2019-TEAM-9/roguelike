package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.plus

/** Attacks hero if it is close to mob. */
class AggressiveStrategy(private val directionGenerator: DirectionGenerator) : MobStrategy {
    /**
     * Goes towards hero if it is closer than AGGRESSION_DISTANCE.
     * Otherwise makes move with directionGenerator.
     */
    override fun makeMove(position: Position, map: GameMap): Pair<Position, MobStrategy> {
        val heroes = map.heroes.values

        fun getDistanceToNearestHero(position: Position): Int? =
            heroes.map { map.distance(position, it.position) }.min()

        return if (heroes.any { map.distance(position, it.position) <= AGGRESSION_DISTANCE }) {
            val newPosition = Direction.values()
                .toList()
                .map { position + it }
                .filter { map.mobCanMoveTo(it) }
                .minBy { newPosition -> getDistanceToNearestHero(newPosition) ?: 0 }
                ?: position + Direction.UP
            Pair(newPosition, this)
        } else {
            Pair(position + directionGenerator.createDirection(), this)
        }
    }

    override fun getProperties(): List<MobProperty> = emptyList()

    companion object {
        private const val AGGRESSION_DISTANCE: Int = 5
    }
}
