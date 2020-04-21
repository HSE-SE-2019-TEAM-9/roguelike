package ru.hse.se.team9.model.logic.gamecycle

import arrow.core.Either
import arrow.core.flatMap
import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.effects.DecreaseHpEffect
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.model.random.GameGenerator
import ru.hse.se.team9.utils.get
import ru.hse.se.team9.utils.plus
import kotlin.math.ceil
import kotlin.math.roundToInt

/** Represents all logic within one game -- moves hero, tells if game is finished etc.
 * @property map game map used in this game
 */
class GameCycleLogic(val map: GameMap, private val gameGenerator: GameGenerator) {
    private var tick: Tick = Tick()

    fun processTick(): GameStatus {
        return InProgress
    }

    // VisibleForTesting
    internal fun movePlayer(move: Move): Either<Finished, InProgress> {
        val direction = when (move) {
            is Left -> Direction.LEFT
            is Up -> Direction.UP
            is Right -> Direction.RIGHT
            is Down -> Direction.DOWN
        }

        val hero = map.heroOnMap.hero
        val newHeroPosition = map.heroOnMap.position + direction
        val mob = map.mobs[newHeroPosition]

        if (mob != null) {
            battle(hero, mob)
        } else {
            map.moveHero(direction)
        }
        return Either.Right(InProgress)
    }

    private fun moveMobs(): Either<Finished, InProgress> {
        for ((position, mob) in map.mobs.toList()) {
            val newPosition = mob.makeMove(position, map)
            val hero = map.heroOnMap.hero
            if (map.heroOnMap.position == newPosition) {
                battle(hero, mob)
            } else {
                map.moveMob(position, newPosition)
            }
        }
        return Either.Right(InProgress)
    }

    private fun battle(hero: Hero, mob: Mob) {
        val damageToHero: Double = mob.damage * getDamageReduceMultiplier(hero.stats.armor)
        hero.addEffect(DecreaseHpEffect(ceil(damageToHero).roundToInt()))

        val damageToMob: Double = hero.stats.damage * getDamageReduceMultiplier(mob.armor)
        mob.hp -= ceil(damageToMob).roundToInt()

        mob.applyStrategyModifier(gameGenerator.createModifier())
    }

    private fun removeDeadMobCorpses(): Either<Finished, InProgress> {
        map.mobs.filterValues { it.hp <= 0 }.forEach {
            map.mobs.remove(it.key)
        }
        return if (map.mobs.isEmpty()) {
            Either.left(Win)
        } else {
            Either.right(InProgress)
        }
    }

    private fun runHeroEffects(): Either<Finished, InProgress> {
        map.heroOnMap.hero.runEffects()
        return if (map.heroOnMap.hero.isDead()) {
            Either.left(Loss)
        } else {
            Either.right(InProgress)
        }
    }

    private fun getDamageReduceMultiplier(armor: Int): Double = (1 - armor / MAX_ARMOR)

    fun makeMove(move: Move): GameStatus {
        return Either.right(InProgress)
            .flatMap { movePlayer(move) }
            .flatMap { removeDeadMobCorpses() }
            .flatMap { runHeroEffects() }
            .flatMap { moveMobs() }
            .flatMap { removeDeadMobCorpses() }
            .flatMap { runHeroEffects() }
            .get()
    }

    companion object {
        private const val MAX_ARMOR: Double = 15.0
    }
}