package ru.hse.se.team9.model.logic.general

import arrow.core.Either
import arrow.core.flatMap
import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.model.logic.gamecycle.*
import ru.hse.se.team9.model.logic.menu.*
import ru.hse.se.team9.model.mapgeneration.*
import ru.hse.se.team9.model.mapgeneration.creators.FromFileMapCreator
import ru.hse.se.team9.model.mapgeneration.creators.RandomMapCreator
import ru.hse.se.team9.model.mapgeneration.creators.RestoreSavedMapCreator
import ru.hse.se.team9.model.generators.GameGenerator
import ru.hse.se.team9.utils.GameMapSaver
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
    private val generator: GameGenerator,
    private val saver: GameMapSaver,
    private val fileChooser: FileChooser,
    private val distance: Distance
) {
    private lateinit var gameCycleLogic: GameCycleLogic
    private lateinit var mapCreator: Either<MapCreationError, MapCreator>
    @Volatile private var appStatus: AppStatus = AppStatus.IN_MENU
    private val menuOptions: List<MenuOption>

    init {
        viewController.setKeyPressedHandler {
            val action = when (it) {
                KeyPressedType.UP -> Up
                KeyPressedType.DOWN -> Down
                KeyPressedType.LEFT -> Left
                KeyPressedType.RIGHT -> Right
                KeyPressedType.ESCAPE -> OpenMenu
                KeyPressedType.TAB -> OpenInventory
            }
            when (action) {
                is Move -> {
                    makeMove(action)
                }
                is OpenMenu -> {
                    appStatus = AppStatus.IN_MENU
                    openMenu()
                }
                is OpenInventory -> {
                    appStatus = AppStatus.IN_INVENTORY
                    drawMap()
                }
            }
        }
        menuOptions = listOf(
            MenuOption(CONTINUE_OPTION, false) { applyMenuAction(Continue) },
            MenuOption(NEW_GAME_OPTION) { applyMenuAction(NewGame) },
            MenuOption(LOAD_SAVED_GAME_OPTION, saver.isSaved()) { applyMenuAction(LoadSavedGame) },
            MenuOption(NEW_GAME_FROM_FILE_OPTION) { applyMenuAction(OpenGameFromFile) },
            MenuOption(EXIT_OPTION) { applyMenuAction(Exit) },
            MenuOption(SAVE_OPTION, false) { applyMenuAction(Save) }
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
                RandomMapCreator.build(generator, MAP_WIDTH, MAP_HEIGHT, fogRadius = FOG_RADIUS, distance = distance).map {
                    object : MapCreator {
                        override fun createMap(): Either<MapCreationError, GameMap> = it.createMap().map { map ->
                            map.addHeroToRandomPosition(0, generator.createHero())
                            map
                        }
                    }
                }

            LoadSavedGame -> mapCreator =
                RestoreSavedMapCreator.build(saver)
            OpenGameFromFile -> mapCreator =
                FromFileMapCreator.build(generator, fileChooser)
            Continue -> {
                appStatus = AppStatus.IN_GAME
                makeInGameOptionsVisible()
                drawMap()
            }
            Exit -> exit(false)
            Save -> exit(true)
        }
        if (action is StartGame) {
            startGame()
        }
        return appStatus
    }

    private val putOffItem = { type: ItemType ->
        gameCycleLogic.putOffItem(type)
    }

    private val putOnItem = { index: Int ->
        gameCycleLogic.putOnItem(index)
    }

    private val closeInventory = {
        appStatus = AppStatus.IN_GAME
        drawMap()
    }

    private fun startGame() {
        val result = mapCreator
            .flatMap { it.createMap() }
            .map { map -> LocalGameCycleLogic(map, generator) { drawMap() } }

        when (result) {
            is Either.Left -> {
                appStatus = AppStatus.IN_MENU
                printError(result.a)
            }
            is Either.Right -> {
                gameCycleLogic = result.b
                appStatus = AppStatus.IN_GAME
                saver.delete()
                makeInGameOptionsVisible()
                makeSavedGameOptionInvisible()
                drawMap()
            }
        }
    }

    private fun printError(error: MapCreationError) {
        when (error) {
            FileNotChosen -> drawError("File not chosen.")
            is ParseError -> drawError("Cannot parse map from a file")
            is RestoreError -> drawError("Failed to restore game")
            MapTooBig -> drawError("Map is too big")
            ChunkTooBig -> drawError("Chunk size must be smaller then map sizes")
            NegativeSize -> drawError("Map sizes must be positive")
        }
    }

    private fun makeInGameOptionsVisible() {
        for (option in menuOptions) {
            if (option.optionName == CONTINUE_OPTION || option.optionName == SAVE_OPTION) {
                option.visible = true
            }
        }
    }

    private fun makeInGameOptionsInvisible() {
        for (option in menuOptions) {
            if (option.optionName == CONTINUE_OPTION || option.optionName == SAVE_OPTION) {
                option.visible = false
            }
        }
    }

    private fun makeSavedGameOptionInvisible() {
        for (option in menuOptions) {
            if (option.optionName == LOAD_SAVED_GAME_OPTION) {
                option.visible = saver.isSaved()
            }
        }
    }

    private fun makeMove(move: Move) {
        require(appStatus == AppStatus.IN_GAME)
        if (gameCycleLogic.makeMove(move) is Finished) {
            appStatus = AppStatus.IN_MENU
            makeInGameOptionsInvisible()
            drawMenu(true)
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

    @Synchronized
    private fun drawMap() {
        if (appStatus == AppStatus.IN_GAME) {
            viewController.drawMap(gameCycleLogic.getCurrentMap())
        } else if (appStatus == AppStatus.IN_INVENTORY) {
            viewController.drawInventory(gameCycleLogic.getCurrentMap(), putOffItem, putOnItem, closeInventory)
        }
    }

    private fun exit(saveGame: Boolean) {
        if (saveGame) {
            saver.save((gameCycleLogic as LocalGameCycleLogic).map)
        }
        viewController.stop()
    }

    companion object {
        private const val MAP_WIDTH = 100
        private const val MAP_HEIGHT = 100
        private const val FOG_RADIUS = 10

        private const val GAME_OVER_TITLE = "Game Over"
        private const val MAIN_MENU_TITLE = "Main menu"
        private const val NEW_GAME_OPTION = "New game"
        private const val LOAD_SAVED_GAME_OPTION = "Load last game"
        private const val NEW_GAME_FROM_FILE_OPTION = "New game from file"
        private const val EXIT_OPTION = "Exit"
        private const val SAVE_OPTION = "Save and exit"
        private const val CONTINUE_OPTION = "Continue"
    }
}