package ru.hse.se.team9.model.mapgeneration

import arrow.core.Either
import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.random.PositionGenerator
import java.io.File
import java.io.IOException

/** Class used for loading maps from files
 * @property positionGenerator position generator which will be used in loaded map
 * @property fileChooser used for traversing file system
 */
class FromFileMapCreator(
    private val positionGenerator: PositionGenerator,
    private val fileChooser: FileChooser
) {
    /** Loads map from file. Suggests user to choose file, starting from current directory
     * @return Either<MapCreationError, GameMap> if map couldn't be parsed then returns appropriate error
     * else returns parsed map
     */
    fun createMap(): Either<MapCreationError, GameMap> {
        val file = fileChooser.chooseFile(File(".")) ?: return Either.left(FileNotChosen)
        return try {
            GameMap.deserialize(file.readText(), positionGenerator).mapLeft {
                ParseError(it)
            }
        } catch (e: IOException) {
            Either.left(ParseError(e))
        }
    }
}

sealed class MapCreationError(cause: Throwable? = null): RuntimeException(cause)
object FileNotChosen: MapCreationError()
class ParseError(cause: Throwable): MapCreationError(cause)