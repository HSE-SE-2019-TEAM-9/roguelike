package ru.hse.se.team9.game.entities.map.distance

import ru.hse.se.team9.positions.Position
import kotlin.math.abs

/** The distance between two points measured along axes at right angles. */
object Manhattan : Distance {
    override fun invoke(from: Position, to: Position): Int {
        return abs(from.x - to.x) + abs(from.y - to.y)
    }
}