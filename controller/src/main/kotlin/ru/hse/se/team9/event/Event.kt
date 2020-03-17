package ru.hse.se.team9.event

import ru.hse.se.team9.model.logic.gamecycle.PlayerAction

sealed class Event

data class KeyPressed(val action: PlayerAction): Event()