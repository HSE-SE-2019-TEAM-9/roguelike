package ru.hse.se.team9.model.random.items

import ru.hse.se.team9.game.entities.hero.inventory.items.Boots
import ru.hse.se.team9.game.entities.hero.inventory.items.Item
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon

/** A common interface for all generators of Items. Generators can be either random or deterministic. */
interface ItemGenerator {
    /** creates any Item (Boots, Underwear, Weapon) */
    fun createItem(): Item

    /** creates Boots item */
    fun createBoots(): Boots

    /** creates Underwear item */
    fun createUnderwear(): Underwear

    /** creates Weapon item */
    fun createWeapon(): Weapon
}