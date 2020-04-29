package ru.hse.se.team9.game.entities.hero.inventory.items

import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.ItemView

/** Adapts Item to ItemView interface. */
class ItemViewImpl(item: Item?) : ItemView {
    override val armor = item?.armorGain ?: 0
    override val damage = item?.dmgGain ?: 0
    override val hp = item?.hpGain ?: 0
    override val name = item?.name ?: ""
    override val type = getType(item)

    private fun getType(item: Item?): ItemType {
        if (item == null) {
            return ItemType.NONE
        }
        return when (item) {
            is Boots -> ItemType.BOOTS
            is Weapon -> ItemType.WEAPON
            is Underwear -> ItemType.UNDERWEAR
        }
    }
}
