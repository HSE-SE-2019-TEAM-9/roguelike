package ru.hse.se.team9.model.random.positions

import ru.hse.se.team9.model.random.positions.PositionGenerator
import ru.hse.se.team9.positions.Position
import kotlin.random.Random

object RandomPosition: PositionGenerator {
    override fun createPosition(width: Int, height: Int): Position {
        val x: Int = Random.nextInt(width)
        val y: Int = Random.nextInt(height)
        return Position(x, y)
    }
}