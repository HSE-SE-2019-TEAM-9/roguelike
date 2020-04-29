package ru.hse.se.team9.game.entities.hero

import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.game.entities.hero.effects.Effect
import ru.hse.se.team9.game.entities.hero.inventory.Equipment
import ru.hse.se.team9.game.entities.hero.inventory.items.Item

/**
 * Represents hero in game model. Stores all properties of hero needed in game
 * @property stats hero stats, e.g. experience, health points etc.
 * @property effects accumulated effects
 * @property inventory all items collected by hero
 */
data class Hero(
        val stats: HeroStats,
        private val effects: MutableList<Effect> = mutableListOf(),
        val inventory: MutableList<Item> = mutableListOf(),
        val equipment: Equipment = Equipment()
) {
    /** Adds effect to effect list (they will be applied later) */
    fun addEffect(effect: Effect) {
        effects.add(effect)
    }

    private fun addEffects(effects: List<Effect>) {
        this.effects.addAll(effects)
    }

    /** Runs all accumulated effects. Clears effects list after. */
    fun runEffects() {
        for (effect in effects) {
            effect(this)
        }
        effects.clear()
    }

    /** Adds item to inventory */
    fun pickupItem(item: Item) {
        inventory.add(item)
    }

    /** Places equipped item of the specified type to invetory */
    fun unEquipItem(type: ItemType) {
        equipment.putOffItem(type)?.let {
            addEffects(it.getRemoveEffect())
            pickupItem(it)
        }
    }

    /**
     * Removes item from inventory and places to the equipment.
     * Previously equiped item of that time is returned to the inventory.
     */
    fun equipItem(index: Int) {
        if (index >= inventory.size || index < 0) {
            throw ArrayIndexOutOfBoundsException("no item with such index")
        }

        val previous = equipment.tryEquip(inventory[index])
        addEffects(inventory[index].getEquipEffect())
        if (previous == null) {
            inventory.removeAt(index)
        } else {
            inventory[index] = previous
            addEffects(previous.getRemoveEffect())
        }
    }

    /** Checks if the hp is below zero */
    fun isDead(): Boolean = stats.hp <= 0
}