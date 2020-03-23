package ru.hse.se.team9.model.logic.gamecycle

sealed class GameStatus

sealed class Finished: GameStatus()
object Win: Finished()
object Loss: Finished()

object InProgress: GameStatus()
