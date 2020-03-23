package ru.hse.se.team9

import ru.hse.se.team9.consoleview.ConsoleViewController
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.model.mapgeneration.creators.RandomMapCreator
import ru.hse.se.team9.model.mapgeneration.ViewFileChooser
import ru.hse.se.team9.model.random.directions.RandomDirection
import ru.hse.se.team9.model.random.global.GlobalRandom
import ru.hse.se.team9.model.random.mobs.RandomMob
import ru.hse.se.team9.model.random.positions.RandomPosition
import java.io.File

fun main(args: Array<String>) {
    val generator = GlobalRandom(RandomDirection, RandomPosition, RandomMob(RandomDirection))

    File("map_example").writer().use {
        val file =
        RandomMapCreator.build(
            generator,
            36,
            36
        ).fold({ null }, { it })!!.createMap().fold({ null }, { it })!!.serialize()
        it.write(file)
    }

    val view = ConsoleViewController()
    val appLogic = AppLogic(view, generator, ViewFileChooser(view))
    view.start()
    appLogic.openMenu()
}
