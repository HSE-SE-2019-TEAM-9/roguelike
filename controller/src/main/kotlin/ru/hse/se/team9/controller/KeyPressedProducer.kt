package ru.hse.se.team9.controller

import ru.hse.se.team9.event.KeyPressed
import ru.hse.se.team9.model.logic.gamecycle.*
import ru.hse.se.team9.view.KeyPressedType
import ru.hse.se.team9.view.View

class KeyPressedProducer(private val view: View, private val controller: GameController) {
    fun start() {
        view.setKeyPressedHandler {
            val action = when (it) {
                KeyPressedType.UP -> Up
                KeyPressedType.DOWN -> Down
                KeyPressedType.LEFT -> Left
                KeyPressedType.RIGHT -> Right
            }
            sendEvent(KeyPressed(action))
        }
    }

    private fun sendEvent(event: KeyPressed) {
        controller.addEvent(event)
    }
}