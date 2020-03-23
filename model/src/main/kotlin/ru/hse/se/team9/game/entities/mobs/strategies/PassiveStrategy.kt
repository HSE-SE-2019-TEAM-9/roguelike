package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.objects.MobOnMap
import ru.hse.se.team9.positions.Position

object PassiveStrategy: MobStrategy {
    override fun makeMove(mobOnMap: MobOnMap, map: GameMap): Position = mobOnMap.position
}