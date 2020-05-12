package ru.hse.se.team9.model.generators.confusion

import ru.hse.se.team9.game.entities.mobs.strategies.ConfusedStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.model.generators.directions.DirectionGenerator
import kotlin.random.Random

/**
 * An implementation of StrategyModifierGenerator which creates ConfusedStrategy or
 * identity modifier with equal (0.5) probabilities
 */
class RandomStrategyModifier(private val directionGenerator: DirectionGenerator) : StrategyModifierGenerator {
    /** Creates confusion strategy modifier or identity modifer */
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