package ru.hse.se.team9.view

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position

interface View {
    fun drawMainMenu(options: List<String>, position: Int)

    fun drawMap(map: List<List<MapObject>>, width: Int, height: Int, heroPosition: Position)
}