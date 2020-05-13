package ru.hse.se.team9.consoleview

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.dialogs.*
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger
import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.ItemView
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
import kotlin.math.min

/** A ViewController implementation with terminal-like gui which uses https://github.com/mabe02/lanterna framework. */
class ConsoleViewController(private val width: Int = 200, private val height: Int = 50) : ViewController {
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
        drawMapAndInventory(map, false, {}, {}, {})
    }

    /** Shows game map. */
    override fun drawInventory(
        map: MapView,
        selectEquipmentAction: (ItemType) -> Unit,
        selectInventoryAction: (Int) -> Unit,
        finishAction: () -> Unit
    ) {
        drawMapAndInventory(map, true, selectEquipmentAction, selectInventoryAction, finishAction)
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

    override fun drawConnectionDialog(
        connectAction: (String, String) -> Unit,
        validateServer: (String?) -> Boolean,
        validateUserName: (String?) -> Boolean
    ) {
        val dialog = TextInputDialogBuilder()
        dialog.extraWindowHints = setOf(Window.Hint.CENTERED)
        var serverAddress = dialog.setTitle("Enter server address:").build().showDialog(gui)
        while (!validateServer(serverAddress)) {
            drawError("Invalid server address") {}
            serverAddress = dialog.build().showDialog(gui)
        }

        var userName = dialog.setTitle("Enter username:").build().showDialog(gui)
        while (!validateUserName(userName)) {
            drawError("Invalid username") {}
            userName = dialog.build().showDialog(gui)
        }

        connectAction(userName, serverAddress)
    }

    override fun drawCreateSessionDialog(validateSessionName: (String?) -> Boolean): String {
        val dialog = TextInputDialogBuilder()
        dialog.extraWindowHints = setOf(Window.Hint.CENTERED)
        var sessionName = dialog.setTitle("Enter session name:").build().showDialog(gui)
        while (!validateSessionName(sessionName)) {
            drawError("Invalid session name") {}
            sessionName = dialog.build().showDialog(gui)
        }
        return sessionName
    }

    private fun drawMapAndInventory(
        map: MapView,
        isInventoryActive: Boolean,
        selectEquipmentAction: (ItemType) -> Unit,
        selectInventoryAction: (Int) -> Unit,
        finishAction: () -> Unit
    ) {
        mapView = MapComponent(map, gui.screen, SIDE_PANEL_WIDTH, actionQueue, keyPressedHandler)
        mapView!!.isEnabled = !isInventoryActive

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
        equipmentList.isEnabled = isInventoryActive
        equipment.forEach { (type, item) ->
            if (item.type != ItemType.NONE) {
                equipmentList.addItem(itemString(item, true, 1)) {
                    actionQueue.add { selectEquipmentAction(type) }
                }
            }
        }
        infoPanel.addComponent(equipmentList.withBorder(Borders.singleLine("Equipment")))

        val inventory = map.hero.inventory.mapIndexed { index, itemView ->
            Pair(index, itemView)
        }
        val typedInventory = mapOf(
            "Boots" to inventory.filter { it.second.type == ItemType.BOOTS }.reversed(),
            "Weapons" to inventory.filter { it.second.type == ItemType.WEAPON }.reversed(),
            "Underwear" to inventory.filter { it.second.type == ItemType.UNDERWEAR }.reversed()
        )
        val inventoryTypeBoxHeight = calcBoxPreferredHeight(
            gui.screen.terminalSize.rows,
            typedInventory.size,
            5,
            listOf(3, 3)
        )
        val inventoryPanel = Panel()
        var inventoryPanelHeight = 0
        for (type in typedInventory) {
            val inventoryList = ActionListBox()
            val height = min(inventoryTypeBoxHeight, type.value.size)
            inventoryPanelHeight += height + 2
            inventoryList.preferredSize = TerminalSize(INFINITY, height)
            inventoryList.isEnabled = isInventoryActive
            type.value.forEach {
                val (index, item) = it
                inventoryList.addItem(itemString(item, false, 2)) {
                    actionQueue.add { selectInventoryAction(index) }
                }
            }
            inventoryPanel.addComponent(inventoryList.withBorder(Borders.singleLine(type.key)))
        }
        inventoryPanel.preferredSize = TerminalSize(INFINITY, inventoryPanelHeight)
        infoPanel.addComponent(inventoryPanel.withBorder(Borders.singleLine("Inventory")))

        if (isInventoryActive) {
            val okButton = Button("OK") {
                actionQueue.add(finishAction)
            }
            infoPanel.addComponent(okButton)
        }

        val panel = Panel()
        panel.layoutManager = LinearLayout(Direction.HORIZONTAL)
        panel.addComponent(infoPanel.withBorder(Borders.singleLine()))
        panel.addComponent(mapView)
        mapWindow.component = panel
    }

    private fun itemString(item: ItemView, withType: Boolean, borderThickness: Int): String {
        val type = if (!withType) {
            ""
        } else {
            when (item.type) {
                ItemType.BOOTS -> "Boots"
                ItemType.WEAPON -> "Weapon"
                ItemType.UNDERWEAR -> "Underwear"
                ItemType.NONE -> ""
            } + ": "
        }
        val name = item.name
        val hp = when {
            item.hp > 0 -> "HP: +${item.hp}"
            item.hp < 0 -> "HP: ${item.hp}"
            else -> ""
        }
        val armor = when {
            item.armor > 0 -> "ARM: +${item.armor}"
            item.armor < 0 -> "ARM: ${item.armor}"
            else -> ""
        }
        val damage = when {
            item.damage > 0 -> "DMG: +${item.damage}"
            item.damage < 0 -> "DMG: ${item.damage}"
            else -> ""
        }
        val effect = listOf(hp, armor, damage).filter { it.isNotEmpty() }.joinToString(" | ")

        var firstPart = "$type$name"
        val secondPart = " // $effect"

        val firstPartMaxLength = SIDE_PANEL_WIDTH - borderThickness * 2 - secondPart.length - 1
        if (firstPart.length > firstPartMaxLength) {
            firstPart = firstPart.take(firstPartMaxLength - 3) + "..."
        }
        return firstPart + secondPart
    }

    private fun calcBoxPreferredHeight(
        windowHeight: Int,
        numberOfBoxes: Int,
        numberOfSingleLineElements: Int,
        otherBoxElements: List<Int>
    ): Int {

        val availableHeight =
            windowHeight - numberOfSingleLineElements - (otherBoxElements.sum() + otherBoxElements.size * 2)
        return (availableHeight - numberOfBoxes * 2) / numberOfBoxes
    }

    companion object {
        private const val APP_TITLE = "Roguelike-3"
        private const val CHOOSE_FILE_TITLE = "Choose file"
        private const val ERROR_TITLE = "Error"

        private const val SIDE_PANEL_WIDTH = 55
        private const val INFINITY = 1000 // lol
    }
}

