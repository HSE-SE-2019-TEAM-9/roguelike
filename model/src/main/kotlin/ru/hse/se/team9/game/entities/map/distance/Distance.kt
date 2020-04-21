package ru.hse.se.team9.game.entities.map.distance

import ru.hse.se.team9.positions.Position

interface Distance {
    operator fun invoke(from: Position, to: Position): Int
}