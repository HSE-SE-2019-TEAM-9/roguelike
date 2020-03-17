package ru.hse.se.team9

import ru.hse.se.team9.consoleview.ConsoleView
import ru.hse.se.team9.controller.GameController
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.model.logic.menu.MenuAction
import ru.hse.se.team9.model.random.RandomDirection
import ru.hse.se.team9.model.random.RandomPosition

fun main(args: Array<String>) {
    val view = ConsoleView()
    val appLogic = AppLogic(view, RandomDirection, RandomPosition)
    appLogic.applyMenuAction(MenuAction.CHOOSE)
    val controller = GameController(view, appLogic)
    view.start()
    controller.start()
}
