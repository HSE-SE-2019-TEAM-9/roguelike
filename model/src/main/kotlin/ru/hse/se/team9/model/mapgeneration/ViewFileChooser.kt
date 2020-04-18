package ru.hse.se.team9.model.mapgeneration

import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.view.ViewController
import java.io.File

/** An implementation of FileChooser which uses ViewController. */
class ViewFileChooser(private val viewController: ViewController) : FileChooser {
    /** Redirects chooseFile operation to viewController */
    override fun chooseFile(startFile: File): File? = viewController.drawFileDialog(startFile)
}