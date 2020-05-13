package ru.hse.se.team9.model.logic.gamecycle

import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.game.entities.map.GameMap
import ru.hse.se.team9.model.generators.GameGenerator

class LocalGameCycleLogic(val map: GameMap, // visible for testing
                          private val gameGenerator: GameGenerator,
                          private val drawMapCallback: (MapView) -> Unit): GameCycleLogic {

    private val processor = GameCycleProcessor(map, gameGenerator)

    override fun makeMove(move: Move): GameStatus {
        val status = processor.makeMove(0, move)
        drawMapCallback(processor.getCurrentMap(0))
        return status
    }

    override fun getCurrentMap(): MapView = processor.getCurrentMap(0)

    override fun putOnItem(index: Int) {
        processor.putOnItem(0, index)
        drawMapCallback(processor.getCurrentMap(0))
    }

    override fun putOffItem(type: ItemType) {
        processor.putOffItem(0, type)
        drawMapCallback(processor.getCurrentMap(0))
    }
}