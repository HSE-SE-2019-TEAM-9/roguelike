package ru.hse.se.team9.model.mapgeneration

import ru.hse.se.team9.entities.EmptySpace
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.HeroStats
import ru.hse.se.team9.game.entities.map.Direction.*
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap
import ru.hse.se.team9.model.random.DirectionGenerator
import ru.hse.se.team9.model.random.PositionGenerator
import ru.hse.se.team9.positions.Position

class RandomMapCreator(
    private val directionGenerator: DirectionGenerator,
    private val positionGenerator: PositionGenerator,
    private val mapWidth: Int,
    private val mapHeight: Int
) : MapCreator {

    companion object {
        private const val CHUNK_SIZE = 4
        private val CHUNK_OFFSETS = mapOf(
            UP to Pair(-CHUNK_SIZE, 0),
            DOWN to Pair(CHUNK_SIZE, 0),
            LEFT to Pair(0, -CHUNK_SIZE),
            RIGHT to Pair(0, CHUNK_SIZE)
        )

        data class Chunk(val h: Int, val w: Int)
    }

    init {
        require(mapWidth >= CHUNK_SIZE)
        require(mapHeight >= CHUNK_SIZE)
        require(mapWidth % CHUNK_SIZE == 0)
        require(mapHeight % CHUNK_SIZE == 0)
    }

    override fun createMap(): GameMap {
        val map = MutableList(mapHeight) { MutableList<MapObject>(mapWidth) { Wall } }
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
            for (i in 0 until 2) {
                CHUNK_OFFSETS[nextDirection]
            }

            val intermediateChunk = moveChunk(lastChunk, nextDirection)
            val finalChunk = moveChunk(intermediateChunk, nextDirection)
            makeEmptyChunk(map, intermediateChunk)
            makeEmptyChunk(map, finalChunk)
            dfsStack.add(finalChunk)
        }

        val hero = Hero(HeroStats(0, 0, 0, 0, 0, 0)) // not used in current version
        return GameMap(
            HeroOnMap(hero, Position(0, 0)),
            map,
            mapWidth,
            mapHeight,
            positionGenerator
        )
    }


    private fun makeEmptyChunk(map: MutableList<MutableList<MapObject>>, chunk: Chunk) {
        for (i in 0 until CHUNK_SIZE) {
            for (j in 0 until CHUNK_SIZE) {
                map[chunk.h + i][chunk.w + j] = EmptySpace
            }
        }
    }

    private fun moveChunk(chunk: Chunk, direction: Direction): Chunk {
        val (offsetH, offsetW) = CHUNK_OFFSETS[direction]!!
        return Chunk(chunk.h + offsetH, chunk.w + offsetW)
    }

    private fun getDirectionsForNextMove(
        map: MutableList<MutableList<MapObject>>,
        chunk: Chunk
    ): List<Direction> = CHUNK_OFFSETS
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
}