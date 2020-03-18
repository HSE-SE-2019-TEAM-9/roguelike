package ru.hse.se.team9.model.logic.menu

sealed class MenuAction
sealed class StartGame: MenuAction()
object NewGame: StartGame()
object LoadGame: StartGame()

object Continue: MenuAction()
object Exit: MenuAction()