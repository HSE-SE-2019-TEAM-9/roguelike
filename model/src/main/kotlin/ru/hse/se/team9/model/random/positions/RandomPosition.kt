package ru.hse.se.team9.model.random.positions

import ru.hse.se.team9.positions.Position
import kotlin.random.Random

/** An implementation of PositionGenerator which uses kotlin.random.Random for generating position. */
object RandomPosition: PositionGenerator {
    /** Creates random position in the given box*/
    override fun createPosition(width: Int, height: Int): Position {
        val x: Int = Random.nextInt(width)
        val y: Int = Random.nextInt(height)
        return Position(x, y)
    }
}