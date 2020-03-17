package ru.hse.se.team9.consoleview

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.Window
import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder
import com.googlecode.lanterna.gui2.dialogs.FileDialog
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.Terminal
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.view.KeyPressedType
import ru.hse.se.team9.view.MenuOption
import ru.hse.se.team9.view.View
import java.io.File

class ConsoleView(private val width: Int = 150, private val height: Int = 50): View {
    private val terminal: Terminal
    private val gui: WindowBasedTextGUI

    init {
        terminal = SwingTerminalFrame(
            APP_TITLE,
            TerminalSize(width, height),
            null,
            null,
            null,
            TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode
        )
        terminal.isVisible = true
        terminal.isResizable = false
        val screen = TerminalScreen(terminal)
        screen.startScreen()
        screen.cursorPosition = null

        gui = MultiWindowTextGUI(screen, TextColor.ANSI.BLACK)
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
        gui.backgroundPane.component = MapComponent(map, width, height, heroPosition)
        gui.updateScreen()
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

    override fun getPressedKey(): KeyPressedType {
        val input = terminal.readInput()
        return when (input.keyType) {
            KeyType.ArrowUp -> KeyPressedType.UP
            KeyType.ArrowDown -> KeyPressedType.DOWN
            KeyType.ArrowLeft -> KeyPressedType.LEFT
            KeyType.ArrowRight -> KeyPressedType.RIGHT
            else -> KeyPressedType.OTHER
        }
    }

    companion object {
        private const val APP_TITLE = "Roguelike-1"
        private const val MAIN_MENU_TITLE = "Main menu"
        private const val CHOOSE_FILE_TITLE = "Choose file"
        private const val ERROR_TITLE = "Error"
    }
}