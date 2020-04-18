package ru.hse.se.team9.view

/**
 * Class for storing main menu entry.
 * @property optionName name of option (e.g. "New Game")
 * @property visible shows if this option is visible currently, i.e. should it be printed the next time menu is drawn or not
 * @property action action executed when this menu option is selected (e.g. starts new game if option is "New Game")
 */
data class MenuOption(val optionName: String, var visible: Boolean = true, val action: () -> Unit)