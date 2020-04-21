package ru.hse.se.team9.model.random.directions

import ru.hse.se.team9.game.entities.map.Direction
import kotlin.random.Random

/** An implementation of DirectionGenerator which uses kotlin.random.Random for choosing direction */
object RandomDirection: DirectionGenerator {
    /** Chooses random direction from the allowedDirections list */
    override fun createDirection(allowedDirections: List<Direction>): Direction {
        require(allowedDirections.isNotEmpty())
        return allowedDirections[Random.nextInt(allowedDirections.size)]
    }

    override fun createDirection(): Direction {
        return createDirection(
            listOf(
                Direction.UP,
                Direction.DOWN,
                Direction.RIGHT,
                Direction.LEFT
            )
        )
    }
}