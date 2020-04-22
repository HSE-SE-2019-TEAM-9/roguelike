package ru.hse.se.team9.model.logic.general

import arrow.core.Either
import arrow.core.flatMap
import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.game.entities.map.MapViewImpl
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.model.logic.gamecycle.*
import ru.hse.se.team9.model.logic.menu.*
import ru.hse.se.team9.model.mapgeneration.*
import ru.hse.se.team9.model.mapgeneration.creators.FromFileMapCreator
import ru.hse.se.team9.model.mapgeneration.creators.RandomMapCreator
import ru.hse.se.team9.model.random.GameGeneratorMediator
import ru.hse.se.team9.view.KeyPressedType
import ru.hse.se.team9.view.MenuOption
import ru.hse.se.team9.view.ViewController

/**
 * Represents all logic of the application.
 * Creates GameCycleLogic for the logic of a single game.
 *
 * @param viewController responsible for ui and keyboard actions
 * @param generator generates random or predetermined game entities or concepts
 * @param fileChooser chooses file from filesystem. Needed for FromFileMapCreator
 * @param distance a distance metric which is used in fog computations and in mob strategies
 */
class AppLogic(
    private val viewController: ViewController,
    private val generator: GameGeneratorMediator,
    private val fileChooser: FileChooser,
    private val distance: Distance
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
                    makeMove(action)
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

    /** starts application and opens main menu */
    fun openMenu() {
        drawMenu()
    }

    private fun applyMenuAction(action: MenuAction): AppStatus {
        require(appStatus == AppStatus.IN_MENU)
        when (action) {
            NewGame -> mapCreator =
                RandomMapCreator.build(generator, MAP_WIDTH, MAP_HEIGHT, fogRadius = FOG_RADIUS, distance = distance)
            LoadGame -> mapCreator =
                FromFileMapCreator.build(generator, fileChooser)
            Continue -> {
                appStatus = AppStatus.IN_GAME
                makeContinueOptionVisible()
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
        val result = mapCreator
            .flatMap { it.createMap() }
            .map { GameCycleLogic(it, generator) }

        when (result) {
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

    private fun makeContinueOptionInvisible() {
        for (option in menuOptions) {
            if (option.optionName == CONTINUE_OPTION) {
                option.visible = false
            }
        }
    }

    private fun makeMove(move: Move) {
        require(appStatus == AppStatus.IN_GAME)
        if (gameCycleLogic.makeMove(move) is Finished) {
            drawMap()
            appStatus = AppStatus.IN_MENU
            makeContinueOptionInvisible()
            drawMenu(true)
        } else {
            drawMap()
        }
    }

    private fun drawMenu(wasGameOver: Boolean = false) {
        require(appStatus == AppStatus.IN_MENU)
        val title = if (wasGameOver) GAME_OVER_TITLE else MAIN_MENU_TITLE
        viewController.drawMenu(title, menuOptions)
    }

    private fun drawError(error: String) {
        require(appStatus == AppStatus.IN_MENU)
        viewController.drawError(error) { drawMenu() }
    }

    private fun drawMap() {
        require(appStatus == AppStatus.IN_GAME)
        val gameMap = gameCycleLogic.map
        viewController.drawMap(MapViewImpl(gameMap))
    }

    private fun exit() {
        viewController.stop()
    }

    companion object {
        private const val MAP_WIDTH = 100
        private const val MAP_HEIGHT = 100
        private const val FOG_RADIUS = 10

        private const val GAME_OVER_TITLE = "Game Over"
        private const val MAIN_MENU_TITLE = "Main menu"
        private const val NEW_GAME_OPTION = "New game"
        private const val LOAD_GAME_OPTION = "Load game"
        private const val EXIT_OPTION = "Exit"
        private const val CONTINUE_OPTION = "Continue"
    }
}