package ru.hse.se.team9.model.random.global

import ru.hse.se.team9.model.random.directions.DirectionGenerator
import ru.hse.se.team9.model.random.mobs.MobGenerator
import ru.hse.se.team9.model.random.positions.PositionGenerator

interface GameGenerator : DirectionGenerator, MobGenerator, PositionGenerator