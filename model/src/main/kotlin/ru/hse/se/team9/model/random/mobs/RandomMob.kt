package ru.hse.se.team9.model.random.mobs

import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.PassiveStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.RandomStrategy
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import kotlin.random.Random

class RandomMob(directionGenerator: DirectionGenerator) :
    MobGenerator {
    private val strategies = listOf(RandomStrategy(directionGenerator), PassiveStrategy)

    override fun createMob(): Mob {
        val hp = Random.nextInt(MAX_HP)
        val damage = Random.nextInt(MAX_DMG)
        val armor = Random.nextInt(MAX_ARM)
        val expGain = Random.nextInt(MAX_EXP_GAIN)
        val strategy = getRandomStrategy()

        return Mob(hp, damage, armor, expGain, strategy)
    }

    private fun getRandomStrategy(): MobStrategy {
        return strategies.random()
    }

    companion object {
        private const val MAX_HP = 10
        private const val MAX_DMG = 10
        private const val MAX_ARM = 10
        private const val MAX_EXP_GAIN = 10
    }
}