package ru.hse.se.team9.gameobjects

sealed class MapObject: GameObject

object Wall: MapObject()

object EmptySpace: MapObject()