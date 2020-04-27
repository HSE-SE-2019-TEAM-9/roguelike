package ru.hse.se.team9.game.entities.hero.inventory

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
        when (item) {
            is Weapon -> if (weapon == null) {
                weapon = item
                return w
            }
            is Underwear -> if (underwear != null) {
                underwear = item
                return u
            }
            is Boots -> if (boots != null) {
                boots = item
                return b
            }
        }
        return null
    }

    fun getItems(): List<Item?> {
        return listOf(boots, underwear, weapon)
    }
}