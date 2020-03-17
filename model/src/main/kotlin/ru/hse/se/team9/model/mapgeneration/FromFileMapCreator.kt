package ru.hse.se.team9.model.mapgeneration

import arrow.core.Either
import ru.hse.se.team9.files.FileChooser
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.random.PositionGenerator
import java.io.File
import java.io.IOException

class FromFileMapCreator(private val positionGenerator: PositionGenerator) {
    fun createMap(fileChooser: FileChooser): Either<MapCreationError, GameMap> {
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