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
import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.view.KeyPressedType
import ru.hse.se.team9.view.MenuOption
import ru.hse.se.team9.view.ViewController
import java.io.EOFException
import java.io.File
import java.lang.NullPointerException
import java.lang.Thread.sleep
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/** A ViewController implementation with terminal-like gui which uses https://github.com/mabe02/lanterna framework. */
class ConsoleViewController(private val width: Int = 150, private val height: Int = 50) : ViewController {
    private val gui: WindowBasedTextGUI
    private val mapWindow: BasicWindow
    private var mapView: MapComponent? = null
    private var keyPressedHandler: (KeyPressedType) -> Unit = {}
    private val actionQueue: LinkedBlockingQueue<() -> Unit> = LinkedBlockingQueue()

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

    /**
     * Starts ui-thread and a thread for processing new actions.
     */
    override fun start() {
        thread(start = true, isDaemon = true, name = "gui updater thread") {
            while (true) {
                sleep(10)
                try {
                    gui.updateScreen()
                    gui.processInput()
                } catch (e: NullPointerException) {
                    // Although it is stated that it is safe to work with Lanterna from multiple threads
                    // it has a concurrency bug (AbstractComposite.setComponent is NOT thread safe)
                } catch (e: EOFException) {
                    return@thread
                }
            }
        }

        thread(start = true, isDaemon = true, name = "actionQueue poller thread") {
            while (true) {
                val action = actionQueue.poll(Long.MAX_VALUE, TimeUnit.DAYS)
                if (action != null) { // action is never null
                    action()
                }
            }
        }
    }

    /** closes GUI */
    override fun stop() {
        gui.screen.stopScreen()
    }

    /**
     * Sets callbacks for keyboard actions.
     * All callbacks will be run in the one thread. Therefore it is allowed for callbacks to be not thread-safe.
     */
    override fun setKeyPressedHandler(keyPressedHandler: (KeyPressedType) -> Unit) {
        this.keyPressedHandler = keyPressedHandler
        mapView?.keyPressedHandler = keyPressedHandler
    }

    /** Shows game map. */
    override fun drawMap(map: MapView) {
        mapView = MapComponent(map, gui.screen, SIDE_PANEL_WIDTH, actionQueue, keyPressedHandler)

        val infoPanel = Panel()
        infoPanel.preferredSize = TerminalSize(SIDE_PANEL_WIDTH, INFINITY)

        val stats = Label(
            "Health: ${map.hero.hp}/${map.hero.maxHp}\nArmor: ${map.hero.armor}\nDamage: ${map.hero.damage}"
        )
        stats.preferredSize = TerminalSize(INFINITY, 3)
        infoPanel.addComponent(stats.withBorder(Borders.singleLine("Stats")))

        val equipment = map.hero.equipment
        val equipmentList = ActionListBox()
        equipmentList.preferredSize = TerminalSize(INFINITY, equipment.size)
        equipmentList.isEnabled = false
        equipment.forEach {
            equipmentList.addItem(it.name) {} // FIXME
        }
        infoPanel.addComponent(equipmentList.withBorder(Borders.singleLine("Equipment")))

        val inventory = map.hero.inventory.mapIndexed { index, itemView ->
            Pair(index, itemView)
        }
        val typedInventory = mapOf(
            "Boots" to inventory.filter { it.second.type == ItemType.BOOTS },
            "Weapon" to inventory.filter { it.second.type == ItemType.WEAPON },
            "Underwear" to inventory.filter { it.second.type == ItemType.UNDERWEAR }
        )
        val inventoryPanel = Panel()
        inventoryPanel.preferredSize = TerminalSize(INFINITY, INFINITY)
        for (type in typedInventory) {
            val inventoryList = ActionListBox()
            inventoryList.preferredSize = TerminalSize(INFINITY, type.value.size)
            inventoryList.isEnabled = false
            type.value.forEach {
                inventoryList.addItem(it.second.name) {} // FIXME
            }
            inventoryPanel.addComponent(inventoryList.withBorder(Borders.singleLine(type.key)))
        }
        infoPanel.addComponent(inventoryPanel.withBorder(Borders.singleLine( "Inventory")))

        val panel = Panel()
        panel.layoutManager = LinearLayout(Direction.HORIZONTAL)
        panel.addComponent(infoPanel.withBorder(Borders.singleLine()))
        panel.addComponent(mapView)
        mapWindow.component = panel
    }

    /** Shows menu with provided menu options */
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

    /** Shows error in a small window */
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

    /** Shows lanterna's native file dialog */
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
        private const val APP_TITLE = "Roguelike-2"
        private const val CHOOSE_FILE_TITLE = "Choose file"
        private const val ERROR_TITLE = "Error"

        private const val SIDE_PANEL_WIDTH = 30
        private const val INFINITY = 1000 // lol
    }
}

