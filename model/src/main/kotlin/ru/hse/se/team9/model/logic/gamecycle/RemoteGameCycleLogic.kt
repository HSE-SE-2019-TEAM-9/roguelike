package ru.hse.se.team9.model.logic.gamecycle

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import ru.hse.se.team9.entities.ItemType
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.network.*
import ru.hse.se.team9.network.PlayerMessage.JoinGame
import ru.hse.se.team9.network.PlayerMessage.MakeMove
import ru.hse.se.team9.network.PlayerMessage.StartGame
import ru.hse.se.team9.network.Service.ServerMessage
import ru.hse.se.team9.network.views.Views
import java.util.concurrent.CountDownLatch
import ru.hse.se.team9.conversions.FromProtoConverter.toView

/**
 * Connects to remote GameCycleProcessor (server) via GRPC.
 * Calls drawMapCallback when new map is available in getCurrentMap method
 */
class RemoteGameCycleLogic private constructor(
    address: String,
    port: Int,
    private val drawMapCallback: () -> Unit
) : GameCycleLogic {

    @Volatile
    private lateinit var currentMap: MapView

    private val mapReceivedLatch = CountDownLatch(1)

    private val asyncStub = RoguelikeApiGrpc.newStub(
        ManagedChannelBuilder.forAddress(address, port).usePlaintext().build()
    )

    private val serverObserver: StreamObserver<Service.PlayerMessage> =
        asyncStub.join(object : StreamObserver<ServerMessage> {
            override fun onNext(value: ServerMessage) {
                if (value.hasMapUpdate()) {
                    currentMap = value.mapUpdate.map.toView()
                    drawMapCallback()
                    mapReceivedLatch.countDown()
                }
            }

            override fun onError(t: Throwable) {
                // (((((((
            }

            override fun onCompleted() {
                // (((((((
            }
        })

    override fun makeMove(move: Move): GameStatus {
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

    companion object RemoteGameCycleLogic {
        /**
         * Tries to create a new game at the remote server which has to be available at the specified address.
         * Blocks until initial map is received and then returns initialized RemoteGameCycleLogic.
         */
        fun createNewGame(
            address: String,
            port: Int,
            drawMapCallback: () -> Unit
        ): ru.hse.se.team9.model.logic.gamecycle.RemoteGameCycleLogic {
            val logic = RemoteGameCycleLogic(address, port, drawMapCallback)
            logic.serverObserver.onNext(PlayerMessage {
                startGame = StartGame {
                }
            })
            logic.mapReceivedLatch.await()
            return logic
        }

        /**
         * Tries to join an existing game at the remote server which has to be available at the specified address.
         * Blocks until initial map is received and then returns initialized RemoteGameCycleLogic.
         */
        fun joinGame(
            address: String,
            port: Int,
            gameId: Int,
            drawMapCallback: () -> Unit
        ): ru.hse.se.team9.model.logic.gamecycle.RemoteGameCycleLogic {
            val logic = RemoteGameCycleLogic(address, port, drawMapCallback)
            logic.serverObserver.onNext(PlayerMessage {
                joinGame = JoinGame {
                    setGameId(gameId)
                }
            })
            logic.mapReceivedLatch.await()
            return logic
        }
    }
}