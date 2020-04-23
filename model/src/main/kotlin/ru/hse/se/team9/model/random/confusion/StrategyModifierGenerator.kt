package ru.hse.se.team9.model.random.confusion

import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy

/**
 * A common interface for all generators of strategy modifiers (functions from strategy to strategy).
 * Generators can be either random or deterministic.
 */
interface StrategyModifierGenerator {
    /** Generates one strategy modifier */
    fun createModifier(): (MobStrategy) -> MobStrategy
}