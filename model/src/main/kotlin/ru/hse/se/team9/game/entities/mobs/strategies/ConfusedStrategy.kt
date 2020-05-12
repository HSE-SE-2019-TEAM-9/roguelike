package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.generators.directions.DirectionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.plus

/** Makes some predetermined number of random moves. Then returns to inner strategy */
class ConfusedStrategy(
    private val innerStrategy: MobStrategy,
    private val directionGenerator: DirectionGenerator,
    private val invocationCount: Int = 0
) : MobStrategy {

    /**
     * Makes random move. If number of invocations is more than MAX_INVOCATIONS then returns underlying strategy.
     * otherwise returns confused strategy
     */
    override fun makeMove(position: Position, map: GameMap): Pair<Position, MobStrategy> {
        val (_, newInnerStrategy) = innerStrategy.makeMove(position, map)
        val confusedPos = position + directionGenerator.createDirection()

        return if (invocationCount < MAX_INVOCATIONS) {
            Pair(confusedPos, ConfusedStrategy(newInnerStrategy, directionGenerator, invocationCount + 1))
        } else {
            Pair(confusedPos, newInnerStrategy)
        }
    }

    /** Returns special confused mob property */
    override fun getProperties(): List<MobProperty> =
        innerStrategy.getProperties().plus(MobProperty.CONFUSED)

    companion object ConfusedStrategy {
        private const val MAX_INVOCATIONS = 10
    }
}