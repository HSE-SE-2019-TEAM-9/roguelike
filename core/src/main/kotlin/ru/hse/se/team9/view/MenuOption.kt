package ru.hse.se.team9.view

data class MenuOption(val optionName: String, var visible:Boolean = true, val action: () -> Unit)