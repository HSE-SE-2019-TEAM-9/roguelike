package ru.hse.se.team9.game.entities.map

import arrow.core.Either
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.hse.se.team9.entities.EmptySpace
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.map.objects.MobOnMap
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.PassiveStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.RandomStrategy
import ru.hse.se.team9.model.random.global.GameGenerator
import ru.hse.se.team9.model.random.positions.PositionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.getRandomNotWallPosition
import ru.hse.se.team9.utils.plus

class GameMap(
    val hero: HeroOnMap,
    val map: List<MutableList<MapObject>>,
    val width: Int,
    val height: Int,
    private val positionGenerator: PositionGenerator,
    val mobs: List<MobOnMap>
) {

    fun moveHero(newPosition: Position) {
        if (canMoveTo(newPosition)) {
            hero.position = newPosition
        }
    }

    fun moveHero(direction: Direction) {
        val position = hero.position + direction
        if (canMoveTo(position)) {
            hero.position = position
        }
    }

    fun moveMob(mobOnMap: MobOnMap, newPosition: Position) {
        for (mob in mobs) {
            if (mobOnMap == mob) {
                if (canMoveTo(newPosition)) {
                    mob.position = newPosition
                }
            }
        }
    }

    fun placeAtRandomPosition(mapObject: MapObject) {
        val (x, y) = getRandomNotWallPosition(positionGenerator, map)
        map[y][x] = mapObject
    }

    private fun isOnMap(position: Position): Boolean {
        val (x, y) = position
        return x >= 0 && y >= 0 && x < width && y < height
    }

    private fun isNotWall(position: Position): Boolean {
        val (x, y) = position
        return map[y][x] !is Wall
    }

    private fun canMoveTo(position: Position): Boolean {
        return isOnMap(position) && isNotWall(position)
    }

    fun serialize(): String = mapper.writeValueAsString(this)

    // TODO add mobs serialization
    companion object Serialization {
        fun deserialize(string: String, positionGenerator: PositionGenerator): Either<Throwable, GameMap> {
            return try {
                val deserialized = mapper.readValue<DeserializedGameMap>(string)
                Either.right(GameMap(
                    deserialized.hero,
                    deserialized.map,
                    deserialized.width,
                    deserialized.height,
                    positionGenerator,
                    deserialized.mobs
                ))
            } catch(e: Throwable) {
                Either.left(e)
            }
        }

        private val mapper = jacksonObjectMapper().registerModule(SimpleModule().apply {
            addSerializer(GameMap::class.java, GameMapSerializer())
            addSerializer(MapObject::class.java, MapObjectSerializer())
            addDeserializer(MapObject::class.java, MapObjectDeserializer())
            /*addSerializer(MobStrategy::class.java, StrategySerializer())
            addDeserializer(MobStrategy::class.java, StrategyDeserializer(gameGenerator))*/
        })


        private data class DeserializedGameMap(
            val hero: HeroOnMap,
            val map: List<MutableList<MapObject>>,
            val width: Int,
            val height: Int,
            val mobs: List<MobOnMap>
        )

        private class GameMapSerializer : JsonSerializer<GameMap>() {
            override fun serialize(value: GameMap, gen: JsonGenerator, serializers: SerializerProvider) {
                gen.writeStartObject()
                gen.writeObjectField("hero", value.hero)
                gen.writeNumberField("width", value.width)
                gen.writeNumberField("height", value.height)
                gen.writeObjectField("map", value.map)
                gen.writeObjectField("mobs", value.mobs)
                gen.writeEndObject()
            }
        }

        // TODO fix
        /*private class StrategyDeserializer : JsonDeserializer<MobStrategy>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MobStrategy {
                return when (val str = p.valueAsString) {
                    "R" -> RandomStrategy(gameGenerator)
                    "P" -> PassiveStrategy
                    else -> ctxt.handleWeirdStringValue(
                        MapObject::class.java,
                        str,
                        "cannot deserialize map object"
                    ) as MobStrategy
                }
            }
        }

        private class StrategySerializer : JsonSerializer<MobStrategy>() {
            override fun serialize(value: MobStrategy, gen: JsonGenerator, serializers: SerializerProvider) {
                when (value) {
                    is RandomStrategy -> gen.writeString("R")
                    is PassiveStrategy -> gen.writeString("P")
                }
            }
        }*/

        private class MapObjectSerializer : JsonSerializer<MapObject>() {
            override fun serialize(value: MapObject, gen: JsonGenerator, serializers: SerializerProvider) {
                when (value) {
                    is EmptySpace -> gen.writeString(".")
                    is Wall -> gen.writeString("@")
                }
            }
        }

        private class MapObjectDeserializer : JsonDeserializer<MapObject>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MapObject? {
                return when (val str = p.valueAsString) {
                    "." -> EmptySpace
                    "@" -> Wall
                    else -> ctxt.handleWeirdStringValue(
                        MapObject::class.java,
                        str,
                        "cannot deserialize map object"
                    ) as MapObject
                }
            }
        }
    }
}