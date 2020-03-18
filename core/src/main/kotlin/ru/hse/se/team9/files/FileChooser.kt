package ru.hse.se.team9.files

import java.io.File

interface FileChooser {
    fun chooseFile(startFile: File): File?
}