package ru.hse.se.team9.model.random

import ru.hse.se.team9.model.random.confusion.StrategyModifierGenerator
import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.model.random.mobs.MobGenerator
import ru.hse.se.team9.model.random.positions.PositionGenerator

interface GameGenerator : DirectionGenerator, MobGenerator, PositionGenerator, StrategyModifierGenerator