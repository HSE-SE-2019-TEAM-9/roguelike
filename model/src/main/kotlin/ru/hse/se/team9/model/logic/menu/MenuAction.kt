package ru.hse.se.team9.model.logic.menu

/**
 * Corresponds to all player action in menu.
 */
sealed class MenuAction
sealed class StartGame : MenuAction()
object NewGame : StartGame()
object LoadSavedGame : StartGame()
object OpenGameFromFile : StartGame()

object Continue : MenuAction()
object Save : MenuAction()
object Exit : MenuAction()