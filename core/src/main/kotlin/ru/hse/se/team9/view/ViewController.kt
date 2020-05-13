package ru.hse.se.team9.view

import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.MapView
import java.io.File

/**
 * An interface for ViewController component. This component is responsible for interactions with user:
 * interface and keyboard processing.
 */
interface ViewController {
    /** Starts this component. */
    fun start()

    /** Stops this component. */
    fun stop()

    /** Sets callbacks for keyboard actions. */
    fun setKeyPressedHandler(keyPressedHandler: (KeyPressedType) -> Unit)

    /** Shows specified map to user. */
    fun drawMap(map: MapView)

    /** Shows inventory to user. */
    fun drawInventory(map: MapView,
                      selectEquipmentAction: (ItemType) -> Unit,
                      selectInventoryAction: (Int) -> Unit,
                      finishAction: () -> Unit)

    /** Shows menu with provided menu options */
    fun drawMenu(title: String, options: List<MenuOption>)

    /** Shows error to user */
    fun drawError(error: String, action: () -> Unit)

    /** Shows file choosing dialog */
    fun drawFileDialog(startFile: File): File?

    /** Shows server connection dialog */
    fun drawCreateSessionDialog(validateSessionName: (String?) -> Boolean): String

    /** Shows session creation dialog */
    fun drawConnectionDialog(
        connectAction: (String, String) -> Unit,
        validateServer: (String?) -> Boolean,
        validateUserName: (String?) -> Boolean
    )
}
