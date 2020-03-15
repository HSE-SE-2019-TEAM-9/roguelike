package ru.hse.se.team9.model.random

import ru.hse.se.team9.model.random.DirectionGenerator.Companion.Direction
import kotlin.random.Random

object RandomDirection: DirectionGenerator {
    override fun createDirection(allowedDirections: List<Direction>): Direction {
        require(allowedDirections.isNotEmpty())
        return allowedDirections[Random.nextInt(allowedDirections.size)]
    }
}