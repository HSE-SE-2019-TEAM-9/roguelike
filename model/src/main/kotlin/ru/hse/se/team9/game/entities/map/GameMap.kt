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
import ru.hse.se.team9.game.entities.map.distance.Descartes
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.game.entities.map.distance.Manhattan
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.map.objects.MobOnMap
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.PassiveStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.RandomStrategy
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.model.random.directions.RandomDirection
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
    val mobs: List<MobOnMap>,
    val distance: Distance,
    val fogRadius: Int
) {
    val fog = FogOfWar(distance, map, width, height, fogRadius)

    init {
        fog.updateVision(hero.position)
    }

    fun moveHero(newPosition: Position) {
        if (canMoveTo(newPosition)) {
            hero.position = newPosition
        }
    }

    fun moveHero(direction: Direction) {
        val position = hero.position + direction
        if (canMoveTo(position)) {
            hero.position = position
            fog.updateVision(position)
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

    // TODO need A LOT OF fixes
    companion object Serialization {
        fun deserialize(
            string: String,
            positionGenerator: PositionGenerator
        ): Either<Throwable, GameMap> {
            return try {
                val gameMap = mapper.readValue<DeserializedGameMap>(string)
                Either.right(GameMap(
                    gameMap.hero,
                    gameMap.map,
                    gameMap.width,
                    gameMap.height,
                    positionGenerator,
                    gameMap.mobs,
                    Manhattan,
                    gameMap.fogRadius
                ))
            } catch(e: Throwable) {
                Either.left(e)
            }
        }

        private val mapper = jacksonObjectMapper().registerModule(SimpleModule().apply {
            addSerializer(GameMap::class.java, GameMapSerializer())
            addSerializer(MapObject::class.java, MapObjectSerializer())
            addDeserializer(MapObject::class.java, MapObjectDeserializer())
            addSerializer(MobStrategy::class.java, StrategySerializer())
            addDeserializer(MobStrategy::class.java, StrategyDeserializer(RandomDirection)) // TODO fix
//            addSerializer(Distance::class.java, DistanceSerializer())
//            addDeserializer(Distance::class.java, DistanceDeserializer())
        })

        /*class DistanceDeserializer : JsonDeserializer<Distance>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Distance {
                return when (val str = p.valueAsString) {
                    "M" -> Manhattan
                    "D" -> Descartes
                    else -> ctxt.handleWeirdStringValue(
                        Distance::class.java,
                        str,
                        "cannot deserialize map object"
                    ) as Distance
                }
            }

        }

        class DistanceSerializer : JsonSerializer<Distance>() {
            override fun serialize(value: Distance, gen: JsonGenerator, serializers: SerializerProvider) {
                when (value) {
                    is Manhattan -> gen.writeString("M")
                    is Descartes -> gen.writeString("D")
                }
            }
        }*/


        private data class DeserializedGameMap(
            val hero: HeroOnMap,
            val map: List<MutableList<MapObject>>,
            val width: Int,
            val height: Int,
            val mobs: List<MobOnMap>,
            val fogRadius: Int
        )

        private class GameMapSerializer : JsonSerializer<GameMap>() {
            override fun serialize(value: GameMap, gen: JsonGenerator, serializers: SerializerProvider) {
                gen.writeStartObject()
                gen.writeObjectField("hero", value.hero)
                gen.writeNumberField("width", value.width)
                gen.writeNumberField("height", value.height)
                gen.writeObjectField("map", value.map)
                gen.writeObjectField("mobs", value.mobs)
                //gen.writeObjectField("distance", value.distance)
                gen.writeNumberField("fogRadius", value.fogRadius)
                gen.writeEndObject()
            }
        }

        // TODO fix
        private class StrategyDeserializer(
            val directionGenerator: DirectionGenerator
        ) : JsonDeserializer<MobStrategy>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MobStrategy {
                return when (val str = p.valueAsString) {
                    "R" -> RandomStrategy(directionGenerator)
                    "P" -> PassiveStrategy
                    else -> ctxt.handleWeirdStringValue(
                        MobStrategy::class.java,
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
        }

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