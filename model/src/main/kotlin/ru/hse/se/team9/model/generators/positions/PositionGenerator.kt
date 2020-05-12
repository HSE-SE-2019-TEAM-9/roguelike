package ru.hse.se.team9.model.generators.positions

import ru.hse.se.team9.positions.Position

/** A common interface for all generators of positions. Generators can be either random or deterministic. */
interface PositionGenerator {
    /** Generates one position in the given box */
    fun createPosition(width: Int, height: Int): Position
}