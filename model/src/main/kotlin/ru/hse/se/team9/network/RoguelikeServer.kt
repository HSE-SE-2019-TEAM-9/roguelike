package ru.hse.se.team9.network

import arrow.core.getOrHandle
import com.google.protobuf.Empty
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

class RoguelikeServer : RoguelikeApiGrpc.RoguelikeApiImplBase() {
    private val games = ConcurrentHashMap<Int, GameSession>()
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
        mapWidth = 100,
        mapHeight = 100,
        distance = Manhattan,
        fogRadius = 10
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
    private fun getGameSessions(): Service.GetGamesResponse {
        return Service.GetGamesResponse.newBuilder().addAllGames(
            games.values.map { it.getInfo() }
        ).build()
    }

    @Synchronized
    private fun addGameSession(): Int {
        val gameId = sessionCounter++
        val gameSession = GameSession(gameId, gameId.toString())
        Thread(gameSession).start()
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
                        sendMap(currentPlayer)
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
            while (true) {
                val playerAction = playerActions.poll(30, TimeUnit.HOURS)!! // FIXME
                processPlayerAction(playerAction)
            }
        }
    }

    private inner class PlayerActionHandler(val out: StreamObserver<Service.ServerMessage>)
            : StreamObserver<Service.PlayerMessage> {
        var heroId = -1
        var gameId = -1
        var gameSession: GameSession? = null

        override fun onNext(value: Service.PlayerMessage) {
            gameSession?.addPlayerAction(PlayerAction(value, heroId)) ?: let {
                when (value.requestCase!!) {
                    Service.PlayerMessage.RequestCase.JOIN_GAME -> {
                        gameId = value.joinGame.gameId
                        gameSession = games[gameId]
                        heroId = gameSession!!.addPlayer(this)
                        gameSession!!.sendMap(heroId)
                    }
                    Service.PlayerMessage.RequestCase.START_GAME -> {
                        gameId = addGameSession()
                        gameSession = games[gameId]
                        heroId = gameSession!!.addPlayer(this)
                        gameSession!!.sendMap(heroId)
                    }
                    else -> throw IllegalStateException()
                }
            }
        }

        override fun onError(t: Throwable) {
        }

        override fun onCompleted() {
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

    companion object {
        fun run(port: Int) {
            val serverImpl = RoguelikeServer()
            serverImpl.addGameSession() // FIXME
            val server = ServerBuilder.forPort(port).addService(serverImpl).build()
            server.start()
            server.awaitTermination()
        }
    }
}