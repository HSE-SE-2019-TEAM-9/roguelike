package ru.hse.se.team9.positions

/** Represents view on hero for user -- any property of hero, not needed for rendering, cannot be accessed */
interface HeroPosition {
    /** Hero position on map */
    val position: Position
}