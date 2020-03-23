package ru.hse.se.team9.model.logic.gamecycle

import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap

/** Represents all logic within one game -- moves hero, tells if game is finished etc.
 * @property map game map used in this game
 */
class GameCycleLogic(val map: GameMap) {
    private var tick: Tick = Tick()

    private fun processTick(): GameStatus {
        return GameStatus.IN_PROGRESS
    }

    /** Move hero according to passed move
     * @param move move to make
     * @return new game status
     */
    fun movePlayer(move: Move): GameStatus {
        when (move) {
            is Left -> map.moveHero(Direction.LEFT)
            is Up -> map.moveHero(Direction.UP)
            is Right -> map.moveHero(Direction.RIGHT)
            is Down -> map.moveHero(Direction.DOWN)
        }
        return GameStatus.IN_PROGRESS
    }
}