package ru.hse.se.team9.model.random

import ru.hse.se.team9.game.entities.hero.inventory.items.Boots
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon
import ru.hse.se.team9.game.entities.map.Direction
import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.game.entities.mobs.strategies.MobStrategy
import ru.hse.se.team9.model.random.confusion.StrategyModifierGenerator
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.model.random.items.ItemGenerator
import ru.hse.se.team9.model.random.mobs.MobGenerator
import ru.hse.se.team9.model.random.positions.PositionGenerator
import ru.hse.se.team9.positions.Position

/** An implementation of all generators which redirects all calls to underlying "small" generators.  */
class GameGenerator(
    private val directionGenerator: DirectionGenerator,
    private val positionGenerator: PositionGenerator,
    private val mobGenerator: MobGenerator,
    private val strategyModifierGenerator: StrategyModifierGenerator,
    private val itemGenerator: ItemGenerator
) : DirectionGenerator, MobGenerator, PositionGenerator, StrategyModifierGenerator, ItemGenerator {
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

    override fun createModifier(): (MobStrategy) -> MobStrategy {
        return strategyModifierGenerator.createModifier()
    }

    override fun createBoots(): Boots {
        return itemGenerator.createBoots()
    }

    override fun createUnderwear(): Underwear {
        return itemGenerator.createUnderwear()
    }

    override fun createWeapon(): Weapon {
        return itemGenerator.createWeapon()
    }
}