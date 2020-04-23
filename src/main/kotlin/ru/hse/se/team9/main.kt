package ru.hse.se.team9

import ru.hse.se.team9.consoleview.ConsoleViewController
import ru.hse.se.team9.game.entities.map.distance.Manhattan
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.model.mapgeneration.creators.RandomMapCreator
import ru.hse.se.team9.model.mapgeneration.ViewFileChooser
import ru.hse.se.team9.model.random.directions.RandomDirection
import ru.hse.se.team9.model.random.GlobalRandom
import ru.hse.se.team9.model.random.confusion.RandomStrategyModifier
import ru.hse.se.team9.model.random.mobs.RandomMob
import ru.hse.se.team9.model.random.positions.RandomPosition
import java.io.File

/**
 * Injects dependencies and starts components of the app.
 */
fun main(args: Array<String>) {
    val generator = GlobalRandom(
        RandomDirection,
        RandomPosition,
        RandomMob(RandomDirection),
        RandomStrategyModifier(RandomDirection)
    )

    if (args.contains("create-map")) {
        File("map_example").writer().use {
            val file =
                RandomMapCreator.build(
                    generator,
                    36,
                    36,
                    4,
                    Manhattan,
                    6
                ).fold({ null }, { it })!!.createMap().fold({ null }, { it })!!.getCurrentState().serialize()
            it.write(file)
        }
        return
    }

    val view = ConsoleViewController()
    val appLogic = AppLogic(view, generator, ViewFileChooser(view), Manhattan)
    view.start()
    appLogic.openMenu()
}
