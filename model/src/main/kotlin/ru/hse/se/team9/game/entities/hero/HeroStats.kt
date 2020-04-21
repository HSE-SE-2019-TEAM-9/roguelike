package ru.hse.se.team9.game.entities.hero

/**
 * Stores hero stats
 * @property hp current health points
 * @property maxHp maximal health points of hero at the current level
 * @property armor armor value
 * @property damage damage dealt within one hit
 * @property exp current experience at this level
 * @property lvl current level
 */
data class HeroStats(var hp: Int, var maxHp: Int, var armor: Int, var damage: Int, var exp: Int, var lvl: Int)
