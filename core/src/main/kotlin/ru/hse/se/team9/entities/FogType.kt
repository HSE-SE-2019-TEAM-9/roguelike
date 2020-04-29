package ru.hse.se.team9.entities

/** Determines visibility of map cells */
enum class FogType {
    /** Fully visible to hero */
    VISIBLE,
    /** Hero knows the landscape but does not see any objects or mobs */
    SHADOWED,
    /** Hero knows nothing about that map cell */
    INVISIBLE
}