package ru.hse.se.team9.game.entities.hero.inventory.items

import ru.hse.se.team9.game.entities.hero.effects.ArmorEffect
import ru.hse.se.team9.game.entities.hero.effects.DeltaDamageEffect
import ru.hse.se.team9.game.entities.hero.effects.DeltaHpEffect
import ru.hse.se.team9.game.entities.hero.effects.Effect

/** A wearable object which can affect stats of the hero. */
sealed class Item(
    val armorGain: Int = 0,
    val hpGain: Int = 0,
    val dmgGain: Int = 0,
    val name: String
) {
    /** Returns an Effect which has to be applied when hero equips that item */
    fun getEquipEffect(): List<Effect> =
        listOf(ArmorEffect(armorGain, hpGain), DeltaDamageEffect(dmgGain))


    /** Returns an Effect which has to be applied when hero unequips that item */
    fun getRemoveEffect(): List<Effect> =
        listOf(ArmorEffect(-armorGain, -hpGain), DeltaDamageEffect(-dmgGain))
}

/** An item which can be placed to the Weapon slot */
class Weapon(armorGain: Int = 0, hpGain: Int = 0, dmgGain: Int = 0, name: String) :
    Item(armorGain, hpGain, dmgGain, name)

/** An item which can be placed to the Underwear slot */
class Underwear(armorGain: Int = 0, hpGain: Int = 0, dmgGain: Int = 0, name: String) :
    Item(armorGain, hpGain, dmgGain, name)

/** An item which can be placed to the Boots slot */
class Boots(armorGain: Int = 0, hpGain: Int = 0, dmgGain: Int = 0, name: String) :
    Item(armorGain, hpGain, dmgGain, name)
