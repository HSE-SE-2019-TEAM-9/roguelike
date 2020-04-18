package ru.hse.se.team9.model.mapgeneration

/** Base class for all errors returned by MapCreators. */
sealed class MapCreationError(cause: Throwable? = null) : RuntimeException(cause)

/** A file with map was not chosen by player. */
object FileNotChosen : MapCreationError()

/** a file cannot be parsed. */
class ParseError(cause: Throwable) : MapCreationError(cause)

/** Some map dimension are not valid. */
sealed class BadSizeError(cause: Throwable? = null) : MapCreationError(cause)

object MapTooBig : BadSizeError()
object ChunkTooBig : BadSizeError()
object NegativeSize : BadSizeError()