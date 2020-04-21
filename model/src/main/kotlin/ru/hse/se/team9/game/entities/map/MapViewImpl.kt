package ru.hse.se.team9.game.entities.map

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.entities.views.HeroView
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.entities.views.MobView

/** Adapts GameMap to MapView interface */
class MapViewImpl(gameMap: GameMap) : MapView {
    override val mobs: List<MobView> = gameMap.mobs.map {
        object : MobView {
            override val position = it.key
            override val hp: Int = it.value.hp
            override val maxHp: Int = it.value.maxHp
            override val properties: List<MobProperty> = it.value.getProperties()
        }
    }
    override val hero: HeroView = object : HeroView {
        override val position = gameMap.heroOnMap.position
    }
    override val map: List<List<MapObject>> = gameMap.map
    override val width = gameMap.map[0].size
    override val height = gameMap.map.size
    override val fog = gameMap.fog.hidden
}