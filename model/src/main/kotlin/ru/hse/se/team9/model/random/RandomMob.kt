package ru.hse.se.team9.model.random

import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.PassiveStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.RandomStrategy
import kotlin.random.Random

class RandomMob(private val directionGenerator: DirectionGenerator) : MobGenerator {
    override fun createMob(): Mob {
        val hp = Random.nextInt(10)
        val damage = Random.nextInt(10)
        val armor = Random.nextInt(10)
        val expGain = Random.nextInt(10)
        val strategy = getRandomStrategy()

        return Mob(hp, damage, armor, expGain, strategy)
    }

    // TODO think of some adequate generation
    private fun getRandomStrategy(): MobStrategy {
        return when (Random.nextInt(3)) {
            0 -> PassiveStrategy
            else -> RandomStrategy(directionGenerator)
        }
    }
}