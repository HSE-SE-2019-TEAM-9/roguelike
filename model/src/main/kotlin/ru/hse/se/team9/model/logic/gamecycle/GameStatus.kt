package ru.hse.se.team9.model.logic.gamecycle

/** Represents status of the current game (not app) */
sealed class GameStatus

sealed class Finished : GameStatus()
object Win : Finished()
object Loss : Finished()

object InProgress : GameStatus()
