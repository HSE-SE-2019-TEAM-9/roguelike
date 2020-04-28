package ru.hse.se.team9.game.entities.map

import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.entities.views.HeroView
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.entities.views.MobView
import ru.hse.se.team9.game.entities.hero.consumables.ConsumableViewImpl
import ru.hse.se.team9.game.entities.hero.inventory.items.ItemViewImpl
import java.util.stream.Collectors

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
        override val hp = gameMap.heroOnMap.hero.stats.hp
        override val maxHp = gameMap.heroOnMap.hero.stats.maxHp
        override val armor = gameMap.heroOnMap.hero.stats.armor
        override val damage = gameMap.heroOnMap.hero.stats.damage
        override val inventory = gameMap.heroOnMap.hero.inventory.stream()
                .map { ItemViewImpl(it) }.collect(Collectors.toList())
        override val equipment = gameMap.heroOnMap.hero.equipment.getItems().mapValues { ItemViewImpl(it.value) }
    }
    override val map: List<List<MapObject>> = gameMap.map
    override val width = gameMap.map[0].size
    override val height = gameMap.map.size
    override val fog = gameMap.fog.fog
    override val items = gameMap.items.mapValues { ItemViewImpl(it.value) }
    override val consumables = gameMap.consumables.mapValues { ConsumableViewImpl }
}