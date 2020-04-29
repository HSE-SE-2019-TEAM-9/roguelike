package ru.hse.se.team9.game.entities.hero.effects

import ru.hse.se.team9.game.entities.hero.Hero
import kotlin.math.max
import kotlin.math.min

/** Changes hero health points */
class DeltaHpEffect(private val delta: Int) : Effect {
    override fun invoke(hero: Hero) {
        hero.stats.hp = max(0, min(hero.stats.maxHp, hero.stats.hp + delta))
    }
}