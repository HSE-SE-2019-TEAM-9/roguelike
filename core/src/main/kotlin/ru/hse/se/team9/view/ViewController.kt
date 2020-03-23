package ru.hse.se.team9.view

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.positions.Position
import java.io.File

interface ViewController {
    fun start()

    fun stop()

    fun setKeyPressedHandler(keyPressedHandler: (KeyPressedType) -> Unit)

    fun drawMap(map: MapView)

    fun drawMenu(title: String, options: List<MenuOption>)

    fun drawError(error: String, action: () -> Unit)

    fun drawFileDialog(startFile: File): File?
}