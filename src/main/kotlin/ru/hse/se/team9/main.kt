package ru.hse.se.team9

import ru.hse.se.team9.consoleview.ConsoleViewController
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.model.mapgeneration.RandomMapCreator
import ru.hse.se.team9.model.mapgeneration.ViewFileChooser
import ru.hse.se.team9.model.random.RandomDirection
import ru.hse.se.team9.model.random.RandomPosition
import java.io.File

fun main(args: Array<String>) {
    File("map_example").writer().use {
        val file =
        RandomMapCreator(RandomDirection, RandomPosition, 36, 36).createMap().serialize()
        it.write(file)
    }

    val view = ConsoleViewController()
    val appLogic = AppLogic(view, RandomDirection, RandomPosition, ViewFileChooser(view))
    view.start()
    appLogic.openMenu()
}
