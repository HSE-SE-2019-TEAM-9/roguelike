package ru.hse.se.team9.model.mapgeneration

import arrow.core.Either
import ru.hse.se.team9.game.entities.map.GameMap

interface MapCreator {
    fun createMap(): Either<MapCreationError, GameMap>
}