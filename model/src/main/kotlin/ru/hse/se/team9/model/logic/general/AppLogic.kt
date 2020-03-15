package ru.hse.se.team9.model.logic.general

import arrow.core.Either
import ru.hse.se.team9.model.logic.gamecycle.*
import ru.hse.se.team9.model.logic.menu.MenuAction
import ru.hse.se.team9.model.logic.menu.MenuLogic
import ru.hse.se.team9.model.logic.menu.MenuStatus
import ru.hse.se.team9.model.mapgeneration.FromFileMapCreator
import ru.hse.se.team9.model.mapgeneration.MapCreationError
import ru.hse.se.team9.model.mapgeneration.RandomMapCreator
import ru.hse.se.team9.model.random.RandomDirection
import ru.hse.se.team9.model.random.RandomPosition
import ru.hse.se.team9.view.View

class AppLogic(private val view: View) {
    private val menuLogic: MenuLogic = MenuLogic()
    private lateinit var gameCycleLogic: GameCycleLogic
    private var appStatus: AppStatus = AppStatus.IN_MENU

    fun applyMenuAction(action: MenuAction): AppStatus {
        when (menuLogic.applyMenuAction(action)) {
            MenuStatus.NEW_GAME ->  {
                gameCycleLogic = startRandomGame()
                drawMap()
                appStatus = AppStatus.IN_GAME
            }
            MenuStatus.LOAD_GAME -> {
                when (val result = startLoadGame()) {
                    is Either.Left -> {
                        appStatus = AppStatus.IN_MENU
                        drawMenu()
                    }
                    is Either.Right -> {
                        gameCycleLogic = result.b
                        drawMap()
                        appStatus = AppStatus.IN_GAME
                    }
                }
            }
            MenuStatus.EXIT -> TODO()
            MenuStatus.STAY_IN_MENU -> drawMenu()
        }
        return appStatus
    }

    private fun drawMenu() {
        TODO("not implemented") // need menu here
    }

    private fun drawMap() {
        val gameMap = gameCycleLogic.map
        view.drawMap(gameMap.map, gameMap.width, gameMap.height, gameMap.hero.position)
    }

    fun movePlayer(move: Move): AppStatus {
        val status = gameCycleLogic.movePlayer(move)
        if (status == GameStatus.FINISHED) {
            appStatus = AppStatus.IN_MENU
        }
        return appStatus
    }

    private fun startRandomGame(): GameCycleLogic {
        val map = RandomMapCreator(RandomDirection, RandomPosition, Companion.MAP_WIDTH, Companion.MAP_HEIGHT).createMap()
        return GameCycleLogic(map)
    }

    private fun startLoadGame(): Either<MapCreationError, GameCycleLogic> {
        val result = FromFileMapCreator(RandomPosition).createMap()
        return result.map { GameCycleLogic(it) }
    }

    companion object {
        private const val MAP_WIDTH = 512
        private const val MAP_HEIGHT = 512
    }
}