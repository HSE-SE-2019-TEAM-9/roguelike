package ru.hse.se.team9.entities.views

import ru.hse.se.team9.entities.FogType
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position

/** A part of map which is allowed to be shown by View. */
interface MapView {
    val mobs: List<MobView>
    val hero: HeroView
    val map: List<List<MapObject>>
    val width: Int
    val height: Int
    val fog: List<List<FogType>>
    val items: Map<Position, ItemView>
}