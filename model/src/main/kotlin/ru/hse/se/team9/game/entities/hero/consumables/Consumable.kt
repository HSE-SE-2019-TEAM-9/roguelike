package ru.hse.se.team9.game.entities.hero.consumables

import ru.hse.se.team9.game.entities.hero.effects.DeltaHpEffect

class Consumable(private val hpGain: Int = 0) {
    fun getEffect() =  DeltaHpEffect(hpGain)
}
