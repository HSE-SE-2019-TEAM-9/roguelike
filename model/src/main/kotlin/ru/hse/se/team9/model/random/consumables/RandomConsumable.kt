package ru.hse.se.team9.model.random.consumables

import ru.hse.se.team9.game.entities.hero.consumables.Consumable
import kotlin.random.Random

object RandomConsumable : ConsumableGenerator {
    override fun createConsumable(): Consumable = Consumable(Random.nextInt(1, 10))
}