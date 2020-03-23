package ru.hse.se.team9.model.random.mobs

import ru.hse.se.team9.game.entities.mobs.Mob

interface MobGenerator {
    fun createMob(): Mob
}