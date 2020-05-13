package ru.hse.se.team9.model.logic.gamecycle

import arrow.core.Either
import arrow.core.flatMap
import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.effects.DeltaHpEffect
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.MapViewImpl
import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.model.generators.GameGenerator
import ru.hse.se.team9.utils.get
import ru.hse.se.team9.utils.plus
import kotlin.math.ceil
import kotlin.math.roundToInt

/** Represents all logic within one game -- moves heroes, tells if game is finished etc.
 * @property map game map used in this game
 */
class GameCycleProcessor(
        val map: GameMap, // visible for testing
        private val gameGenerator: GameGenerator,
        private val generateNewObjects: Boolean = true
) {
    // VisibleForTesting
    internal fun movePlayer(heroId: Int, move: Move): Either<Finished, InProgress> {
        val direction = when (move) {
            is Left -> Direction.LEFT
            is Up -> Direction.UP
            is Right -> Direction.RIGHT
            is Down -> Direction.DOWN
        }

        val hero = map.heroes[heroId]?.hero ?: throw IllegalArgumentException("no such hero exists")
        val newHeroPosition = map.heroes[heroId]!!.position + direction
        val mob = map.mobs[newHeroPosition]

        if (mob != null) {
            battle(hero, mob)
        } else {
            map.moveHero(heroId, direction)
        }
        return Either.Right(InProgress)
    }

    private fun moveMobs(): Either<Finished, InProgress> {
        for ((position, mob) in map.mobs.toList()) {
            val newPosition = mob.makeMove(position, map)
            val hero = map.heroes.values.firstOrNull { it.position == newPosition }
            if (hero != null) {
                battle(hero.hero, mob)
            } else {
                map.moveMob(position, newPosition)
            }
        }
        return Either.Right(InProgress)
    }

    private fun battle(hero: Hero, mob: Mob) {
        val damageToHero: Double = mob.damage * getDamageReduceMultiplier(hero.stats.armor)
        hero.addEffect(DeltaHpEffect(-ceil(damageToHero).roundToInt()))

        val damageToMob: Double = hero.stats.damage * getDamageReduceMultiplier(mob.armor)
        mob.hp -= ceil(damageToMob).roundToInt()

        mob.applyStrategyModifier(gameGenerator.createModifier())
    }

    private fun removeDeadMobCorpses(): Either<Finished, InProgress> {
        map.mobs.filterValues { it.hp <= 0 }.forEach {
            map.removeMob(it.key)
        }
        return if (map.mobs.isEmpty()) {
            Either.left(Win)
        } else {
            Either.right(InProgress)
        }
    }

    private fun runHeroEffects(): Either<Finished, InProgress> {
        map.heroes.values.map { it.hero }.forEach { it.runEffects() }
        return if (map.heroes.values.all {it.hero.isDead()} ) {
            Either.left(Loss)
        } else {
            Either.right(InProgress)
        }
    }

    private fun pickupObjects(): Either<Finished, InProgress> {
        for (heroOnMap in map.heroes.values) {
            val position = heroOnMap.position
            map.items.remove(position)?.let { heroOnMap.hero.pickupItem(it) }
            map.consumables.remove(position)?.let { heroOnMap.hero.addEffect(it.getEffect()) }
        }
        return Either.right(InProgress)
    }

    private fun generateMapObjects(): Either<Finished, InProgress> {
        if (generateNewObjects) {
            map.generateObjects()
        }
        return Either.right(InProgress)
    }

    private fun getDamageReduceMultiplier(armor: Int): Double = (1 - armor / MAX_ARMOR)

    /**
     * Makes one game move. One game move consists of 6 stages:
     *
     * 1. Player tries to make a move. If that cell is empty, then player successfully makes the move.
     * if the cell is occupied by mob, then a battle occurs. Mobs get instant damage. Damage for hero is
     * added to effects
     *
     * 2. Dead mobs are deleted from maps. If all mobs are dead then player wins.
     *
     * 3. Accumulated hero effects are applied (e.g. HP decreases)
     *
     * 4. Mobs try to make a move according to their strategy. The logic is equivalent to step 1.
     *
     * 5. Dead mobs are deleted from maps. If all mobs are dead then player wins.
     *
     * 6. Accumulated hero effects are applied (e.g. HP decreases)
     */
    fun makeMove(heroId: Int, move: Move): GameStatus {
        return Either.right(InProgress)
            .flatMap { movePlayer(heroId, move) }
            .flatMap { removeDeadMobCorpses() }
            .flatMap { pickupObjects() }
            .flatMap { runHeroEffects() }
            .flatMap { moveMobs() }
            .flatMap { removeDeadMobCorpses() }
            .flatMap { runHeroEffects() }
            .flatMap { generateMapObjects() }
            .get()
    }

    /**
     * Returns map as it is seen from the point view of the specified hero.
     * Such map contains this hero's inventory, fog of war, etc.
     *
     * @param heroId id of a hero
     * @return mapView view on the map from the perspective of a hero
     */
    fun getCurrentMap(heroId: Int): MapView = MapViewImpl(heroId, map)

    /**
     * Puts on an item in the inventory of a hero
     *
     * @param heroId id of a hero who puts an item on
     * @param index index of an item in the inventory of a hero
     */
    fun putOnItem(heroId: Int, index: Int) {
        val hero = map.heroes[heroId]?.hero ?: throw IllegalArgumentException("no such hero exists")
        hero.equipItem(index)
        hero.runEffects()
    }

    /**
     * Puts off an item in the equipment of a hero
     *
     * @param heroId id of a hero who puts an item off
     * @param type type of an item to put off (one of Boots, Underwear or Weapon)
     */
    fun putOffItem(heroId: Int, type: ItemType) {
        val hero = map.heroes[heroId]?.hero ?: throw IllegalArgumentException("no such hero exists")
        hero.unEquipItem(type)
        hero.runEffects()
    }

    companion object {
        private const val MAX_ARMOR: Double = 15.0
    }
}