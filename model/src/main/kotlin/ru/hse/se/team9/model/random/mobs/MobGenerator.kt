package ru.hse.se.team9.model.random.mobs

import ru.hse.se.team9.game.entities.mobs.Mob

/** A common interface for all generators of Mobs. Generators can be either random or deterministic. */
interface MobGenerator {
    /** Generates one new Mob */
    fun createMob(): Mob
}