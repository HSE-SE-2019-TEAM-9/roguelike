package ru.hse.se.team9.model.logic.menu

/**
 * Corresponds to all player action in menu.
 */
sealed class MenuAction
sealed class StartLocalGame : MenuAction()
object NewLocalGame : StartLocalGame()
object LoadSavedGame : StartLocalGame()
object OpenGameFromFile : StartLocalGame()

sealed class StartOnlineGame: MenuAction()
object JoinExistingSession: StartOnlineGame()
object CreateSession: StartOnlineGame()

object Continue : MenuAction()
object Save : MenuAction()
object Exit : MenuAction()