package ru.hse.se.team9.entities.views

import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.positions.Position

/** A part of mob which is allowed to be shown by View. */
interface MobView {
    val hp: Int
    val maxHp: Int
    val properties: List<MobProperty>
}
