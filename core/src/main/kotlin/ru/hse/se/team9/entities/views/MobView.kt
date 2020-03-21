package ru.hse.se.team9.entities.views

import ru.hse.se.team9.entities.MobType
import ru.hse.se.team9.positions.Position

interface MobView {
    val position: Position
    val type: MobType
}
