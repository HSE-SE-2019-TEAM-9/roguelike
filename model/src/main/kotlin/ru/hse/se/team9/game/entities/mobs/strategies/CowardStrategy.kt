package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.generators.directions.DirectionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.plus

/** Flees from a nearby hero. */
class CowardStrategy(private val directionGenerator: DirectionGenerator) : MobStrategy {
    /**
     * Flees from hero if it is closer than FEAR_DISTANCE.
     * Otherwise makes move with directionGenerator.
     */
    override fun makeMove(position: Position, map: GameMap): Pair<Position, MobStrategy> {
        val heroes = map.heroes.values

        fun getDistanceToNearestHero(position: Position): Int? =
            heroes.map { map.distance(position, it.position) }.min()

        return if (heroes.any { map.distance(position, it.position) <= FEAR_DISTANCE }) {
            val newPosition = Direction.values()
                .toList()
                .map { position + it }
                .filter { map.mobCanMoveTo(it) }
                .maxBy { newPosition -> getDistanceToNearestHero(newPosition) ?: 0 }
                ?: position + Direction.UP
            Pair(newPosition, this)
        } else {
            Pair(position + directionGenerator.createDirection(), this)
        }
    }

    override fun getProperties(): List<MobProperty> = emptyList()

    companion object {
        private const val FEAR_DISTANCE: Int = 5
    }
}
