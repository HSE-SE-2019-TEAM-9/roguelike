package ru.hse.se.team9.entities.views

import ru.hse.se.team9.entities.FogType
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position

/** A part of map which is allowed to be shown by View. */
interface MapView {
    val hero: HeroView
    val otherHeroes: List<HeroView>
    val map: List<List<MapObject>>
    val width: Int
    val height: Int
    val fog: List<List<FogType>>
    val mobs: Map<Position, MobView>
    val items: Map<Position, ItemView>
    val consumables: Map<Position, ConsumableView>
}