package ru.hse.se.team9

import ru.hse.se.team9.network.RoguelikeServer
import java.lang.Exception
import java.util.*

fun main(args: Array<String>) {
    val port = try {
        args[0].toInt()
    } catch (e: Exception) {
        System.err.println("Usage: gradle startServer <port>")
        return
    }

    val server = RoguelikeServer(port)
    server.start()

    val scanner = Scanner(System.`in`)
    do {
        println("Write 'shutdown' to stop server.")
    } while (scanner.nextLine() != "shutdown")
    server.stop()
}