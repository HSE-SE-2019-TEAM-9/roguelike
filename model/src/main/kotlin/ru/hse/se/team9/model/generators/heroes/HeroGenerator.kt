package ru.hse.se.team9.model.generators.heroes

import ru.hse.se.team9.game.entities.hero.Hero

interface HeroGenerator {
    fun createHero(): Hero
}