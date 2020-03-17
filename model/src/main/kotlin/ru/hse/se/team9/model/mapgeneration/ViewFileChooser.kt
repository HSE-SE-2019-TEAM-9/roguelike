package ru.hse.se.team9.model.mapgeneration

import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.view.ViewController
import java.io.File

class ViewFileChooser(private val viewController: ViewController): FileChooser {
    override fun chooseFile(file: File) = viewController.drawFileDialog(File("."))
}