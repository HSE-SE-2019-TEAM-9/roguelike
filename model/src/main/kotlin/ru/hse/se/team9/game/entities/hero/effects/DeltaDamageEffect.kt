package ru.hse.se.team9.game.entities.hero.effects

import ru.hse.se.team9.game.entities.hero.Hero
import kotlin.math.max

class DeltaDamageEffect(private val delta: Int) : Effect {
    override fun invoke(hero: Hero) {
        hero.stats.damage = max(0, hero.stats.damage + delta)
    }
}