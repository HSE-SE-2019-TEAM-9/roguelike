package ru.hse.se.team9

import ru.hse.se.team9.consoleview.ConsoleViewController
import ru.hse.se.team9.game.entities.map.distance.Manhattan
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.model.mapgeneration.ViewFileChooser
import ru.hse.se.team9.model.generators.heroes.DefaultHeroCreator
import ru.hse.se.team9.model.mapgeneration.creators.RandomMapCreator
import ru.hse.se.team9.model.generators.GameGenerator
import ru.hse.se.team9.model.generators.confusion.RandomStrategyModifier
import ru.hse.se.team9.model.generators.consumables.RandomConsumable
import ru.hse.se.team9.model.generators.directions.RandomDirection
import ru.hse.se.team9.model.generators.items.RandomItem
import ru.hse.se.team9.model.generators.mobs.RandomMob
import ru.hse.se.team9.model.generators.positions.RandomPosition
import ru.hse.se.team9.utils.GameMapSaver
import java.io.File

/**
 * Injects dependencies and starts components of the app.
 */
fun main(args: Array<String>) {
    val generator = GameGenerator(
        RandomDirection,
        RandomPosition,
        RandomMob(RandomDirection),
        RandomStrategyModifier(RandomDirection),
        RandomItem,
        RandomConsumable,
        DefaultHeroCreator
    )
    val saver = GameMapSaver(File(".saved"))

    if (args.contains("create-map")) {
            val map = RandomMapCreator.build(
                    generator,
                    36,
                    36,
                    4,
                    Manhattan,
                    6
            ).fold({ null }, { it })?.createMap()?.fold({ null }, { it })
            map?.addHeroToRandomPosition(0, generator.createHero())
            val bytes = map?.getCurrentState()?.serialize()
            if (bytes != null) {
                File("map_example").writeBytes(bytes)
            } else {
                System.err.println("error while creating default map")
            }
            return
    }

    val view = ConsoleViewController()
    val appLogic = AppLogic(view, generator, saver, ViewFileChooser(view), Manhattan)
    view.start()
    appLogic.openMenu()
}
