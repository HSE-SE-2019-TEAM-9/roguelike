package ru.hse.se.team9.model.random.directions

import ru.hse.se.team9.game.entities.map.Direction

/** A common interface for all generators of directions. Generators can be either random or deterministic. */
interface DirectionGenerator {
    /** Chooses one direction from the allowdDirections list. */
    fun createDirection(allowedDirections: List<Direction>): Direction

    fun createDirection(): Direction
}