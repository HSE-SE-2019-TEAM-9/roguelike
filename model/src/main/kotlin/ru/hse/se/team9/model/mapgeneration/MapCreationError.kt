package ru.hse.se.team9.model.mapgeneration

sealed class MapCreationError(cause: Throwable? = null): RuntimeException(cause)

object FileNotChosen: MapCreationError()

class ParseError(cause: Throwable): MapCreationError(cause)

sealed class BadSizeError(cause: Throwable? = null): MapCreationError(cause)
object MapTooBig: BadSizeError()
object ChunkTooBig: BadSizeError()
object NegativeSize: BadSizeError()