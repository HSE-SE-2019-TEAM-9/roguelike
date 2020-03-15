package ru.hse.se.team9.model.logic.menu

import ru.hse.se.team9.model.logic.menu.windows.MainMenu
import ru.hse.se.team9.model.logic.menu.windows.Menu

class MenuLogic {
    private val menus: List<Menu>

    init {
        menus = listOf(MainMenu())
    }

    fun applyMenuAction(action: MenuAction): MenuStatus {
        return MenuStatus.NEW_GAME
    }
}