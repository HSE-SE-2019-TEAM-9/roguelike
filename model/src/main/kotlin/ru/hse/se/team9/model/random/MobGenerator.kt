package ru.hse.se.team9.model.random

import ru.hse.se.team9.game.entities.mobs.Mob

interface MobGenerator {
    fun createMob(): Mob
}