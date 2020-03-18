package ru.hse.se.team9.model.logic.general

import arrow.core.Either
import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.model.logic.gamecycle.*
import ru.hse.se.team9.model.logic.menu.MenuAction
import ru.hse.se.team9.model.mapgeneration.*
import ru.hse.se.team9.model.random.DirectionGenerator
import ru.hse.se.team9.model.random.PositionGenerator
import ru.hse.se.team9.view.KeyPressedType
import ru.hse.se.team9.view.MenuOption
import ru.hse.se.team9.view.ViewController

class AppLogic(
    private val viewController: ViewController,
    private val directionGenerator: DirectionGenerator,
    private val positionGenerator: PositionGenerator,
    private val fileChooser: FileChooser
) {
    private lateinit var gameCycleLogic: GameCycleLogic
    private var appStatus: AppStatus = AppStatus.IN_MENU
    private val menuOptions: List<MenuOption>

    init {
        viewController.setKeyPressedHandler {
            val action = when (it) {
                KeyPressedType.UP -> Up
                KeyPressedType.DOWN -> Down
                KeyPressedType.LEFT -> Left
                KeyPressedType.RIGHT -> Right
                KeyPressedType.ESCAPE -> OpenMenu
            }
            when (action) {
                is Move -> movePlayer(action)
                is OpenMenu -> {
                    appStatus = AppStatus.IN_MENU
                    openMenu()
                }
            }
        }
        menuOptions = listOf(
            MenuOption(NEW_GAME_OPTION) { applyMenuAction(MenuAction.NEW_GAME) },
            MenuOption(CONTINUE_OPTION, false) { applyMenuAction(MenuAction.CONTINUE)},
            MenuOption(LOAD_GAME_OPTION) { applyMenuAction(MenuAction.LOAD_GAME) },
            MenuOption(EXIT_OPTION) { applyMenuAction(MenuAction.EXIT) }
        )
    }

    fun openMenu() {
        drawMenu()
    }

    private fun applyMenuAction(action: MenuAction): AppStatus {
        require(appStatus == AppStatus.IN_MENU)
        when (action) {
            MenuAction.NEW_GAME ->  {
                gameCycleLogic = startRandomGame()
                appStatus = AppStatus.IN_GAME
                makeContinueOptionVisible()
                drawMap()
            }
            MenuAction.LOAD_GAME -> {
                when (val result = startLoadGame()) {
                    is Either.Left -> {
                        appStatus = AppStatus.IN_MENU
                        when (result.a) {
                            is FileNotChosen -> drawError("File not chosen.")
                            is ParseError -> drawError("Cannot parse map from a file")
                        }
                    }
                    is Either.Right -> {
                        gameCycleLogic = result.b
                        appStatus = AppStatus.IN_GAME
                        drawMap()
                    }
                }
            }
            MenuAction.CONTINUE -> {
                appStatus = AppStatus.IN_GAME
                drawMap()
            }
            MenuAction.EXIT -> exit()
        }
        return appStatus
    }

    private fun makeContinueOptionVisible() {
        for (option in menuOptions) {
            if (option.optionName == CONTINUE_OPTION) {
                option.visible = true
            }
        }
    }

    private fun movePlayer(move: Move): AppStatus {
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
        viewController.drawMenu(MAIN_MENU_TITLE, menuOptions)
    }

    private fun drawError(error: String) {
        require(appStatus == AppStatus.IN_MENU)
        viewController.drawError(error) { drawMenu() }
    }

    private fun drawMap() {
        require(appStatus == AppStatus.IN_GAME)
        val gameMap = gameCycleLogic.map
        viewController.drawMap(gameMap.map, gameMap.width, gameMap.height, gameMap.hero.position)
    }

    private fun exit() {
        viewController.stop()
    }

    companion object {
        private const val MAP_WIDTH = 516
        private const val MAP_HEIGHT = 516

        private const val MAIN_MENU_TITLE = "Main menu"
        private const val NEW_GAME_OPTION = "New game"
        private const val LOAD_GAME_OPTION = "Load game"
        private const val EXIT_OPTION = "Exit"
        private const val CONTINUE_OPTION = "Continue"
    }
}