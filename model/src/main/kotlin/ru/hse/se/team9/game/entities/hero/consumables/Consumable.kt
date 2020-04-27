package ru.hse.se.team9.game.entities.hero.consumables

import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.effects.Effect
import kotlin.math.min

sealed class Consumable(val hpGain: Int = 0) {
    fun getEffect(): Effect = object : Effect {
        override fun invoke(hero: Hero) {
            hero.stats.hp += hpGain
            hero.stats.hp = min(hero.stats.hp, hero.stats.maxHp)
        }
    }
}
