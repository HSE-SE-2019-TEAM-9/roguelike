package ru.hse.se.team9.game.entities.map.objects

import ru.hse.se.team9.game.entities.mobs.Mob
import ru.hse.se.team9.positions.Position

data class MobOnMap(val mob: Mob, var position: Position)
