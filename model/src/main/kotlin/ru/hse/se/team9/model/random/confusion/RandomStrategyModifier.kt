package ru.hse.se.team9.model.random.confusion

import ru.hse.se.team9.game.entities.mobs.strategies.ConfusedStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import kotlin.random.Random

class RandomStrategyModifier(private val directionGenerator: DirectionGenerator) : StrategyModifierGenerator {
    override fun createModifier(): (MobStrategy) -> MobStrategy =
        if (Random.nextBoolean()) {
            { mobStrategy ->
                ConfusedStrategy(
                    mobStrategy,
                    directionGenerator,
                    Random.nextInt(MAX_CONFUSED_MOVES)
                )
            }
        } else {
            { it }
        }


    companion object {
        private const val MAX_CONFUSED_MOVES: Int = 10
    }
}