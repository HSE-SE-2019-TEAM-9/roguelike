package ru.hse.se.team9.game.entities.hero.inventory.items

import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.effects.Effect

sealed class Item(
        val armorGain: Int = 0,
        val hpGain: Int = 0,
        val dmgGain: Int = 0,
        val name: String
) {
    fun getEquipEffect() = object : Effect {
        override fun invoke(hero: Hero) {
            hero.stats.armor += armorGain
            hero.stats.maxHp += hpGain
            hero.stats.damage += dmgGain
        }
    }

    fun getRemoveEffect() = object : Effect {
        override fun invoke(hero: Hero) {
            hero.stats.armor -= armorGain
            hero.stats.maxHp -= hpGain
            hero.stats.damage -= dmgGain
        }
    }
}

class Weapon(armorGain: Int = 0, hpGain: Int = 0, dmgGain: Int = 0, name: String) :
        Item(armorGain, hpGain, dmgGain, name)
class Underwear(armorGain: Int = 0, hpGain: Int = 0, dmgGain: Int = 0, name: String) :
        Item(armorGain, hpGain, dmgGain, name)
class Boots(armorGain: Int = 0, hpGain: Int = 0, dmgGain: Int = 0, name: String) :
        Item(armorGain, hpGain, dmgGain, name)
