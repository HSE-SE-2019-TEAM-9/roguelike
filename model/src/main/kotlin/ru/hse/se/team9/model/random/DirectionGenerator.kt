package ru.hse.se.team9.model.random

interface DirectionGenerator {
    companion object {
        enum class Direction { // TODO: move somewhere else
            UP,
            DOWN,
            LEFT,
            RIGHT
        }
    }

    fun createDirection(allowedDirections: List<Direction>): Direction
}