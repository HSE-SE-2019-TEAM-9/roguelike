package ru.hse.se.team9.game.entities.hero.effects

import ru.hse.se.team9.game.entities.hero.Hero

class IncreaseXpEffect(private val number: Int) : Effect {
    override fun invoke(hero: Hero) {
        hero.stats.exp += number
    }
}