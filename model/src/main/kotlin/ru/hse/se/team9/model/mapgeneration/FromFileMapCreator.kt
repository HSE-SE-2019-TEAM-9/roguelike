package ru.hse.se.team9.model.mapgeneration

import arrow.core.Either
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.random.PositionGenerator
import java.lang.RuntimeException

class FromFileMapCreator(private val positionGenerator: PositionGenerator) {
    fun createMap(): Either<MapCreationError, GameMap> {
        TODO()
    }
}

sealed class MapCreationError: RuntimeException()
object FileNotChosen: MapCreationError()
object ParseError: MapCreationError()