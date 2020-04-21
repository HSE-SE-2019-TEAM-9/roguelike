package ru.hse.se.team9.model.random.global

import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.model.random.mobs.MobGenerator
import ru.hse.se.team9.model.random.positions.PositionGenerator
import ru.hse.se.team9.positions.Position

class GlobalRandom(
    private val directionGenerator: DirectionGenerator,
    private val positionGenerator: PositionGenerator,
    private val mobGenerator: MobGenerator
) : GameGenerator {
    override fun createDirection(allowedDirections: List<Direction>): Direction {
        return directionGenerator.createDirection(allowedDirections)
    }

    override fun createDirection(): Direction {
        return directionGenerator.createDirection()
    }

    override fun createMob(): Mob {
        return mobGenerator.createMob()
    }

    override fun createPosition(width: Int, height: Int): Position {
        return positionGenerator.createPosition(width, height)
    }
}