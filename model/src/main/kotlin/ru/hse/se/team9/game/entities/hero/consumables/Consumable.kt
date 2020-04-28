package ru.hse.se.team9.game.entities.hero.consumables

import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.effects.DeltaHpEffect
import ru.hse.se.team9.game.entities.hero.effects.Effect
import kotlin.math.min

class Consumable(private val hpGain: Int = 0) {
    fun getEffect() =  DeltaHpEffect(hpGain)
}
