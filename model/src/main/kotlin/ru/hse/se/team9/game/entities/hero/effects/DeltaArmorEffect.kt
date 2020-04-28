package ru.hse.se.team9.game.entities.hero.effects

import ru.hse.se.team9.game.entities.hero.Hero

class DeltaArmorEffect(private val delta: Int) : Effect {
    override fun invoke(hero: Hero) {
        hero.stats.armor += delta
    }
}