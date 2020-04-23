package ru.hse.se.team9.entities

/**
 * Represents map landscape.
 * Used for map representation in view.
 */
enum class MapObject {
    /** Entity, through which neither player nor anybody else can walk */
    WALL,
    /** Can be stepped over by anyone */
    EMPTY_SPACE
}