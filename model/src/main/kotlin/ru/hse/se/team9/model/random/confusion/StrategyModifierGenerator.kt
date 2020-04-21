package ru.hse.se.team9.model.random.confusion

import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy

interface StrategyModifierGenerator {
    fun createModifier(): (MobStrategy) -> MobStrategy
}