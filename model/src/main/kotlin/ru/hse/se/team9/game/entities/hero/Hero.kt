package ru.hse.se.team9.game.entities.hero

import ru.hse.se.team9.game.entities.hero.effects.Effect

/**
 * Represents hero in game model. Stores all properties of hero needed in game
 * @property stats hero stats, e.g. experience, health points etc.
 * @property effects accumulated effects
 */
data class Hero(val stats: HeroStats, private var effects: MutableList<Effect> = mutableListOf()) {
    /** Adds effect to effect list (they will be applied later) */
    fun addEffect(effect: Effect) {
        effects.add(effect)
    }

    /** Runs all accumulated effects. Clears effects list after. */
    fun runEffects() {
        for (effect in effects) {
            effect(this)
        }
        effects = mutableListOf()
    }

    /** Checks if the hp is below zero */
    fun isDead(): Boolean = stats.hp <= 0
}