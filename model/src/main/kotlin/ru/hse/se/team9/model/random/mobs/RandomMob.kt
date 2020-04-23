package ru.hse.se.team9.model.random.mobs

import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.game.entities.mobs.strategies.AggressiveStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.PassiveStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.CowardStrategy
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import kotlin.random.Random

class RandomMob(private val directionGenerator: DirectionGenerator) : // FIXME kostyl for serialization
    MobGenerator {
    private val strategies = listOf(
        AggressiveStrategy(directionGenerator),
        AggressiveStrategy(directionGenerator),
        CowardStrategy(directionGenerator),
        PassiveStrategy
    )

    override fun createMob(): Mob {
        val hp = Random.nextInt(1, MAX_HP)
        val damage = Random.nextInt(1, MAX_DMG)
        val armor = Random.nextInt(MAX_ARM)
        val expGain = Random.nextInt(MAX_EXP_GAIN)
        val strategy = getRandomStrategy()

        return Mob(hp, hp, damage, armor, expGain, strategy)
    }

    private fun getRandomStrategy(): MobStrategy {
        return strategies.random()
    }

    companion object {
        private const val MAX_HP = 30
        private const val MAX_DMG = 10
        private const val MAX_ARM = 10
        private const val MAX_EXP_GAIN = 10
    }
}