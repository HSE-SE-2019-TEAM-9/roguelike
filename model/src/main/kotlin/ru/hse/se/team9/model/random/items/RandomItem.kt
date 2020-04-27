package ru.hse.se.team9.model.random.items

import ru.hse.se.team9.game.entities.hero.inventory.items.Boots
import ru.hse.se.team9.game.entities.hero.inventory.items.Item
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon
import kotlin.random.Random

object RandomItem : ItemGenerator {
    override fun createItem(): Item = listOf(createWeapon(), createUnderwear(), createBoots()).random()

    override fun createBoots(): Boots {
        val hpGain = Random.nextInt(MAX_HP_GAIN_BOOTS + 1)
        val armorGain = Random.nextInt(MAX_ARMOR_GAIN + 1)
        val greatness = getGreatness(armorGain, MAX_ARMOR_GAIN)
        return Boots(armorGain = armorGain, hpGain = hpGain, name = greatness + " boots of " + getTitle())
    }

    override fun createUnderwear(): Underwear {
        val hpGain = Random.nextInt(MAX_HP_GAIN_UNDIES + 1)
        val armorGain = Random.nextInt(MAX_ARMOR_GAIN + 1)
        val greatness = getGreatness(armorGain, MAX_ARMOR_GAIN)
        return Underwear(armorGain = armorGain, hpGain = hpGain, name = greatness + " panties of " + getTitle())
    }

    override fun createWeapon(): Weapon {
        val dmgGain = Random.nextInt(MAX_DMG_GAIN + 1)
        val greatness = getGreatness(dmgGain, MAX_DMG_GAIN)
        return Weapon(dmgGain = dmgGain, name = greatness + " sword of " + getTitle())
    }

    private fun getGreatness(actualValue: Int, maxValue: Int): String {
        if (actualValue * 3 < maxValue) {
            return "Simple"
        }

        if (actualValue * 3 < maxValue * 2) {
            return "Medium"
        }

        if (actualValue < maxValue) {
            return "Great"
        }

        return "Absolute"
    }

    private fun getTitle(): String {
        return TITLES.random()
    }

    private const val MAX_HP_GAIN_UNDIES = 10
    private const val MAX_HP_GAIN_BOOTS = MAX_HP_GAIN_UNDIES / 2
    private const val MAX_DMG_GAIN = 20
    private const val MAX_ARMOR_GAIN = 5
    private val TITLES = listOf("Depression", "Sadness", "Sorrow", "Despair", "Regret", "Grief", "Misery", "Suffering")
}