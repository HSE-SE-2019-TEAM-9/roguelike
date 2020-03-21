package ru.hse.se.team9.game.entities.mobs

import ru.hse.se.team9.entities.GameObject
import ru.hse.se.team9.entities.MobType
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy

data class Mob(val hp: Int, val damage: Int, val armor: Int, val expGain: Int, val strategy: MobStrategy): GameObject {
    private val id: Int
    val type: MobType

    init {
        id = mobId++
        type = when (hp) {
            in 1..3 -> MobType.SMALL
            in 4..6 -> MobType.MEDIUM
            else -> MobType.BIG
        }
    }

    companion object {
        var mobId: Int = 0
    }
}