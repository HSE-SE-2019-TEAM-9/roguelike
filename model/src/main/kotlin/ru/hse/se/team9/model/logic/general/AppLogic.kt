package ru.hse.se.team9.model.logic.general

import arrow.core.Either
import arrow.core.flatMap
import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.game.entities.map.MapViewImpl
import ru.hse.se.team9.model.logic.gamecycle.*
import ru.hse.se.team9.model.logic.menu.*
import ru.hse.se.team9.model.mapgeneration.*
import ru.hse.se.team9.model.mapgeneration.creators.FromFileMapCreator
import ru.hse.se.team9.model.mapgeneration.creators.RandomMapCreator
import ru.hse.se.team9.model.random.DirectionGenerator
import ru.hse.se.team9.model.random.MobGenerator
import ru.hse.se.team9.model.random.PositionGenerator
import ru.hse.se.team9.view.KeyPressedType
import ru.hse.se.team9.view.MenuOption
import ru.hse.se.team9.view.ViewController

class AppLogic(
    private val viewController: ViewController,
    private val directionGenerator: DirectionGenerator,
    private val positionGenerator: PositionGenerator,
    private val mobGenerator: MobGenerator,
    private val fileChooser: FileChooser
) {
    private lateinit var gameCycleLogic: GameCycleLogic
    private lateinit var mapCreator: Either<MapCreationError, MapCreator>
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
                is Move -> {
                    doMove(action)
                }
                is OpenMenu -> {
                    appStatus = AppStatus.IN_MENU
                    openMenu()
                }
            }
        }
        menuOptions = listOf(
            MenuOption(NEW_GAME_OPTION) { applyMenuAction(NewGame) },
            MenuOption(CONTINUE_OPTION, false) { applyMenuAction(Continue) },
            MenuOption(LOAD_GAME_OPTION) { applyMenuAction(LoadGame) },
            MenuOption(EXIT_OPTION) { applyMenuAction(Exit) }
        )
    }

    fun openMenu() {
        drawMenu()
    }

    private fun doMove(action: Move) {
        movePlayer(action)
        moveMobs()
    }

    private fun applyMenuAction(action: MenuAction): AppStatus {
        require(appStatus == AppStatus.IN_MENU)
        when (action) {
            NewGame -> mapCreator =
                RandomMapCreator.build(directionGenerator, positionGenerator, mobGenerator, MAP_WIDTH, MAP_HEIGHT)
            LoadGame -> mapCreator =
                FromFileMapCreator.build(positionGenerator, fileChooser)
            Continue -> {
                appStatus = AppStatus.IN_GAME
                drawMap()
            }
            Exit -> exit()
        }
        if (action is StartGame) {
            startGame()
        }
        return appStatus
    }

    private fun startGame() {
        when (val result = mapCreator.flatMap { it.createMap() }.map { GameCycleLogic(it) }) {
            is Either.Left -> {
                appStatus = AppStatus.IN_MENU
                printError(result.a)
            }
            is Either.Right -> {
                gameCycleLogic = result.b
                appStatus = AppStatus.IN_GAME
                makeContinueOptionVisible()
                drawMap()
            }
        }
    }

    private fun printError(error: MapCreationError) {
        when (error) {
            FileNotChosen -> drawError("File not chosen.")
            is ParseError -> drawError("Cannot parse map from a file")
            MapTooBig -> drawError("Map is too big")
            ChunkTooBig -> drawError("Chunk size must be smaller then map sizes")
            NegativeSize -> drawError("Map sizes must be positive")
        }
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
        updateAppStatus(gameCycleLogic.movePlayer(move))
        return appStatus
    }

    private fun moveMobs(): AppStatus {
        require(appStatus == AppStatus.IN_GAME)
        updateAppStatus(gameCycleLogic.moveMobs())
        return appStatus
    }

    private fun updateAppStatus(status: GameStatus) {
        if (status == GameStatus.FINISHED) {
            appStatus = AppStatus.IN_MENU
            drawMenu()
        } else {
            drawMap()
        }
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
        viewController.drawMap(MapViewImpl(gameMap), gameMap.width, gameMap.height)
    }

    private fun exit() {
        viewController.stop()
    }

    companion object {
        private const val MAP_WIDTH = 40
        private const val MAP_HEIGHT = 40

        private const val MAIN_MENU_TITLE = "Main menu"
        private const val NEW_GAME_OPTION = "New game"
        private const val LOAD_GAME_OPTION = "Load game"
        private const val EXIT_OPTION = "Exit"
        private const val CONTINUE_OPTION = "Continue"
    }
}