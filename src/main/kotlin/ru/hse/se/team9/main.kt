package ru.hse.se.team9

import ru.hse.se.team9.consoleview.ConsoleViewController
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.model.mapgeneration.creators.RandomMapCreator
import ru.hse.se.team9.model.mapgeneration.ViewFileChooser
import ru.hse.se.team9.model.random.RandomDirection
import ru.hse.se.team9.model.random.RandomMob
import ru.hse.se.team9.model.random.RandomPosition
import java.io.File

fun main(args: Array<String>) {
    File("map_example").writer().use {
        val file =
        RandomMapCreator.build(
            RandomDirection,
            RandomPosition,
            RandomMob(RandomDirection),
            36,
            36
        ).fold({ null }, { it })!!.createMap().fold({ null }, { it })!!.serialize()
        it.write(file)
    }

    val view = ConsoleViewController()
    val appLogic = AppLogic(view, RandomDirection, RandomPosition, RandomMob(RandomDirection), ViewFileChooser(view))
    view.start()
    appLogic.openMenu()
}
