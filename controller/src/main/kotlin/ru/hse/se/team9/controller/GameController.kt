package ru.hse.se.team9.controller

import ru.hse.se.team9.event.Event
import ru.hse.se.team9.event.KeyPressed
import ru.hse.se.team9.model.logic.gamecycle.Move
import ru.hse.se.team9.model.logic.general.AppLogic
import ru.hse.se.team9.view.View

class GameController(view: View, private val appLogic: AppLogic) {
    private val keyPressedProducer = KeyPressedProducer(view, this)

    fun start() {
        keyPressedProducer.start()
    }

    private fun sendEvent(event: Event) {
        when (event) {
            is KeyPressed -> if (event.action is Move) {
                appLogic.movePlayer(event.action)
            }
        }
    }

    internal fun addEvent(event: Event) {
        sendEvent(event)
    }
}