package ru.hse.se.team9.game.entities.hero

import ru.hse.se.team9.entities.views.HeroView
import ru.hse.se.team9.game.entities.hero.inventory.items.ItemViewImpl
import ru.hse.se.team9.game.entities.map.objects.HeroOnMap

class HeroViewImpl(hero: HeroOnMap) : HeroView {
        override val position = hero.position
        override val hp = hero.hero.stats.hp
        override val maxHp = hero.hero.stats.maxHp
        override val armor = hero.hero.stats.armor
        override val damage = hero.hero.stats.damage
        override val inventory = hero.hero.inventory.map { ItemViewImpl(it) }
        override val equipment = hero.hero.equipment.getItems().mapValues { ItemViewImpl(it.value) }
}