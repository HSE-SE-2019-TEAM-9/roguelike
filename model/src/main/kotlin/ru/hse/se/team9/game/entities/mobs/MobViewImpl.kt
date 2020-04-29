package ru.hse.se.team9.game.entities.mobs

import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.entities.views.MobView

/** Adapts Mob to MobView interface */
class MobViewImpl(mob: Mob) : MobView {
    override val hp: Int = mob.hp
    override val maxHp: Int = mob.maxHp
    override val properties: List<MobProperty> = mob.getProperties()
}
