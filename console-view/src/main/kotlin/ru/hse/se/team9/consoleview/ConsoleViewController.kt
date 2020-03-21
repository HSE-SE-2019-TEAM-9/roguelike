package ru.hse.se.team9.consoleview

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder
import com.googlecode.lanterna.gui2.dialogs.FileDialog
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.view.KeyPressedType
import ru.hse.se.team9.view.MenuOption
import ru.hse.se.team9.view.ViewController
import java.io.EOFException
import java.io.File
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

class ConsoleViewController(private val width: Int = 150, private val height: Int = 50): ViewController {
    private val gui: WindowBasedTextGUI
    private val mapWindow: BasicWindow
    private var mapView: MapComponent? = null
    private var keyPressedHandler: (KeyPressedType) -> Unit = {}
    private val actionQueue: Queue<() -> Unit> = ConcurrentLinkedQueue()

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
        mapWindow.component = EmptySpace(TextColor.ANSI.BLACK)
        mapWindow.setHints(listOf(Window.Hint.NO_DECORATIONS, Window.Hint.FULL_SCREEN))

        gui = MultiWindowTextGUI(screen, TextColor.ANSI.BLACK)
        gui.addWindow(mapWindow)
    }

    override fun start() {
        thread(start = true, isDaemon = true) {
            while (true) {
                sleep(10)
                try {
                    gui.updateScreen()
                    gui.processInput()
                } catch (e: EOFException) {
                    return@thread
                }
            }
        }

        thread(start = true, isDaemon = true) {
            while (true) {
                sleep(10)
                val action = actionQueue.poll()
                if (action != null) {
                    action()
                }
            }
        }
    }

    override fun stop() {
        gui.screen.stopScreen()
    }

    override fun setKeyPressedHandler(keyPressedHandler: (KeyPressedType) -> Unit) {
        this.keyPressedHandler = keyPressedHandler
        mapView?.keyPressedHandler = keyPressedHandler
    }

    override fun drawMap(map: MapView, width: Int, height: Int) {
        mapView = MapComponent(map, gui.screen, actionQueue, keyPressedHandler)
        mapWindow.component = mapView
    }

    override fun drawMenu(title: String, options: List<MenuOption>) {
        val builder = ActionListDialogBuilder()
        builder.title = title
        builder.isCanCancel = false

        for (option in options.filter { it.visible }) {
            builder.addAction(option.optionName) {
                actionQueue.add(option.action)
            }
        }
        val dialog = builder.build()
        dialog.setHints(listOf(Window.Hint.CENTERED))
        dialog.showDialog(gui)
    }

    override fun drawError(error: String, action: () -> Unit) {
        val builder = MessageDialogBuilder()
        builder.setTitle(ERROR_TITLE)
        builder.setText(error)
        builder.addButton(MessageDialogButton.OK)
        val dialog = builder.build()
        dialog.setHints(listOf(Window.Hint.CENTERED))
        dialog.showDialog(gui)
        actionQueue.add(action)
    }

    override fun drawFileDialog(startFile: File): File? {
        val dialog = FileDialog(
            CHOOSE_FILE_TITLE,
            null,
            "OK",
            TerminalSize(width / 2, height / 2),
            false,
            startFile
        )
        dialog.setHints(listOf(Window.Hint.CENTERED))
        return dialog.showDialog(gui)
    }

    companion object {
        private const val APP_TITLE = "Roguelike-1"
        private const val CHOOSE_FILE_TITLE = "Choose file"
        private const val ERROR_TITLE = "Error"
    }
}

