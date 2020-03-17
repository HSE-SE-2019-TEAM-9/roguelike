package ru.hse.se.team9.view

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position
import java.io.File

interface View {
    fun start()

    fun drawMainMenu(options: List<MenuOption>)

    fun drawMap(map: List<List<MapObject>>, width: Int, height: Int, heroPosition: Position)

    fun drawError(error: String)

    fun drawFileDialog(selectedObject: File): File?

    fun setKeyPressedHandler(keyPressedHandler: (KeyPressedType) -> Unit)
}