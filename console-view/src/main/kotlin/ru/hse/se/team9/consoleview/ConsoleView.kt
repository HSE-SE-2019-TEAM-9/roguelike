package ru.hse.se.team9.consoleview

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.BasicWindow
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.Window
import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder
import com.googlecode.lanterna.gui2.dialogs.FileDialog
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.view.KeyPressedType
import ru.hse.se.team9.view.MenuOption
import ru.hse.se.team9.view.View
import java.io.EOFException
import java.io.File
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class ConsoleView(private val width: Int = 150, private val height: Int = 50): View {
    private val gui: WindowBasedTextGUI
    private val mapWindow: BasicWindow
    private var mapView: MapComponent? = null
    private var keyPressedHandler: (KeyPressedType) -> Unit = {}

    init {
        val terminal = SwingTerminalFrame(
            APP_TITLE,
            TerminalSize(width, height),
            null,
            null,
            null,
            TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode
        )
        terminal.isVisible = true
        val screen = TerminalScreen(terminal)
        screen.startScreen()
        screen.cursorPosition = null

        mapWindow = BasicWindow()
        mapWindow.setHints(listOf(Window.Hint.NO_DECORATIONS, Window.Hint.FULL_SCREEN))

        gui = MultiWindowTextGUI(screen, TextColor.ANSI.BLACK)
        gui.addWindow(mapWindow)
    }

    override fun start() {
        thread(start = true, isDaemon = true) {
            while (true) {
                sleep(1)
                try {
                    gui.updateScreen()
                    gui.processInput()
                } catch (e: EOFException) {
                    return@thread
                }
            }
        }
    }

    override fun drawMainMenu(options: List<MenuOption>) {
        val builder = ActionListDialogBuilder()
        builder.title = MAIN_MENU_TITLE
        builder.isCanCancel = false

        for (option in options) {
            builder.addAction(option.optionName, option.action)
        }
        val dialog = builder.build()
        dialog.setHints(listOf(Window.Hint.CENTERED))
        dialog.showDialog(gui)
    }

    override fun drawMap(map: List<List<MapObject>>, width: Int, height: Int, heroPosition: Position) {
        mapView = MapComponent(map, heroPosition, gui.screen, keyPressedHandler)
        mapWindow.component = mapView
    }

    override fun drawError(error: String) {
        val builder = MessageDialogBuilder()
        builder.setTitle(ERROR_TITLE)
        builder.setText(error)
        builder.addButton(MessageDialogButton.OK)
        val dialog = builder.build()
        dialog.setHints(listOf(Window.Hint.CENTERED))
        dialog.showDialog(gui)
    }

    override fun drawFileDialog(selectedObject: File): File? {
        return FileDialog(
            CHOOSE_FILE_TITLE,
            null,
            "OK",
            TerminalSize(width / 2, height / 2),
            false,
            selectedObject
        ).showDialog(gui)
    }

    override fun setKeyPressedHandler(keyPressedHandler: (KeyPressedType) -> Unit) {
        this.keyPressedHandler = keyPressedHandler
        mapView?.keyPressedHandler = keyPressedHandler
    }

    companion object {
        private const val APP_TITLE = "Roguelike-1"
        private const val MAIN_MENU_TITLE = "Main menu"
        private const val CHOOSE_FILE_TITLE = "Choose file"
        private const val ERROR_TITLE = "Error"
    }
}

