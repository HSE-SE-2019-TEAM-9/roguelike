package ru.hse.se.team9.network

import arrow.core.getOrHandle
import com.google.protobuf.Empty
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import ru.hse.se.team9.conversions.ToProtoConverter.toProto
import ru.hse.se.team9.conversions.FromProtoConverter.toView
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.game.entities.map.distance.Manhattan
import ru.hse.se.team9.model.generators.GameGenerator
import ru.hse.se.team9.model.generators.confusion.RandomStrategyModifier
import ru.hse.se.team9.model.generators.consumables.RandomConsumable
import ru.hse.se.team9.model.generators.directions.RandomDirection
import ru.hse.se.team9.model.generators.heroes.DefaultHeroCreator
import ru.hse.se.team9.model.generators.items.RandomItem
import ru.hse.se.team9.model.generators.mobs.RandomMob
import ru.hse.se.team9.model.generators.positions.RandomPosition
import ru.hse.se.team9.model.logic.gamecycle.*
import ru.hse.se.team9.model.mapgeneration.creators.RandomMapCreator
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/** Mutliplayer gRPC Roguelike server */
class RoguelikeServer(private val port: Int) {
    private lateinit var serverImpl: RoguelikeServerImpl
    private lateinit var server: Server

    /** Starts servers */
    fun start() {
        serverImpl = RoguelikeServerImpl()
        serverImpl.addGameSession()
        server = ServerBuilder.forPort(port).addService(serverImpl).build()
        server.start()
    }

    /** Stop server and awaits termination */
    fun stop() {
        serverImpl.stop()
        server.shutdownNow()
        server.awaitTermination()
    }

    /** Awaits server termination */
    fun await() {
        server.awaitTermination()
    }
}

private class RoguelikeServerImpl : RoguelikeApiGrpc.RoguelikeApiImplBase() {
    private val games = ConcurrentHashMap<Int, GameSession>()
    private val threads = mutableListOf<Thread>()
    private var sessionCounter = 0
    private val generator = GameGenerator(
        RandomDirection,
        RandomPosition,
        RandomMob(RandomDirection),
        RandomStrategyModifier(RandomDirection),
        RandomItem,
        RandomConsumable,
        DefaultHeroCreator
    )
    private val mapCreator = RandomMapCreator.build(
        generator,
        mapWidth = 60,
        mapHeight = 60,
        distance = Manhattan,
        fogRadius = 12
    ).getOrHandle { throw it } // Never happens

    override fun getGames(request: Empty, responseObserver: StreamObserver<Service.GetGamesResponse>) {
        responseObserver.onNext(getGameSessions())
        responseObserver.onCompleted()
    }

    override fun join(responseObserver: StreamObserver<Service.ServerMessage>)
            : StreamObserver<Service.PlayerMessage> {
        return PlayerActionHandler(responseObserver)
    }

    @Synchronized
    fun stop() {
        for (game in games.values) {
            game.close()
        }
        for (thread in threads) {
            thread.interrupt()
            thread.join()
        }
    }

    @Synchronized
    private fun getGameSessions(): Service.GetGamesResponse {
        return Service.GetGamesResponse.newBuilder().addAllGames(
            games.values.map { it.getInfo() }
        ).build()
    }

    @Synchronized
    fun addGameSession(): Int {
        val gameId = sessionCounter++
        val gameSession = GameSession(gameId, gameId.toString())
        val thread = Thread(gameSession)
        thread.start()
        threads.add(thread)
        games[gameId] = gameSession
        return gameId
    }

    private inner class GameSession(private val id: Int, private val name: String) : Runnable {
        private val game = GameCycleProcessor(mapCreator.createMap().getOrHandle { throw it }, generator)
        private var playerCounter = 0
        private val players = ConcurrentHashMap<Int, PlayerActionHandler>()
        private val playerActions = LinkedBlockingQueue<PlayerAction>()

        @Synchronized
        fun getInfo(): Service.GameInfo {
            return Service.GameInfo.newBuilder()
                .setGameId(id)
                .setName(name)
                .setPlayers(playerCounter) // FIXME
                .build()
        }

        @Synchronized
        fun addPlayerAction(playerAction: PlayerAction) {
            playerActions.add(playerAction)
        }

        @Synchronized
        fun addPlayer(player: PlayerActionHandler): Int {
            val playerId = playerCounter++
            game.map.addHeroToRandomPosition(playerId, DefaultHeroCreator.createHero())
            players[playerId] = player
            return playerId
        }

        @Synchronized
        fun deletePlayer(playerId: Int) {
            game.map.removeHero(playerId)
            val player = players.remove(playerId)
            player?.gameSession = null
        }

        @Synchronized
        fun close() {
            for (player in players.values) {
                player.out.onCompleted()
            }
        }

        fun sendMap(player: PlayerActionHandler) {
            player.out.onNext(packMap(game.getCurrentMap(player.heroId)))
        }

        fun sendMap(playerId: Int) {
            sendMap(players[playerId]!!)
        }

        @Synchronized
        fun processPlayerAction(playerAction: PlayerAction) {
            val action = playerAction.action
            val playerId = playerAction.id
            val currentPlayer = players[playerId]!!
            when (action.requestCase) {
                Service.PlayerMessage.RequestCase.MAKE_MOVE -> {
                    game.makeMove(playerId, unpackMove(action.makeMove.move))
                    for (player in players.values) {
                        sendMap(player)
                    }
                }
                Service.PlayerMessage.RequestCase.GET_CURRENT_MAP -> {
                    sendMap(currentPlayer)
                }
                Service.PlayerMessage.RequestCase.PUT_ON_ITEM -> {
                    game.putOnItem(playerId, action.putOnItem.index)
                    sendMap(currentPlayer)
                }
                Service.PlayerMessage.RequestCase.PUT_OFF_ITEM -> {
                    game.putOffItem(playerId, action.putOffItem.type.toView())
                    sendMap(currentPlayer)
                }
                else -> throw IllegalStateException()
            }
        }

        override fun run() {
            try {
                while (true) {
                    val playerAction = playerActions.poll(Long.MAX_VALUE, TimeUnit.DAYS)!!
                    processPlayerAction(playerAction)
                }
            } catch (ignored: InterruptedException) {
            }
        }
    }

    private inner class PlayerActionHandler(val out: StreamObserver<Service.ServerMessage>)
            : StreamObserver<Service.PlayerMessage> {
        var heroId = -1
        var gameSession: GameSession? = null

        override fun onNext(value: Service.PlayerMessage) {
            gameSession?.addPlayerAction(PlayerAction(value, heroId)) ?: let {
                when (value.requestCase!!) {
                    Service.PlayerMessage.RequestCase.JOIN_GAME -> {
                        val gameId = value.joinGame.gameId
                        gameSession = games[gameId]
                        heroId = gameSession!!.addPlayer(this)
                        gameSession!!.sendMap(heroId)
                    }
                    Service.PlayerMessage.RequestCase.START_GAME -> {
                        val gameId = addGameSession()
                        gameSession = games[gameId]
                        heroId = gameSession!!.addPlayer(this)
                        gameSession!!.sendMap(heroId)
                    }
                    else -> throw IllegalStateException()
                }
            }
        }

        override fun onError(t: Throwable) {
            gameSession?.deletePlayer(heroId)
        }

        override fun onCompleted() {
            gameSession?.deletePlayer(heroId)
        }
    }

    private class PlayerAction(val action: Service.PlayerMessage, val id: Int)

    private fun packMap(mapView: MapView) =
        Service.ServerMessage.newBuilder()
            .setMapUpdate(
                Service.ServerMessage.MapUpdate.newBuilder()
                    .setMap(mapView.toProto())
                    .build()
            ).build()

    fun unpackMove(move: Service.PlayerMessage.MakeMove.Move): Move {
        return when (move) {
            Service.PlayerMessage.MakeMove.Move.LEFT -> Left
            Service.PlayerMessage.MakeMove.Move.UP -> Up
            Service.PlayerMessage.MakeMove.Move.RIGHT -> Right
            Service.PlayerMessage.MakeMove.Move.DOWN -> Down
            Service.PlayerMessage.MakeMove.Move.UNRECOGNIZED -> error("deserialization error")
        }
    }
}