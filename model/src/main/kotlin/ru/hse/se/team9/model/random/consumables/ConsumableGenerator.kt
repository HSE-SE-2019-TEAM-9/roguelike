package ru.hse.se.team9.model.random.consumables

import ru.hse.se.team9.game.entities.hero.consumables.Consumable
import ru.hse.se.team9.game.entities.hero.inventory.items.Boots
import ru.hse.se.team9.game.entities.hero.inventory.items.Item
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon

/** A common interface for all generators of Consumables. Generators can be either random or deterministic. */
interface ConsumableGenerator {
    /** Generates consumable */
    fun createConsumable(): Consumable
}