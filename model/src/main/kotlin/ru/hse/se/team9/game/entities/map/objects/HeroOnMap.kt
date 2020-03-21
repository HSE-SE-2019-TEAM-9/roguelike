package ru.hse.se.team9.game.entities.map.objects

import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.positions.Position

data class HeroOnMap(val hero: Hero, var position: Position)
