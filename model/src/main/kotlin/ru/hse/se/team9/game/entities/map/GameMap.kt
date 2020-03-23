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
import com.fasterxml.jackson.module.kotlin.*
import ru.hse.se.team9.entities.EmptySpace
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.model.random.PositionGenerator

// TODO add wrapper
/** Represents game map
 * @property hero hero and its position on map
 * @property map two-dimensional array of MapObject, stores landscape elements and not active participants of game
 * @property width width of map
 * @property height height of map
 * @property positionGenerator object using for generating random positions on map
 */
class GameMap(
    val hero: HeroOnMap,
    val map: List<MutableList<MapObject>>,
    val width: Int,
    val height: Int,
    private val positionGenerator: PositionGenerator
) {

    /** Moves hero to the given position. If map has wall at this position, does nothing
     * @param newPosition position to move hero to
     */
    fun moveHero(newPosition: Position) {
        if (canMoveTo(newPosition)) {
            hero.position = newPosition
        }
    }

    /** Moves hero to the neighbor cell according to the direction. If map has wall at this position, does nothing.
     * @param direction direction to move towards
     */
    fun moveHero(direction: Direction) {
        val (x, y) = hero.position
        val position = when (direction) {
            Direction.LEFT -> Position(x - 1, y)
            Direction.RIGHT -> Position(x + 1, y)
            Direction.DOWN -> Position(x, y + 1)
            Direction.UP -> Position(x, y - 1)
        }
        if (canMoveTo(position)) {
            hero.position = position
        }
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

    /** Writes this map object as string */
    fun serialize(): String = mapper.writeValueAsString(this)

    private tailrec fun getRandomNotWallPosition(): Position {
        val (x, y) = positionGenerator.createPosition(width, height)
        return if (map[y][x] is Wall) {
            getRandomNotWallPosition()
        } else {
            Position(x, y)
        }
    }

    companion object Serialization {
        fun deserialize(string: String, positionGenerator: PositionGenerator): Either<Throwable, GameMap> {
            return try {
                val deserialized = mapper.readValue<DeserializedGameMap>(string)
                Either.right(GameMap(
                    deserialized.hero,
                    deserialized.map,
                    deserialized.width,
                    deserialized.height,
                    positionGenerator
                ))
            } catch(e: Throwable) {
                Either.left(e)
            }
        }

        private val mapper = jacksonObjectMapper().registerModule(SimpleModule().apply {
            addSerializer(GameMap::class.java, GameMapSerializer())
            addSerializer(MapObject::class.java, MapObjectSerializer())
            addDeserializer(MapObject::class.java, MapObjectDeserializer())
        })

        private data class DeserializedGameMap(
            val hero: HeroOnMap,
            val map: List<MutableList<MapObject>>,
            val width: Int,
            val height: Int
        )

        private class GameMapSerializer : JsonSerializer<GameMap>() {
            override fun serialize(value: GameMap, gen: JsonGenerator, serializers: SerializerProvider) {
                gen.writeStartObject()
                gen.writeObjectField("hero", value.hero)
                gen.writeNumberField("width", value.width)
                gen.writeNumberField("height", value.height)
                gen.writeObjectField("map", value.map)
                gen.writeEndObject()
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