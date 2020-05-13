package ru.hse.se.team9.game.entities.map

import arrow.core.Either
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.consumables.Consumable
import ru.hse.se.team9.game.entities.hero.inventory.items.Item
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.model.generators.GameGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.getNotWallPosition
import ru.hse.se.team9.utils.plus
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.math.max

/**
 * Represents game map
 * @property heroes map from heroId to hero and its position
 * @property map two-dimensional array of MapObject, stores landscape elements and not active participants of game
 * @property width width of map
 * @property height height of map
 * @property generator object using for generating random objects on map
 * @property mobs a map from mob position to mob
 * @property distance a metric used for map-related processes
 * @property fogRadius how far hero sees
 * @property items wearable items initially located on the map
 * @property consumables consumable items initially located on the map
 */
class GameMap(
    val heroes: MutableMap<Int, HeroOnMap>,
    val map: List<MutableList<MapObject>>,
    val width: Int,
    val height: Int,
    private val generator: GameGenerator,
    var mobs: MutableMap<Position, Mob>,
    val distance: Distance,
    private val fogRadius: Int,
    val items: MutableMap<Position, Item>,
    val consumables: MutableMap<Position, Consumable>
) {
    val fog: MutableMap<Int, FogOfWar> = mutableMapOf()

    /** Adds new hero to some empty position on a map
     *
     * @param heroId id of a new hero
     * @param hero hero to add on a map with its parameters
     */
    fun addHeroToRandomPosition(heroId: Int, hero: Hero) {
        val position = generatePositionForHero() ?: throw IllegalStateException("no valid field found on map")
        heroes[heroId] = HeroOnMap(hero, position)
        val fogOfWar = FogOfWar(distance, map, width, height, fogRadius)
        fogOfWar.updateVision(position)
        fog[heroId] = fogOfWar
    }

    /** Moves hero to the neighbor cell according to the direction. If this position is occupied, does nothing.
     * @param direction direction to move towards
     */
    fun moveHero(heroId: Int, direction: Direction) {
        val hero = heroes[heroId] ?: throw IllegalArgumentException("no such hero exists")
        val position = hero.position + direction
        if (heroCanMoveTo(position)) {
            hero.position = position
            fog[heroId]?.updateVision(position) ?: throw IllegalArgumentException("no such hero exists")
        }
    }

    /** Moves mob to the given position.
     * If this position is occupied or no mob is located at previous position, does nothing
     * @param previousPosition position of moving mob
     * @param newPosition position to move hero to
     */
    fun moveMob(previousPosition: Position, newPosition: Position) {
        if (mobCanMoveTo(newPosition)) {
            mobs.remove(previousPosition)?.let {
                mobs.put(newPosition, it)
            }
        }
    }

    /** Removes mob from map. If no mob is located at given position does nothing */
    fun removeMob(position: Position) {
        mobs.remove(position)
    }

    /** Checks that given position is an empty cell OR a hero (so that mobs can attack hero) */
    fun mobCanMoveTo(position: Position): Boolean = isEmptyCell(position) || heroes.values.any {
        it.position == position
    }

    /** Checks that given position is not a wall and located on the map*/
    fun heroCanMoveTo(position: Position): Boolean = isOnMap(position) && isNotWall(position)

    /** Generates new mobs, items and consumables according to inner map logic */
    fun generateObjects() {
        generateMobs()
        generateConsumables()
        generateItems()
    }

    private fun isEmptyCell(position: Position): Boolean = isOnMap(position)
            && isNotWall(position)
            && isNotMob(position)
            && isNotConsumable(position)
            && isNotHero(position)
            && isNotItem(position)

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

    private fun isNotConsumable(position: Position): Boolean {
        return !consumables.containsKey(position)
    }

    private fun isNotItem(position: Position): Boolean {
        return !items.containsKey(position)
    }

    private fun isNotHero(position: Position): Boolean {
        return heroes.values.none { it.position == position }
    }

    private fun generatePositionForHero(): Position? = getValidPositionSequence().firstOrNull()

    private fun generateMobs() = getValidPositionSequence()
        .take(max(0, MOB_AMOUNT - mobs.size))
        .map { Pair(it, generator.createMob()) }
        .forEach { mobs[it.first] = it.second }

    private fun generateItems() = getValidPositionSequence()
        .take(max(0, ITEM_AMOUNT - items.size))
        .map { Pair(it, generator.createItem()) }
        .forEach { items[it.first] = it.second }

    private fun generateConsumables() = getValidPositionSequence()
        .take(max(0, CONSUMABLE_AMOUNT - consumables.size))
        .map { Pair(it, generator.createConsumable()) }
        .forEach { consumables[it.first] = it.second }

    private fun getValidPositionSequence() = generateSequence { getNotWallPosition(generator, map) }
        .take(GENERATION_MAX_RETRIES)
        .distinct()
        .filter { isEmptyCell(it) }

    /** Returns GameMap snapshot */
    fun getCurrentState(): State {
        return State(this)
    }

    companion object {
        private const val MOB_AMOUNT = 20
        private const val ITEM_AMOUNT = 10
        private const val CONSUMABLE_AMOUNT = 10
        private const val GENERATION_MAX_RETRIES = 300
    }

    /**
     * Holds GameMap snapshot.
     * Can be used to save and restore game state.
     */
    class State {
        private val serializedState: ByteArray

        /** Creates representation of specified GameMap current state */
        constructor(gameMap: GameMap) {
            val out = ByteArrayOutputStream()
            val gzipOut = GZIPOutputStream(out, bufferSize, true)
            mapper.writeValue(gzipOut, gameMap)
            gzipOut.flush()
            serializedState = out.toByteArray()
        }

        /** Creates State from its serialization.
         * Note that this constructor does not throw anything if its argument is not a correct serialization.
         */
        constructor(serialized: ByteArray) {
            serializedState = serialized
        }

        /** Serializes map snapshot to ByteArray */
        fun serialize(): ByteArray {
            return serializedState
        }

        /**
         * Restores GameMap from snapshot.
         * @return Left<Throwable> if some error occurred and Right<GameMap> if restored successfully.
         */
        fun restore(): Either<Throwable, GameMap> {
            return try {
                val gameMap = mapper.readValue<GameMap>(
                    GZIPInputStream(ByteArrayInputStream(serializedState), bufferSize)
                )
                Either.right(gameMap)
            } catch (e: Throwable) {
                Either.left(e)
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

        private companion object {
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