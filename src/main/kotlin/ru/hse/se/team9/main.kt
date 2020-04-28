package ru.hse.se.team9

import ru.hse.se.team9.consoleview.ConsoleViewController
import ru.hse.se.team9.game.entities.map.distance.Manhattan
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.model.mapgeneration.ViewFileChooser
import ru.hse.se.team9.model.mapgeneration.creators.RandomMapCreator
import ru.hse.se.team9.model.random.GameGenerator
import ru.hse.se.team9.model.random.confusion.RandomStrategyModifier
import ru.hse.se.team9.model.random.consumables.RandomConsumable
import ru.hse.se.team9.model.random.directions.RandomDirection
import ru.hse.se.team9.model.random.items.RandomItem
import ru.hse.se.team9.model.random.mobs.RandomMob
import ru.hse.se.team9.model.random.positions.RandomPosition
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
        RandomConsumable
    )
    val saver = GameMapSaver(File(".saved"))

    if (args.contains("create-map")) {
        val file = RandomMapCreator.build(
            generator,
            36,
            36,
            4,
            Manhattan,
            6
        ).fold({ null }, { it })!!.createMap().fold({ null }, { it })!!.getCurrentState().serialize()
        File("map_example").writeBytes(file)
        return
    }

    val view = ConsoleViewController()
    val appLogic = AppLogic(view, generator, saver, ViewFileChooser(view), Manhattan)
    view.start()
    appLogic.openMenu()
}
