package ru.hse.se.team9.model.logic.gamecycle

data class Tick(val value: Int = 0) {
    operator fun inc(): Tick {
        return Tick(value + 1)
    }
}
