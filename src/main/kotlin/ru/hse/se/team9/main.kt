package ru.hse.se.team9

import ru.hse.se.team9.consoleview.ConsoleView
import ru.hse.se.team9.controller.GameController
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.model.logic.menu.MenuAction
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

    val view = ConsoleView()
    val appLogic = AppLogic(view, RandomDirection, RandomPosition, ViewFileChooser(view))
    val controller = GameController(view, appLogic)
    view.start()
    controller.start()
    appLogic.applyMenuAction(MenuAction.CHOOSE)
}
