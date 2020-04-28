package ru.hse.se.team9.model.mapgeneration.creators

import arrow.core.Either
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.HeroStats
import ru.hse.se.team9.game.entities.hero.consumables.Consumable
import ru.hse.se.team9.game.entities.hero.inventory.Equipment
import ru.hse.se.team9.game.entities.hero.inventory.items.Boots
import ru.hse.se.team9.game.entities.hero.inventory.items.Item
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.Direction.*
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.distance.Distance
import ru.hse.se.team9.game.entities.map.distance.Manhattan
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.model.mapgeneration.*
import ru.hse.se.team9.model.random.GameGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.getRandomNotWallPosition

/**
 * This class is used for creating pseudo-random or predetermined maps.
 * The class itself does not perform any random actions: the only random sources are
 * directionGenerator and positionGenerator.
 */
class RandomMapCreator private constructor(
    private val generator: GameGenerator,
    private val mapWidth: Int = MAX_WIDTH,
    private val mapHeight: Int = MAX_HEIGHT,
    private val chunkSize: Int = DEFAULT_CHUNK_SIZE,
    private val distance: Distance = Manhattan,
    private val fogRadius: Int
) : MapCreator {
    private val borderSize = BORDER_CHUNK_SIZE * chunkSize
    private val mapHeightAdjusted = mapHeight + BORDER_CHUNK_SIZE * chunkSize
    private val mapWidthAdjusted = mapWidth + BORDER_CHUNK_SIZE * chunkSize

    private val chunkOffsets: Map<Direction, Pair<Int, Int>> = mapOf(
        UP to Pair(-chunkSize, 0),
        DOWN to Pair(chunkSize, 0),
        LEFT to Pair(0, -chunkSize),
        RIGHT to Pair(0, chunkSize)
    )

    /** Creates a labyrinth-like map. */
    override fun createMap(): Either<MapCreationError, GameMap> {
        val borderSize = BORDER_CHUNK_SIZE * chunkSize
        val mapHeightAdjusted = mapHeight + BORDER_CHUNK_SIZE * chunkSize
        val mapWidthAdjusted = mapWidth + BORDER_CHUNK_SIZE * chunkSize
        val heroPosition = Position(borderSize, borderSize)

        val map = List(mapHeightAdjusted) { MutableList(mapWidthAdjusted) { MapObject.WALL } }

        makeEmptyChunk(map, Chunk(borderSize, borderSize))
        val dfsStack = mutableListOf(Chunk(borderSize, borderSize))

        while (dfsStack.isNotEmpty()) {
            val lastChunk = dfsStack.last()
            val directions = getDirectionsForNextMove(map, lastChunk)
            if (directions.isEmpty()) {
                dfsStack.pop()
                continue
            }
            val nextDirection = generator.createDirection(directions)
            val intermediateChunk = moveChunk(lastChunk, nextDirection)
            val finalChunk = moveChunk(intermediateChunk, nextDirection)
            makeEmptyChunk(map, intermediateChunk)
            makeEmptyChunk(map, finalChunk)
            dfsStack.add(finalChunk)
        }

        val hero = Hero(stats = createDefaultStats(), equipment = createDefaultEquipment())
        val gameMap = GameMap(
            HeroOnMap(hero, heroPosition),
            map,
            mapWidthAdjusted,
            mapHeightAdjusted,
            generator,
            mutableMapOf(),
            distance,
            fogRadius,
            mutableMapOf(),
            mutableMapOf()
        )
        gameMap.generateObjects()

        return Either.right(gameMap)
    }

    private fun makeEmptyChunk(map: List<MutableList<MapObject>>, chunk: Chunk) {
        for (i in 0 until chunkSize) {
            for (j in 0 until chunkSize) {
                map[chunk.h + i][chunk.w + j] = MapObject.EMPTY_SPACE
            }
        }
    }

    private fun moveChunk(chunk: Chunk, direction: Direction): Chunk {
        val (offsetH, offsetW) = chunkOffsets[direction]!!
        return Chunk(
            chunk.h + offsetH,
            chunk.w + offsetW
        )
    }

    private fun getDirectionsForNextMove(
        map: List<MutableList<MapObject>>,
        chunk: Chunk
    ): List<Direction> = chunkOffsets
        .mapValues {
            Pair(it.value.first * 2, it.value.second * 2)
        }
        .filter { (_, offset) ->
            val (offsetH, offsetW) = offset
            val newH = chunk.h + offsetH
            val newW = chunk.w + offsetW
            val isWall = map.getOrNull(newH)?.getOrNull(newW) == MapObject.WALL
            val hNotBorder = newH >= borderSize && newH < (mapHeightAdjusted - borderSize)
            val wNotBorder = newW >= borderSize && newW < (mapWidthAdjusted - borderSize)
            isWall && hNotBorder && wNotBorder
        }
        .keys
        .toList()


    private fun <T> MutableList<T>.pop() {
        this.removeAt(this.lastIndex)
    }

    companion object {
        private const val MAX_WIDTH = 10_000
        private const val MAX_HEIGHT = 10_000
        private const val MAX_MAP_SIZE = 1_000_000
        private const val DEFAULT_CHUNK_SIZE = 6
        private const val BORDER_CHUNK_SIZE = 2

        internal data class Chunk(val h: Int, val w: Int)

        private fun createDefaultStats(): HeroStats = HeroStats(30, 30, 2, 10, 0, 1)

        private fun createDefaultEquipment(): Equipment {
            val boots = Boots(1, 1, 0, "Dirty old boots")
            val underwear = Underwear(1, 1, 0, "Wet underpants")
            val weapon = Weapon(0,0, 1, "Broken stick")
            return Equipment(boots, underwear, weapon)
        }
        /**
         * Checks provided arguments and returns Right<RandomMapCreator> if all arguments are valid.
         *
         * @param generator generators for game entities on map
         * @param mapWidth should not be more than 10000 or less than chunkSize.
         * Will be rounded up to the closest integer divisible by chunkSize.
         * Does not include border.
         * @param mapHeight should not be more than 10000 or less than chunkSize
         * Will be rounded up to the closest integer divisible by chunkSize.
         * Does not include border.
         * @param chunkSize a minimal square filled with objects of one type.
         * @param distance a distance metric which is used in fog computations and in mob strategies
         * @param fogRadius how far player sees
         */
        fun build(
            generator: GameGenerator,
            mapWidth: Int = MAX_WIDTH,
            mapHeight: Int = MAX_HEIGHT,
            chunkSize: Int = DEFAULT_CHUNK_SIZE,
            distance: Distance,
            fogRadius: Int
        ): Either<MapCreationError, RandomMapCreator> {
            val n = nextDivisibleBy(chunkSize, mapWidth)
            val m = nextDivisibleBy(chunkSize, mapHeight)
            if (checkChunkSize(chunkSize, n, m)) return Either.left(ChunkTooBig)
            if (checkNegativeSize(n, m)) return Either.left(NegativeSize)
            if (checkBigMap(n, m)) return Either.left(MapTooBig)
            return Either.Right(RandomMapCreator(generator, n, m, chunkSize, distance, fogRadius))
        }

        private fun checkNegativeSize(mapWidth: Int, mapHeight: Int): Boolean =
            mapWidth <= 0 || mapHeight <= 0

        private fun checkChunkSize(chunkSize: Int, mapWidth: Int, mapHeight: Int): Boolean =
            mapWidth < chunkSize || mapHeight < chunkSize

        private fun checkBigMap(mapWidth: Int, mapHeight: Int): Boolean =
            mapWidth > MAX_WIDTH || mapHeight > MAX_HEIGHT || mapWidth * mapHeight > MAX_MAP_SIZE

        private fun nextDivisibleBy(n: Int, m: Int): Int {
            return if (m % n == 0) {
                m
            } else {
                m + n - (m % n)
            }
        }
    }
}
