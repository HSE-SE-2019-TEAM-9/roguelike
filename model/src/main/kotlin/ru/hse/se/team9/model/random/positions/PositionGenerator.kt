package ru.hse.se.team9.model.random.positions

import ru.hse.se.team9.positions.Position

interface PositionGenerator {
    fun createPosition(width: Int, height: Int): Position
}