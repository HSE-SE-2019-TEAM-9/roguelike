package ru.hse.se.team9.game.entities.hero

import ru.hse.se.team9.game.entities.hero.effects.Effect

/** Represents hero in game model. Stores all properties of hero needed in game
 * @property stats hero stats, e.g. experience, health points etc.
 */
data class Hero(val stats: HeroStats, private var effects: MutableList<Effect> = mutableListOf()) {
    fun addEffect(effect: Effect) {
        effects.add(effect)
    }

    fun runEffects() {
        for (effect in effects) {
            effect(this)
        }
        effects = mutableListOf()
    }

    fun isDead(): Boolean = stats.hp <= 0
}