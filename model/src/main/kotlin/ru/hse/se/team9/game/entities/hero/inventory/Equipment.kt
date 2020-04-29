package ru.hse.se.team9.game.entities.hero.inventory

import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.game.entities.hero.inventory.items.Boots
import ru.hse.se.team9.game.entities.hero.inventory.items.Item
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon

/**
 * Currently equipped items which affect stats of the hero.
 * Contains only one item per type.
 */
class Equipment(
        private var boots: Boots? = null,
        private var underwear: Underwear? = null,
        private var weapon: Weapon? = null
) {
    /**
     * Places specified item to the equipment
     * and returns previously equipped item of that type or null if there is none.
     */
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

    /**
     * Tries to remove item of the specified type from the equipment.
     * @return removed item or null if no item is removed.
     **/
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

    /** Returns all currently equipped items. */
    fun getItems(): Map<ItemType, Item?> {
        return mapOf(
            ItemType.BOOTS to boots,
            ItemType.WEAPON to weapon,
            ItemType.UNDERWEAR to underwear
        )
    }
}