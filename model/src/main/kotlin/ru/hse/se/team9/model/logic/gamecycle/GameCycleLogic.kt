package ru.hse.se.team9.model.logic.gamecycle

import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap

class GameCycleLogic(val map: GameMap) {
    private var tick: Tick = Tick()

    fun processTick(): GameStatus {
        return GameStatus.IN_PROGRESS
    }

    fun movePlayer(move: Move): GameStatus {
        when (move) {
            is Left -> map.moveHero(Direction.LEFT)
            is Up -> map.moveHero(Direction.UP)
            is Right -> map.moveHero(Direction.RIGHT)
            is Down -> map.moveHero(Direction.DOWN)
        }
        return GameStatus.IN_PROGRESS
    }

    fun moveMobs(): GameStatus {
        for (mobOnMap in map.mobs) {
            val newPosition = mobOnMap.mob.strategy.makeMove(mobOnMap, map)
            map.moveMob(mobOnMap, newPosition)
        }
        return GameStatus.IN_PROGRESS
    }
}