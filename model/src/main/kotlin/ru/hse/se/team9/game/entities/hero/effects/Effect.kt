package ru.hse.se.team9.game.entities.hero.effects

import ru.hse.se.team9.game.entities.hero.Hero

interface Effect {
    operator fun invoke(hero: Hero)
}