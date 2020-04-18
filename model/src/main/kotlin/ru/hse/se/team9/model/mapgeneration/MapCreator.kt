package ru.hse.se.team9.model.mapgeneration

import arrow.core.Either
import ru.hse.se.team9.game.entities.map.GameMap

/** Base class for all map creators. */
interface MapCreator {
    /**
     * Creates map.
     * @return Left<GameMap> if some error occurred in creation algorithm or
     * Right<GameMap> if map was created successfully.
     */
    fun createMap(): Either<MapCreationError, GameMap>
}