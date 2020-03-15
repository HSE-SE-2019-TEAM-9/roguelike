package ru.hse.se.team9.model.mapgeneration

import ru.hse.se.team9.game.entities.map.GameMap

interface MapCreator {
    fun createMap(): GameMap
}