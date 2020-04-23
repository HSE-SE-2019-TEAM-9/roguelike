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
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.model.random.positions.PositionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.getRandomNotWallPosition
import ru.hse.se.team9.utils.plus

/** Represents game map
 * @property heroOnMap hero and its position on map
 * @property map two-dimensional array of MapObject, stores landscape elements and not active participants of game
 * @property width width of map
 * @property height height of map
 * @property positionGenerator object using for generating random positions on map
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

    /**
     * Removes mob from map. If no mob is located at given position does nothing
     */
    fun removeMob(position: Position) {
        mobs.remove(position)
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

    private fun isNotMob(position: Position): Boolean {
        return !mobs.containsKey(position)
    }

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
        private val serializedState: String

        constructor(gameMap: GameMap) {
            serializedState = mapper.writeValueAsString(gameMap)
        }

        constructor(serialized: String) {
            serializedState = serialized
        }

        fun serialize(): String {
            return serializedState
        }

        fun restore(): Either<Throwable, GameMap> {
            return try {
                val gameMap = mapper.readValue<GameMap>(serializedState)
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
            val mapper: ObjectMapper = jacksonObjectMapper()
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