package ru.hse.se.team9.game.entities.hero.consumables

import ru.hse.se.team9.game.entities.hero.effects.DeltaHpEffect

/** An object which is immediately consumed by hero when he steps on it. */
class Consumable(private val hpGain: Int = 0) {
    /** Returns an effect which has to be applied to hero when he consumes that object. */
    fun getEffect() =  DeltaHpEffect(hpGain)
}
