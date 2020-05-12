package ru.hse.se.team9.model.generators.heroes

import ru.hse.se.team9.game.entities.hero.Hero
import ru.hse.se.team9.game.entities.hero.HeroStats
import ru.hse.se.team9.game.entities.hero.inventory.Equipment
import ru.hse.se.team9.game.entities.hero.inventory.items.Boots
import ru.hse.se.team9.game.entities.hero.inventory.items.Underwear
import ru.hse.se.team9.game.entities.hero.inventory.items.Weapon

object DefaultHeroCreator: HeroGenerator {
    override fun createHero() = Hero(stats = createDefaultStats(), equipment = createDefaultEquipment())

    private fun createDefaultStats(): HeroStats = HeroStats(30, 30, 2, 10, 0, 1)

    private fun createDefaultEquipment(): Equipment {
        val boots = Boots(1, 1, 0, "Dirty old boots")
        val underwear = Underwear(1, 1, 0, "Wet underpants")
        val weapon = Weapon(0,0, 1, "Broken stick")
        return Equipment(boots, underwear, weapon)
    }
}