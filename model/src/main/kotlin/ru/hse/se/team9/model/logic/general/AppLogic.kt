package ru.hse.se.team9.model.logic.general

import arrow.core.Either
import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.model.logic.gamecycle.GameCycleLogic
import ru.hse.se.team9.model.logic.gamecycle.GameStatus
import ru.hse.se.team9.model.logic.gamecycle.Move
import ru.hse.se.team9.model.logic.menu.MenuAction
import ru.hse.se.team9.model.logic.menu.MenuLogic
import ru.hse.se.team9.model.logic.menu.MenuStatus
import ru.hse.se.team9.model.mapgeneration.FromFileMapCreator
import ru.hse.se.team9.model.mapgeneration.MapCreationError
import ru.hse.se.team9.model.mapgeneration.RandomMapCreator
import ru.hse.se.team9.model.mapgeneration.ViewFileChooser
import ru.hse.se.team9.model.random.DirectionGenerator
import ru.hse.se.team9.model.random.PositionGenerator
import ru.hse.se.team9.view.View

class AppLogic(
    private val view: View,
    private val directionGenerator: DirectionGenerator,
    private val positionGenerator: PositionGenerator,
    private val fileChooser: FileChooser
) {
    private val menuLogic: MenuLogic = MenuLogic()
    private lateinit var gameCycleLogic: GameCycleLogic
    private var appStatus: AppStatus = AppStatus.IN_MENU

    fun applyMenuAction(action: MenuAction): AppStatus {
        require(appStatus == AppStatus.IN_MENU)
        when (menuLogic.applyMenuAction(action)) {
            MenuStatus.NEW_GAME ->  {
                gameCycleLogic = startRandomGame()
                appStatus = AppStatus.IN_GAME
                drawMap()
            }
            MenuStatus.LOAD_GAME -> {
                when (val result = startLoadGame()) {
                    is Either.Left -> {
                        appStatus = AppStatus.IN_MENU
                        drawMenu()
                        TODO() // need smth like drawError here
                    }
                    is Either.Right -> {
                        gameCycleLogic = result.b
                        appStatus = AppStatus.IN_GAME
                        drawMap()
                    }
                }
            }
            MenuStatus.EXIT -> TODO("not implemented")
            MenuStatus.STAY_IN_MENU -> drawMenu()
        }
        return appStatus
    }

    fun movePlayer(move: Move): AppStatus {
        require(appStatus == AppStatus.IN_GAME)
        val status = gameCycleLogic.movePlayer(move)
        if (status == GameStatus.FINISHED) {
            appStatus = AppStatus.IN_MENU
            drawMenu()
        } else {
            drawMap()
        }
        return appStatus
    }

    private fun startRandomGame(): GameCycleLogic {
        val map = RandomMapCreator(directionGenerator, positionGenerator, MAP_WIDTH, MAP_HEIGHT).createMap()
        return GameCycleLogic(map)
    }

    private fun startLoadGame(): Either<MapCreationError, GameCycleLogic> {
        val result = FromFileMapCreator(positionGenerator, fileChooser).createMap()
        return result.map { GameCycleLogic(it) }
    }

    private fun drawMenu() {
        require(appStatus == AppStatus.IN_MENU)
        TODO("not implemented") // need menu here
    }

    private fun drawMap() {
        require(appStatus == AppStatus.IN_GAME)
        val gameMap = gameCycleLogic.map
        view.drawMap(gameMap.map, gameMap.width, gameMap.height, gameMap.hero.position)
    }

    companion object {
        private const val MAP_WIDTH = 516
        private const val MAP_HEIGHT = 516
    }
}