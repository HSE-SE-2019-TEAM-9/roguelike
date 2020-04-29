package ru.hse.se.team9.entities.views

import ru.hse.se.team9.entities.ItemType

/** A part of item which is allowed to be shown by View. */
interface ItemView {
    val armor: Int
    val damage: Int
    val hp: Int
    val name: String
    val type: ItemType
}