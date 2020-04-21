package ru.hse.se.team9.game.entities.hero.effects

import ru.hse.se.team9.game.entities.hero.Hero

/** An arbitrary delayed modification of player */
interface Effect {
    operator fun invoke(hero: Hero)
}