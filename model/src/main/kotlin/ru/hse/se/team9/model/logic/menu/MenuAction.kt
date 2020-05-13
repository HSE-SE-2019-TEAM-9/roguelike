package ru.hse.se.team9.model.logic.menu

/**
 * Corresponds to all player action in menu.
 */
sealed class MenuAction
sealed class StartLocalGame : MenuAction()
object NewLocalGame : StartLocalGame()
object LoadSavedGame : StartLocalGame()
object OpenGameFromFile : StartLocalGame()
object StartOnlineGame: MenuAction()

sealed class OnlineMenuAction : MenuAction()
object BackToLocalMenu: OnlineMenuAction()
object JoinExistingSession: OnlineMenuAction()
object CreateSession: OnlineMenuAction()

object Continue : MenuAction()
object Save : MenuAction()
object Exit : MenuAction()