package ru.hse.se.team9.model.generators.consumables

import ru.hse.se.team9.game.entities.hero.consumables.Consumable
import kotlin.random.Random

/**
 * An implementation of ConsumableGenerator which uses kotlin.random.Random and generates random consumables.
 * Generated consumables always heal the hero.
 */
object RandomConsumable : ConsumableGenerator {
    /** Generates random healing consumable */
    override fun createConsumable(): Consumable = Consumable(Random.nextInt(1, 10))
}