package ru.hse.se.team9.model.logic.gamecycle

/** Game tick (not used in current version) */
data class Tick(val value: Int = 0) {
    /** Plus one*/
    operator fun inc(): Tick {
        return Tick(value + 1)
    }
}
