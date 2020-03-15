package ru.hse.se.team9.entities

sealed class MapObject: GameObject

object Wall: MapObject()

object EmptySpace: MapObject()