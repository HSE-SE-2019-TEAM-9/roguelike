package ru.hse.se.team9.entities.views

import ru.hse.se.team9.entities.ItemType

interface ItemView {
    val armor: Int
    val damage: Int
    val hp: Int
    val name: String
    val type: ItemType
}