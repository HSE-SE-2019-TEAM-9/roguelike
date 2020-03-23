package ru.hse.se.team9.files

import java.io.File

/** Interface for traversing the filesystem */
interface FileChooser {
    /** Traverse file system
     *
     * @param startFile start point of traversal
     * @return end point of traversal -- selected file
     */
    fun chooseFile(startFile: File): File?
}