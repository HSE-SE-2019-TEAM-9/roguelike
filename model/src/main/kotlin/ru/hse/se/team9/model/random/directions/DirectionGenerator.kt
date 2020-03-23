package ru.hse.se.team9.model.random.directions

import ru.hse.se.team9.game.entities.map.Direction

interface DirectionGenerator {
    fun createDirection(allowedDirections: List<Direction>): Direction
    fun createDirection(): Direction
}