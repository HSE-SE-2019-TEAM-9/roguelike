package ru.hse.se.team9.model.logic.gamecycle

/** Defines all action player can make in the app */
sealed class PlayerAction

object OpenMenu : PlayerAction()

sealed class Move : PlayerAction()

object Left : Move()
object Up : Move()
object Right : Move()
object Down : Move()
