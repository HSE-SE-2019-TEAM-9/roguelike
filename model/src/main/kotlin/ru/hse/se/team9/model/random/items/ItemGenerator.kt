package ru.hse.se.team9.model.random.items

import ru.hse.se.team9.game.entities.hero.inventory.items.Boots
import ru.hse.se.team9.game.entities.hero.inventory.items.Item
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon

interface ItemGenerator {
    fun createItem(): Item
    fun createBoots(): Boots
    fun createUnderwear(): Underwear
    fun createWeapon(): Weapon
}