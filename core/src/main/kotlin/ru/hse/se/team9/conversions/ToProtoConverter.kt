package ru.hse.se.team9.conversions

import ru.hse.se.team9.entities.FogType
import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.entities.views.*
import ru.hse.se.team9.network.views.*
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.network.views.HeroView as ProtoHeroView
import ru.hse.se.team9.network.views.ItemView as ProtoItemView
import ru.hse.se.team9.network.views.MapView as ProtoMapView
import ru.hse.se.team9.network.views.MobView as ProtoMobView
import ru.hse.se.team9.network.views.Position as ProtoPosition

object ToProtoConverter {

    /** Converts kotlin MapView to a proto one */
    fun MapView.toProto(): Views.MapView {
        val mapView = this
        return ProtoMapView {
            hero = mapView.hero.toProto()
            addAllOtherHeroes(mapView.otherHeroes.map { it.toProto() })
            addAllMap(mapView.map.map { it.toProto() })
            width = mapView.width
            height = mapView.height
            addAllFog(mapView.fog.map { it.toProto() })
            addAllMobs(mapView.mobs.map { it.toProto() })
            addAllItems(mapView.items.map { it.toProto() })
            addAllConsumables(mapView.consumables.map { it.toProto() })
        }
    }

    private fun Position.toProto(): Views.Position {
        val position = this
        return ProtoPosition {
            x = position.x
            y = position.y
        }
    }

    private fun HeroView.toProto(): Views.HeroView {
        val hero = this
        return ProtoHeroView {
            position = hero.position.toProto()
            hp = hero.hp
            maxHp = hero.maxHp
            armor = hero.armor
            damage = hero.damage
            addAllInventory(hero.inventory.map { it.toProto() })
            addAllEquipment(hero.equipment.map { it.toProto() })
        }
    }

    private fun ItemView.toProto(): Views.ItemView {
        val item = this
        return ProtoItemView {
            armor = item.armor
            damage = item.damage
            hp = item.hp
            name = item.name
            type = item.type.toProto()
        }
    }

    private fun MobView.toProto(): Views.MobView {
        val mob = this
        return ProtoMobView {
            hp = mob.hp
            maxHp = mob.maxHp
            addAllProperties(mob.properties.map { it.toProto() })
        }
    }

    private fun List<MapObject>.toProto(): Views.MapRow {
        val list = this
        return MapRow {
            addAllObjects(list.map { it.toProto() })
        }
    }

    private fun List<FogType>.toProto(): Views.FogRow {
        val list = this
        return FogRow {
            addAllType(list.map { it.toProto() })
        }
    }

    private fun Map.Entry<Position, MobView>.toProto(): Views.MobPair {
        val entry = this
        return MobPair {
            position = entry.key.toProto()
            mob = entry.value.toProto()
        }
    }

    private fun Map.Entry<Position, ItemView>.toProto(): Views.ItemPair {
        val entry = this
        return ItemPair {
            position = entry.key.toProto()
            item = entry.value.toProto()
        }
    }

    private fun Map.Entry<Position, ConsumableView>.toProto(): Views.ConsumablePair {
        val entry = this
        return ConsumablePair {
            position = entry.key.toProto()
            consumable = entry.value.toProto()
        }
    }

    private fun Map.Entry<ItemType, ItemView>.toProto(): Views.EquipmentPair {
        val entry = this
        return EquipmentPair {
            type = entry.key.toProto()
            item = entry.value.toProto()
        }
    }

    private fun ItemType.toProto(): Views.ItemView.ItemType {
        return when (this) {
            ItemType.BOOTS -> Views.ItemView.ItemType.BOOTS
            ItemType.WEAPON -> Views.ItemView.ItemType.WEAPON
            ItemType.UNDERWEAR -> Views.ItemView.ItemType.UNDERWEAR
            ItemType.NONE -> Views.ItemView.ItemType.NONE
        }
    }

    private fun MapObject.toProto(): Views.MapRow.MapObject {
        return when (this) {
            MapObject.WALL -> Views.MapRow.MapObject.WALL
            MapObject.EMPTY_SPACE -> Views.MapRow.MapObject.EMPTY_SPACE
        }
    }

    private fun FogType.toProto(): Views.FogRow.FogType {
        return when (this) {
            FogType.VISIBLE -> Views.FogRow.FogType.VISIBLE
            FogType.SHADOWED -> Views.FogRow.FogType.SHADOWED
            FogType.INVISIBLE -> Views.FogRow.FogType.INVISIBLE
        }
    }

    private fun MobProperty.toProto(): Views.MobView.MobProperty {
        return when (this) {
            MobProperty.CONFUSED -> Views.MobView.MobProperty.CONFUSED
        }
    }

    private fun ConsumableView.toProto(): Views.ConsumableView {
        return ConsumableView {}
    }
}