package ru.hse.se.team9.conversions

import ru.hse.se.team9.entities.FogType
import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.entities.views.*
import ru.hse.se.team9.network.views.Views
import ru.hse.se.team9.positions.Position

object FromProtoConverter {
    fun Views.MapView.toView(): MapView {
        return MapViewConverted(
            hero.toView(),
            mapList.toViewMap(),
            width,
            height,
            fogList.toViewFog(),
            mobsList.toViewMobs(),
            itemsList.toViewItems(),
            consumablesList.toViewConsumables()
        )
    }

    private fun Views.HeroView.toView(): HeroView {
        return HeroViewConverted(
            position.toView(),
            hp,
            maxHp,
            armor,
            damage,
            inventoryList.toViewItems(),
            equipmentList.toView()
        )
    }

    private fun Views.MobView.toView(): MobView {
        return MobViewConverted(
            hp,
            maxHp,
            propertiesList.toView()
        )
    }

    private fun Views.ItemView.toView(): ItemView {
        return ItemViewConverted(
            armor,
            damage,
            hp,
            name,
            type.toView()
        )
    }

    // Other name because of type erasure
    private fun List<Views.MapColumn>.toViewMap(): List<List<MapObject>> {
        return this.map { column ->
            column.objectsList.map { it.toView() }
        }
    }

    // Other name because of type erasure
    private fun List<Views.FogColumn>.toViewFog(): List<List<FogType>> {
        return this.map { column ->
            column.typeList.map { it.toView() }
        }
    }

    // Other name because of type erasure
    private fun List<Views.MobPair>.toViewMobs(): Map<Position, MobView> {
        return this.map {
            it.position.toView() to it.mob.toView()
        }.toMap()
    }

    // Other name because of type erasure
    private fun List<Views.ItemPair>.toViewItems(): Map<Position, ItemView> {
        return this.map {
            it.position.toView() to it.item.toView()
        }.toMap()
    }

    // Other name because of type erasure
    private fun List<Views.ConsumablePair>.toViewConsumables(): Map<Position, ConsumableView> {
        return this.map {
            it.position.toView() to it.consumable.toView()
        }.toMap()
    }

    private fun Views.Position.toView(): Position {
        return Position(x, y)
    }

    // Other name because of type erasure
    private fun List<Views.ItemView>.toViewItems(): List<ItemView> {
        return this.map {
            it.toView()
        }
    }

    private fun List<Views.EquipmentPair>.toView(): Map<ItemType, ItemView> {
        return this.map {
            it.type.toView() to it.item.toView()
        }.toMap()
    }

    private fun Views.MapColumn.MapObject.toView(): MapObject {
        return when (this) {
            Views.MapColumn.MapObject.WALL -> MapObject.WALL
            Views.MapColumn.MapObject.EMPTY_SPACE -> MapObject.EMPTY_SPACE
            Views.MapColumn.MapObject.UNRECOGNIZED -> error(ERROR_MESSAGE)
        }
    }

    private fun Views.FogColumn.FogType.toView(): FogType {
        return when (this) {
            Views.FogColumn.FogType.VISIBLE -> FogType.VISIBLE
            Views.FogColumn.FogType.SHADOWED -> FogType.SHADOWED
            Views.FogColumn.FogType.INVISIBLE -> FogType.INVISIBLE
            Views.FogColumn.FogType.UNRECOGNIZED -> error(ERROR_MESSAGE)
        }
    }

    private fun List<Views.MobView.MobProperty>.toView(): List<MobProperty> {
        return this.map { it.toView() }
    }

    private fun Views.ItemView.ItemType.toView(): ItemType {
        return when (this) {
            Views.ItemView.ItemType.NONE -> ItemType.NONE
            Views.ItemView.ItemType.BOOTS -> ItemType.BOOTS
            Views.ItemView.ItemType.WEAPON -> ItemType.WEAPON
            Views.ItemView.ItemType.UNDERWEAR -> ItemType.UNDERWEAR
            Views.ItemView.ItemType.UNRECOGNIZED -> error(ERROR_MESSAGE)
        }
    }

    private fun Views.ConsumableView.toView(): ConsumableView {
        return object: ConsumableView {}
    }

    private fun Views.MobView.MobProperty.toView(): MobProperty {
        return when (this) {
            Views.MobView.MobProperty.CONFUSED -> MobProperty.CONFUSED
            Views.MobView.MobProperty.UNRECOGNIZED -> error(ERROR_MESSAGE)
        }
    }

    private data class MapViewConverted(
        override val hero: HeroView,
        override val map: List<List<MapObject>>,
        override val width: Int,
        override val height: Int,
        override val fog: List<List<FogType>>,
        override val mobs: Map<Position, MobView>,
        override val items: Map<Position, ItemView>,
        override val consumables: Map<Position, ConsumableView>
    ) : MapView

    private data class HeroViewConverted(
        override val position: Position,
        override val hp: Int,
        override val maxHp: Int,
        override val armor: Int,
        override val damage: Int,
        override val inventory: List<ItemView>,
        override val equipment: Map<ItemType, ItemView>
    ) : HeroView

    private data class MobViewConverted(
        override val hp: Int,
        override val maxHp: Int,
        override val properties: List<MobProperty>
    ) : MobView

    private data class ItemViewConverted(
        override val armor: Int,
        override val damage: Int,
        override val hp: Int,
        override val name: String,
        override val type: ItemType
    ) : ItemView

    private const val ERROR_MESSAGE = "deserialization error"
}