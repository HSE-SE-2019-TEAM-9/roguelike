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
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.game.entities.map.distance.Manhattan
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.PassiveStrategy
import ru.hse.se.team9.game.entities.mobs.strategies.CowardStrategy
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.model.random.directions.RandomDirection
import ru.hse.se.team9.model.random.positions.PositionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.getRandomNotWallPosition
import ru.hse.se.team9.utils.plus

/**
 * Represents game map
 * @property heroOnMap hero and its position on map
 * @property map two-dimensional array of MapObject, stores landscape elements and not active participants of game
 * @property width width of map
 * @property height height of map
 * @property positionGenerator object using for generating random positions on map
 * @property mobs a map from mob position to mob
 * @property distance a metric used for map-related processes
 * @property fogRadius how far hero sees
 */
class GameMap(
    val heroOnMap: HeroOnMap,
    val map: List<MutableList<MapObject>>,
    val width: Int,
    val height: Int,
    private val positionGenerator: PositionGenerator,
    var mobs: MutableMap<Position, Mob>,
    val distance: Distance,
    val fogRadius: Int
) {
    val fog = FogOfWar(distance, map, width, height, fogRadius)

    init {
        fog.updateVision(heroOnMap.position)
    }

    /** Moves hero to the neighbor cell according to the direction. If this position is occupied, does nothing.
     * @param direction direction to move towards
     */
    fun moveHero(direction: Direction) {
        val position = heroOnMap.position + direction
        if (canMoveTo(position)) {
            heroOnMap.position = position
            fog.updateVision(position)
        }
    }

    /** Moves mob to the given position.
     * If this position is occupied or no mob is located at previous position, does nothing
     * @param previousPosition position of moving mob
     * @param newPosition position to move hero to
     */
    fun moveMob(previousPosition: Position, newPosition: Position) {
        if (canMoveTo(newPosition)) {
            mobs.remove(previousPosition)?.let {
                mobs.put(newPosition, it)
            }
        }
    }

    /** Removes mob from map. If no mob is located at given position does nothing */
    fun removeMob(position: Position) {
        mobs.remove(position)
    }

    private fun isOnMap(position: Position): Boolean {
        val (x, y) = position
        return x >= 0 && y >= 0 && x < width && y < height
    }

    private fun isNotWall(position: Position): Boolean {
        val (x, y) = position
        return map[y][x] !is Wall
    }

    private fun isNotMob(position: Position): Boolean {
        return !mobs.containsKey(position)
    }

    /** Checks that given position exists and is not occupied. */
    fun canMoveTo(position: Position): Boolean {
        return isOnMap(position) && isNotWall(position) && isNotMob(position)
    }

    /** Writes this map object as string */
    fun serialize(): String = mapper.writeValueAsString(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameMap

        if (heroOnMap != other.heroOnMap) return false
        if (map != other.map) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (positionGenerator != other.positionGenerator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = heroOnMap.hashCode()
        result = 31 * result + map.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + positionGenerator.hashCode()
        return result
    }

    // TODO need A LOT OF fixes
    companion object Serialization {
        /**
         * Reads map from string and puts specified positionGenerator in it.
         * Accepts strings which were generated by GameMap::serialize method.
         */
        fun deserialize(
            string: String,
            positionGenerator: PositionGenerator
        ): Either<Throwable, GameMap> {
            return try {
                val gameMap = mapper.readValue<DeserializedGameMap>(string)
                Either.right(
                    GameMap(
                        gameMap.hero,
                        gameMap.map,
                        gameMap.width,
                        gameMap.height,
                        positionGenerator,
                        gameMap.mobs,
                        Manhattan,
                        gameMap.fogRadius
                    )
                )
            } catch (e: Throwable) {
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
            val mobs: MutableMap<Position, Mob>,
            val fogRadius: Int
        )

        private class GameMapSerializer : JsonSerializer<GameMap>() {
            override fun serialize(value: GameMap, gen: JsonGenerator, serializers: SerializerProvider) {
                gen.writeStartObject()
                gen.writeObjectField("hero", value.heroOnMap)
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
                    "R" -> CowardStrategy(directionGenerator)
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
                    is CowardStrategy -> gen.writeString("R")
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