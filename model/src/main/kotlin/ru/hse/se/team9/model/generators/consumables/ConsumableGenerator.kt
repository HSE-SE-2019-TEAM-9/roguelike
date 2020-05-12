package ru.hse.se.team9.model.generators.consumables

import ru.hse.se.team9.game.entities.hero.consumables.Consumable

/** A common interface for all generators of Consumables. Generators can be either random or deterministic. */
interface ConsumableGenerator {
    /** Generates consumable */
    fun createConsumable(): Consumable
}