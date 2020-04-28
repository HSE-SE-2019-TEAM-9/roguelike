package ru.hse.se.team9.utils

import arrow.core.Either
import ru.hse.se.team9.game.entities.map.GameMap
import java.io.File
import java.io.FileNotFoundException

/** Save game map helper class
 * @property file a file to save state in
 */
class GameMapSaver(private val file: File) {
    /** Saves map current state. */
    fun save(gameMap: GameMap) = file.writeBytes(gameMap.getCurrentState().serialize())

    /** Deletes saved state. */
    fun delete() {
        file.delete()
    }

    /** Returns true if file with saved state exists, false otherwise.
     * Note that this method does not check if file content is correct.
     */
    fun isSaved(): Boolean = file.exists()

    /** Returns true if file with saved state exists, false otherwise.
     * Note that this method does not check if file content is correct.
     */
    fun restore(): Either<Throwable, GameMap> {
        return try {
            GameMap.State(file.readBytes()).restore()
        } catch (e: FileNotFoundException) {
            Either.left(e)
        }
    }
}