package ru.hse.se.team9.game.entities.map.objects

import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.positions.Position

/**
 * Represents hero as it is placed on map
 * @param hero the hero itself
 * @param position position of hero on map
 */
class HeroOnMap(val hero: Hero, var position: Position)
