package ru.hse.se.team9

import ru.hse.se.team9.consoleview.ConsoleViewController
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.model.mapgeneration.ViewFileChooser
import ru.hse.se.team9.model.random.RandomDirection
import ru.hse.se.team9.model.random.RandomPosition

fun main(args: Array<String>) {
    val view = ConsoleViewController()
    val appLogic = AppLogic(view, RandomDirection, RandomPosition, ViewFileChooser(view))
    view.start()
    appLogic.openMenu()
}
