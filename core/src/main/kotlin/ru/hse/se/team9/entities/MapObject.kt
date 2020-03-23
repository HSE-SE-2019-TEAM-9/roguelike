package ru.hse.se.team9.entities

/** Class representing landscape object -- object lying on map and, mostly, not moving.
 *  Used for map representation in view */
sealed class MapObject: GameObject

/** Object representing wall -- entity, through which neither player nor anybody else can walk */
object Wall: MapObject()

/** Object representing empty space -- it can be stepped over by anyone */
object EmptySpace: MapObject()