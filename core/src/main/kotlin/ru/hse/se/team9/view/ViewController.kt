package ru.hse.se.team9.view

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position
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
    fun drawMap(map: List<List<MapObject>>, width: Int, height: Int, heroPosition: Position)

    /** Shows menu with provided menu options */
    fun drawMenu(title: String, options: List<MenuOption>)

    /** Shows error to user */
    fun drawError(error: String, action: () -> Unit)

    /** Shows file choosing dialog */
    fun drawFileDialog(startFile: File): File?
}