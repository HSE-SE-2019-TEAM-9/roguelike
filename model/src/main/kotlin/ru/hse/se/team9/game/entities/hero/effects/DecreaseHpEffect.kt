package ru.hse.se.team9.game.entities.hero.effects

import ru.hse.se.team9.game.entities.hero.Hero
import kotlin.math.max

/** Decreases hero health points */
class DecreaseHpEffect(private val number: Int) : Effect {
    override fun invoke(hero: Hero) {
        hero.stats.hp = max(hero.stats.hp - number, 0)
    }
}