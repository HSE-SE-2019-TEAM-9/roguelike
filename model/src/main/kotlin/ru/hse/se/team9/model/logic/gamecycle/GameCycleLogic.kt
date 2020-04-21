package ru.hse.se.team9.model.logic.gamecycle

import arrow.core.Either
import arrow.core.flatMap
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap

/** Represents all logic within one game -- moves hero, tells if game is finished etc.
 * @property map game map used in this game
 */
class GameCycleLogic(val map: GameMap) {
    private var tick: Tick = Tick()

    fun processTick(): GameStatus {
        return InProgress
    }

    // VisibleForTesting
    internal fun movePlayer(move: Move): Either<Finished, InProgress> {
        when (move) {
            is Left -> map.moveHero(Direction.LEFT)
            is Up -> map.moveHero(Direction.UP)
            is Right -> map.moveHero(Direction.RIGHT)
            is Down -> map.moveHero(Direction.DOWN)
        }
        return Either.Right(InProgress)
    }

    private fun moveMobs(): Either<Finished, InProgress> {
        for (mobOnMap in map.mobs) {
            val newPosition = mobOnMap.mob.strategy.makeMove(mobOnMap, map)
            map.moveMob(mobOnMap, newPosition)
        }
        return Either.Right(InProgress)
    }

    fun makeMove(move: Move): GameStatus {
        // TODO fix
        val status = movePlayer(move).flatMap { moveMobs() }.fold({ it }, { it })
        return status
    }
}