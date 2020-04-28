package ru.hse.se.team9.model.mapgeneration.creators

import arrow.core.Either
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.mapgeneration.MapCreationError
import ru.hse.se.team9.model.mapgeneration.MapCreator
import ru.hse.se.team9.model.mapgeneration.RestoreError
import ru.hse.se.team9.utils.GameMapSaver

/**
 * Class used for loading saved maps
 * @property saver game map saver used to restore map state
 */
class RestoreSavedMapCreator(private val saver: GameMapSaver) : MapCreator {
    /**
     * Loads saved map
     * @return Either<MapCreationError, GameMap> if map couldn't be parsed then returns appropriate error
     * else returns parsed map
     */
    override fun createMap(): Either<MapCreationError, GameMap> {
        return saver.restore().mapLeft { RestoreError(it) }
    }

    companion object {
        /** Constructs new RestoreSavedMapCreator from GameMapSaver */
        fun build(saver: GameMapSaver): Either<MapCreationError, RestoreSavedMapCreator> {
            return Either.right(RestoreSavedMapCreator(saver))
        }
    }
}