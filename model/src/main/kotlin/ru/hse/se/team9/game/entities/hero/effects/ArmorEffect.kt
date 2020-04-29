package ru.hse.se.team9.game.entities.hero.effects

import ru.hse.se.team9.game.entities.hero.Hero

/** Changes armor points and hp points */
class ArmorEffect(private val deltaArmor: Int, private val hpGain: Int = 0) : Effect {
    override fun invoke(hero: Hero) {
        hero.stats.armor += deltaArmor
        hero.stats.maxHp += hpGain
    }
}