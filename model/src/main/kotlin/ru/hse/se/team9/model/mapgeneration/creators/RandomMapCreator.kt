package ru.hse.se.team9.model.mapgeneration.creators

import arrow.core.Either
import ru.hse.se.team9.entities.EmptySpace
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.HeroStats
import ru.hse.se.team9.game.entities.map.Direction.*
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.game.entities.map.objects.MobOnMap
import ru.hse.se.team9.model.mapgeneration.*
import ru.hse.se.team9.model.random.DirectionGenerator
import ru.hse.se.team9.model.random.MobGenerator
import ru.hse.se.team9.model.random.PositionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.getRandomNotWallPosition

class RandomMapCreator private constructor(
    private val directionGenerator: DirectionGenerator,
    private val positionGenerator: PositionGenerator,
    private val mobGenerator: MobGenerator,
    private val mapWidth: Int = MAX_WIDTH,
    private val mapHeight: Int = MAX_HEIGHT,
    private val chunkSize: Int = DEFAULT_CHUNK_SIZE
) : MapCreator {
    private val chunkOffsets: Map<Direction, Pair<Int, Int>> = mapOf(
        UP to Pair(-chunkSize, 0),
        DOWN to Pair(chunkSize, 0),
        LEFT to Pair(0, -chunkSize),
        RIGHT to Pair(0, chunkSize)
    )

    override fun createMap(): Either<MapCreationError, GameMap> {
        val map = List(mapHeight) { MutableList<MapObject>(mapWidth) { Wall } }
        makeEmptyChunk(map, Chunk(0, 0))
        val dfsStack = mutableListOf(Chunk(0, 0))

        while (dfsStack.isNotEmpty()) {
            val lastChunk = dfsStack.last()
            val directions = getDirectionsForNextMove(map, lastChunk)
            if (directions.isEmpty()) {
                dfsStack.pop()
                continue
            }
            val nextDirection = directionGenerator.createDirection(directions)
            val intermediateChunk = moveChunk(lastChunk, nextDirection)
            val finalChunk = moveChunk(intermediateChunk, nextDirection)
            makeEmptyChunk(map, intermediateChunk)
            makeEmptyChunk(map, finalChunk)
            dfsStack.add(finalChunk)
        }

        val hero = Hero(HeroStats(0, 0, 0, 0, 0, 0)) // not used in current version
        val mobs = createRandomMobs(DEFAULT_MOB_AMOUNT, map)
        return Either.right(
            GameMap(
                HeroOnMap(hero, START_HERO_POSITION),
                map,
                mapWidth,
                mapHeight,
                positionGenerator,
                mobs
            )
        )
    }

    private fun createRandomMobs(mobAmount: Int, map: List<List<MapObject>>): List<MobOnMap> {
        return List(mobAmount) {
            MobOnMap(mobGenerator.createMob(), getRandomNotWallPosition(positionGenerator, map))
        }.filter { it.position != START_HERO_POSITION }
    }

    private fun makeEmptyChunk(map: List<MutableList<MapObject>>, chunk: Chunk) {
        for (i in 0 until chunkSize) {
            for (j in 0 until chunkSize) {
                map[chunk.h + i][chunk.w + j] = EmptySpace
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
            map.getOrNull(chunk.h + offsetH)?.getOrNull(chunk.w + offsetW) == Wall
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
        private const val DEFAULT_MOB_AMOUNT = 20
        private const val DEFAULT_CHUNK_SIZE = 4
        private val START_HERO_POSITION = Position(0, 0)

        internal data class Chunk(val h: Int, val w: Int)

        fun build(
            directionGenerator: DirectionGenerator,
            positionGenerator: PositionGenerator,
            mobGenerator: MobGenerator,
            mapWidth: Int = MAX_WIDTH,
            mapHeight: Int = MAX_HEIGHT,
            chunkSize: Int = DEFAULT_CHUNK_SIZE
        ): Either<MapCreationError, RandomMapCreator> {
            val n = nextDivisibleBy(chunkSize, mapWidth)
            val m = nextDivisibleBy(chunkSize, mapHeight)
            if (checkChunkSize(chunkSize, n, m)) return Either.left(ChunkTooBig)
            if (checkNegativeSize(n, m)) return Either.left(NegativeSize)
            if (checkBigMap(n, m)) return Either.left(MapTooBig)
            return Either.Right(RandomMapCreator(directionGenerator, positionGenerator, mobGenerator, n, m, chunkSize))
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
                n + m - (m % n)
            }
        }
    }
}