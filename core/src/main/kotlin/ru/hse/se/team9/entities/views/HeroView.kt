package ru.hse.se.team9.entities.views

import ru.hse.se.team9.positions.Position

/** A part of hero which is allowed to be shown by View. */
interface HeroView {
    /** Position of hero on the map */
    val position: Position
}