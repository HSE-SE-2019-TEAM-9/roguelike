package ru.hse.se.team9.game.entities.mobs.strategies

import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.game.entities.map.objects.MobOnMap
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.utils.plus

class RandomStrategy(private val directionGenerator: DirectionGenerator): MobStrategy {
    override fun makeMove(mobOnMap: MobOnMap, map: GameMap): Position {
        val direction = directionGenerator.createDirection()
        return mobOnMap.position + direction
    }
}
