package ru.hse.se.team9.game.entities.hero

import ru.hse.se.team9.entities.GameObject

/** Represents hero in game model. Stores all properties of hero needed in game
 * @property stats hero stats, e.g. experience, health points etc.
 */
data class Hero(val stats: HeroStats): GameObject