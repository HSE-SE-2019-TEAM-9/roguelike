package ru.hse.se.team9.entities.views

import ru.hse.se.team9.entities.MobModifier
import ru.hse.se.team9.positions.Position

interface MobView {
    val position: Position
    val hp: Int
    val maxHp: Int
    val modifiers: List<MobModifier>
}
