package ru.hse.se.team9.model.logic.gamecycle

import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.MapView

interface GameCycleLogic {
    //TODO: fix doc
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
    fun makeMove(heroId: Int, move: Move): GameStatus

    //TODO: doc
    fun getCurrentMap(heroId: Int): MapView

    //TODO: doc
    fun putOnItem(heroId: Int, index: Int)

    //TODO: doc
    fun putOffItem(heroId: Int, type: ItemType)
}