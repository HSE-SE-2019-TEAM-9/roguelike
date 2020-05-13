package ru.hse.se.team9

import ru.hse.se.team9.network.RoguelikeServer

fun main(args: Array<String>) {
    val port = 3030
    RoguelikeServer.run(port)
}