package ru.hse.se.team9.game.entities.hero.inventory

import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.game.entities.hero.inventory.items.Boots
import ru.hse.se.team9.game.entities.hero.inventory.items.Item
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon

class Equipment(
        private var boots: Boots? = null,
        private var underwear: Underwear? = null,
        private var weapon: Weapon? = null
) {
    fun tryEquip(item: Item): Item? {
        val w = weapon
        val u = underwear
        val b = boots

        return when (item) {
            is Weapon -> {
                weapon = item
                w
            }
            is Underwear -> {
                underwear = item
                u
            }
            is Boots -> {
                boots = item
                b
            }
        }
    }

    fun putOffItem(type: ItemType): Item? {
        return when (type) {
            ItemType.BOOTS -> {
                val previous = boots
                boots = null
                previous
            }
            ItemType.WEAPON -> {
                val previous = weapon
                weapon = null
                previous
            }
            ItemType.UNDERWEAR -> {
                val previous = underwear
                underwear = null
                previous
            }
            ItemType.NONE -> null
        }
    }

    fun getItems(): Map<ItemType, Item?> {
        return mapOf(
            ItemType.BOOTS to boots,
            ItemType.WEAPON to weapon,
            ItemType.UNDERWEAR to underwear
        )
    }
}