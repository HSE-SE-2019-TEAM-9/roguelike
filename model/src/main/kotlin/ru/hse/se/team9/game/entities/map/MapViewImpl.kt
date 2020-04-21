package ru.hse.se.team9.game.entities.map

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.views.HeroView
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.entities.views.MobView

class MapViewImpl(private val gameMap: GameMap) : MapView {
    override val mobs: List<MobView> = gameMap.mobs.map {
        object : MobView {
            override val position = it.position
            override val type = it.mob.type
        }
    }
    override val hero: HeroView = object : HeroView {
        override val position = gameMap.hero.position
    }
    override val map: List<List<MapObject>> = gameMap.map
    override val width = gameMap.map[0].size
    override val height = gameMap.map.size
    override val fog = gameMap.fog.hidden
}