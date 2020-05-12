package ru.hse.se.team9.game.entities.map

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.views.HeroView
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.entities.views.MobView
import ru.hse.se.team9.game.entities.hero.HeroViewImpl
import ru.hse.se.team9.game.entities.hero.consumables.ConsumableViewImpl
import ru.hse.se.team9.game.entities.hero.inventory.items.ItemViewImpl
import ru.hse.se.team9.game.entities.mobs.MobViewImpl
import ru.hse.se.team9.positions.Position

/** Adapts GameMap to MapView interface */
class MapViewImpl(gameMap: GameMap) : MapView {
    override val hero: HeroView = HeroViewImpl(gameMap.heroOnMap)
    override val otherHeroes: List<HeroView> = emptyList()
    override val map: List<List<MapObject>> = gameMap.map
    override val width = gameMap.map[0].size
    override val height = gameMap.map.size
    override val fog = gameMap.fog.fog
    override val mobs: Map<Position, MobView> = gameMap.mobs.mapValues { MobViewImpl(it.value) }
    override val items = gameMap.items.mapValues { ItemViewImpl(it.value) }
    override val consumables = gameMap.consumables.mapValues { ConsumableViewImpl }
}