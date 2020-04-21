package ru.hse.se.team9.util

import ru.hse.se.team9.files.FileChooser
import java.io.File

fun <T> getResourceFile(testClass: Class<T>, name: String): File {
    return File(testClass.getResource(name).file)
}

class DummyFileChooser(private val result: File) : FileChooser {
    override fun chooseFile(startFile: File): File? {
        return result
    }
}