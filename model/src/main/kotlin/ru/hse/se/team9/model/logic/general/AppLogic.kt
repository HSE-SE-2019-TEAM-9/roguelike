package ru.hse.se.team9.model.logic.general

import ru.hse.se.team9.model.logic.gamecycle.*
import ru.hse.se.team9.model.logic.menu.MenuAction
import ru.hse.se.team9.model.logic.menu.MenuLogic
import ru.hse.se.team9.model.logic.menu.MenuStatus

class AppLogic {
    private val menuLogic: MenuLogic = MenuLogic()
    private lateinit var gameCycleLogic: GameCycleLogic
    private var appStatus: AppStatus = AppStatus.IN_MENU

    fun applyMenuAction(action: MenuAction): AppStatus {
        when (menuLogic.applyMenuAction(action)) {
            MenuStatus.NEW_GAME ->  {
                startGame()
                appStatus = AppStatus.IN_GAME
            }
        }
        return appStatus
    }

    fun movePlayer(move: Move): AppStatus {
        val status = gameCycleLogic.movePlayer(move)
        if (status == GameStatus.FINISHED) {
            appStatus = AppStatus.IN_MENU
        }
        return appStatus
    }

    private fun startGame() {
        TODO("not implemented") // Map creator needed
    }


}