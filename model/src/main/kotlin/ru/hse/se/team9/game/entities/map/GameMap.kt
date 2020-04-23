package ru.hse.se.team9.game.entities.map

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.model.random.positions.PositionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.plus
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

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
        return map[y][x] != MapObject.WALL
    }

    private fun isNotMob(position: Position): Boolean {
        return !mobs.containsKey(position)
    }

    /** Checks that given position exists and is not occupied. */
    fun canMoveTo(position: Position): Boolean {
        return isOnMap(position) && isNotWall(position) && isNotMob(position)
    }

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

    fun getCurrentState(): State {
        return State(this)
    }

    class State {
        private val serializedState: ByteArray

        constructor(gameMap: GameMap) {
            val out = ByteArrayOutputStream()
            val gzipOut = GZIPOutputStream(out, bufferSize, true)
            mapper.writeValue(gzipOut, gameMap)
            gzipOut.flush()
            serializedState = out.toByteArray()
        }

        constructor(serialized: ByteArray) {
            serializedState = serialized
        }

        fun serialize(): ByteArray {
            return serializedState
        }

        fun restore(): Either<Throwable, GameMap> {
            return try {
                val gameMap = mapper.readValue<GameMap>(
                    GZIPInputStream(ByteArrayInputStream(serializedState), bufferSize)
                )
                Right(gameMap)
            } catch (e: Throwable) {
                Left(e)
            }
        }

        private class PositionKeySerializer : JsonSerializer<Position>() {
            override fun serialize(value: Position, gen: JsonGenerator, serializers: SerializerProvider) {
                gen.writeFieldName("${value.x} ${value.y}")
            }
        }

        private class PositionKeyDeserializer : KeyDeserializer() {
            override fun deserializeKey(key: String, ctxt: DeserializationContext): Any {
                val split = key.split(" ")
                return Position(split[0].toInt(), split[1].toInt())
            }
        }

        companion object {
            private const val bufferSize = 8096
            private val mapper: ObjectMapper = jacksonObjectMapper()
                .enable(SerializationFeature.WRITE_ENUMS_USING_INDEX)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
                .registerModule(SimpleModule().apply {
                    addKeySerializer(Position::class.java, PositionKeySerializer())
                    addKeyDeserializer(Position::class.java, PositionKeyDeserializer())
                })

            init {
                mapper.activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder().allowIfBaseType("").build(),
                    ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE
                )
            }
        }
    }
}