package ru.hse.se.team9.model.logic.gamecycle

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.AbstractStub
import io.grpc.stub.StreamObserver
import ru.hse.se.team9.conversions.FromProtoConverter.toView
import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.network.*
import ru.hse.se.team9.network.PlayerMessage.JoinGame
import ru.hse.se.team9.network.PlayerMessage.MakeMove
import ru.hse.se.team9.network.PlayerMessage.StartGame
import ru.hse.se.team9.network.Service.ServerMessage
import ru.hse.se.team9.network.views.Views
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Connects to remote GameCycleProcessor (server) via GRPC.
 * Calls drawMapCallback when new map is available in getCurrentMap method
 */
class RemoteGameCycleLogic(
    private val drawMapCallback: () -> Unit
) : GameCycleLogic, AutoCloseable {

    @Volatile private lateinit var currentMap: MapView
    @Volatile private var gameOver = false
    private var started: Boolean = false
    private val mapReceivedLatch = CountDownLatch(1)
    private lateinit var serverObserver: StreamObserver<Service.PlayerMessage>
    private lateinit var channel: ManagedChannel

    private fun start(address: String, port: Int) {
        channel = ManagedChannelBuilder.forAddress(address, port).usePlaintext().build()
        val asyncStub = RoguelikeApiGrpc.newStub(channel)
        serverObserver = asyncStub.join(object : StreamObserver<ServerMessage> {
            override fun onNext(value: ServerMessage) {
                currentMap = value.mapUpdate.map.toView()
                if (currentMap.hero.hp <= 0) {
                    gameOver = true
                }
                drawMapCallback()
                mapReceivedLatch.countDown()
            }

            override fun onError(t: Throwable) {
                // (((((((
            }

            override fun onCompleted() {
                // (((((((
            }
        })
    }

    override fun makeMove(move: Move): GameStatus {
        if (gameOver) {
            return Loss
        }
        serverObserver.onNext(PlayerMessage {
            makeMove = MakeMove {
                setMove(
                    when (move) {
                        Left -> Service.PlayerMessage.MakeMove.Move.LEFT
                        Up -> Service.PlayerMessage.MakeMove.Move.UP
                        Right -> Service.PlayerMessage.MakeMove.Move.RIGHT
                        Down -> Service.PlayerMessage.MakeMove.Move.DOWN
                    }
                )
            }
        })
        return InProgress
    }

    override fun getCurrentMap(): MapView {
        return currentMap
    }

    override fun putOnItem(index: Int) {
        serverObserver.onNext(PlayerMessage {
            putOnItem {
                setIndex(index)
            }
        })
    }

    override fun putOffItem(type: ItemType) {
        serverObserver.onNext(PlayerMessage {
            putOffItem {
                setType(
                    when (type) {
                        ItemType.BOOTS -> Views.ItemView.ItemType.BOOTS
                        ItemType.WEAPON -> Views.ItemView.ItemType.WEAPON
                        ItemType.UNDERWEAR -> Views.ItemView.ItemType.UNDERWEAR
                        ItemType.NONE -> Views.ItemView.ItemType.NONE
                    }
                )
            }
        })
    }

    /**
     * Tries to create a new game at the remote server which has to be available at the specified address.
     * Blocks until initial map is received and then returns initialized RemoteGameCycleLogic.
     */
    @Synchronized
    fun createNewGame(
        address: String,
        port: Int
    ) {
        if (started) error("RemoteGameCycleView already started")
        started = true
        start(address, port)

        serverObserver.onNext(PlayerMessage {
            startGame = StartGame {
            }
        })
        mapReceivedLatch.await()
    }

    /**
     * Tries to join an existing game at the remote server which has to be available at the specified address.
     * Blocks until initial map is received and then returns initialized RemoteGameCycleLogic.
     */
    @Synchronized
    fun joinGame(
        address: String,
        port: Int,
        gameId: Int
    ) {
        if (started) error("RemoteGameCycleView already started")
        started = true
        start(address, port)

        serverObserver.onNext(PlayerMessage {
            joinGame = JoinGame {
                setGameId(gameId)
            }
        })
        mapReceivedLatch.await()
    }

    override fun close() {
        channel.shutdownNow()
        channel.awaitTermination(5, TimeUnit.SECONDS)
    }
}
