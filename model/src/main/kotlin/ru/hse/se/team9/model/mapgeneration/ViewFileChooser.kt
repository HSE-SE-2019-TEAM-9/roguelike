package ru.hse.se.team9.model.mapgeneration

import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.view.View
import java.io.File

class ViewFileChooser(private val view: View): FileChooser {
    override fun chooseFile(file: File) = view.drawFileDialog(File("."))
}