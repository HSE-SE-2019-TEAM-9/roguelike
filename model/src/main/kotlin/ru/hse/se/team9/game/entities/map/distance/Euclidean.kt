package ru.hse.se.team9.game.entities.map.distance

import ru.hse.se.team9.positions.Position

/** Euclidean distance SQUARED */
object Euclidean : Distance {
    override fun invoke(from: Position, to: Position): Int {
        val x = from.x - to.x
        val y = from.y - to.y
        return x * x + y * y
    }
}