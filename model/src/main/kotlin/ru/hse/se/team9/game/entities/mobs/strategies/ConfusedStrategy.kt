package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.entities.MobModifier
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.plus

class ConfusedStrategy(
    private val innerStrategy: MobStrategy,
    private val directionGenerator: DirectionGenerator,
    private val invocationCount: Int = 0
) : MobStrategy {

    override fun makeMove(position: Position, map: GameMap): Pair<Position, MobStrategy> {
        val (newInnerPos, newInnerStrategy) = innerStrategy.makeMove(position, map)
        val confusedPos = position + directionGenerator.createDirection()

        return if (invocationCount < MAX_INVOCATIONS) {
            Pair(confusedPos, ConfusedStrategy(newInnerStrategy, directionGenerator, invocationCount + 1))
        } else {
            Pair(confusedPos, newInnerStrategy)
        }
    }

    override fun getModifiers(): List<MobModifier> =
        innerStrategy.getModifiers().plus(MobModifier.CONFUSED)

    companion object ConfusedStrategy {
        private const val MAX_INVOCATIONS = 10
    }
}