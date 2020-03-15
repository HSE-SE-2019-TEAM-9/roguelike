package ru.hse.se.team9.model.logic.gamecycle

sealed class PlayerAction

object OpenMenu: PlayerAction()

sealed class Move: PlayerAction()

object Left: Move()
object Up: Move()
object Right: Move()
object Down: Move()
