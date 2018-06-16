package com.stewsters

import com.github.ocraft.s2client.api.S2Client.starcraft2Client
import com.github.ocraft.s2client.api.controller.S2Controller.starcraft2Game
import com.github.ocraft.s2client.protocol.action.Action.action
import com.github.ocraft.s2client.protocol.action.raw.ActionRawUnitCommand
import com.github.ocraft.s2client.protocol.data.Abilities
import com.github.ocraft.s2client.protocol.data.Units
import com.github.ocraft.s2client.protocol.data.Units.ZERG_DRONE
import com.github.ocraft.s2client.protocol.data.Units.ZERG_LARVA
import com.github.ocraft.s2client.protocol.game.BattlenetMap
import com.github.ocraft.s2client.protocol.game.ComputerPlayerSetup.computer
import com.github.ocraft.s2client.protocol.game.Difficulty
import com.github.ocraft.s2client.protocol.game.GameStatus
import com.github.ocraft.s2client.protocol.game.InterfaceOptions
import com.github.ocraft.s2client.protocol.game.PlayerSetup.participant
import com.github.ocraft.s2client.protocol.game.Race.PROTOSS
import com.github.ocraft.s2client.protocol.game.Race.ZERG
import com.github.ocraft.s2client.protocol.request.RequestCreateGame
import com.github.ocraft.s2client.protocol.request.RequestJoinGame
import com.github.ocraft.s2client.protocol.request.RequestObservation
import com.github.ocraft.s2client.protocol.request.RequestStep
import com.github.ocraft.s2client.protocol.request.Requests.actions
import com.github.ocraft.s2client.protocol.request.Requests.createGame
import com.github.ocraft.s2client.protocol.request.Requests.joinGame
import com.github.ocraft.s2client.protocol.request.Requests.nextStep
import com.github.ocraft.s2client.protocol.request.Requests.observation
import com.github.ocraft.s2client.protocol.response.ResponseCreateGame
import com.github.ocraft.s2client.protocol.response.ResponseJoinGame
import com.github.ocraft.s2client.protocol.response.ResponseObservation
import com.github.ocraft.s2client.protocol.response.ResponseStep

object SampleBot {
    @JvmStatic fun main(args: Array<String>) {

        val game = starcraft2Game().launch()
        val client = starcraft2Client().connectTo(game).traced(true).start()

        client.request<RequestCreateGame>(createGame()
                .onBattlenetMap(BattlenetMap.of("Lava Flow"))
                .withPlayerSetup(participant(), computer(PROTOSS, Difficulty.VERY_EASY)))

        client.responseStream()
                .takeWhile { response -> !game.inState(GameStatus.ENDED) }
                .subscribe { response ->

                    response.`as`(ResponseCreateGame::class.java).ifPresent { r ->
                        client.request<RequestJoinGame>(joinGame().`as`(ZERG).use(InterfaceOptions.interfaces().raw().build()))
                    }

                    response.`as`(ResponseJoinGame::class.java).ifPresent { r -> client.request<RequestObservation>(observation()) }

                    response.`as`(ResponseStep::class.java).ifPresent { r -> client.request<RequestObservation>(observation()) }

                    response.`as`(ResponseObservation::class.java).ifPresent { r ->
                        // HERE GOES BOT LOGIC

                        r.observation.raw.ifPresent { w ->
                            var mineralsSpentThisStep = 0

                            // weighs the need for defence, offense, and upgrades
                            // determine what to buy?
                            // Figure out what we need to build


                            val larvae = w.units.filter { it.type == ZERG_LARVA }
                            val drones = w.units.filter { it.type == ZERG_DRONE }

                            val hasSpawningPool = w.units.find { it.type == Units.ZERG_SPAWNING_POOL } != null

                            if (drones.size > 10) {

                                if (hasSpawningPool) {
//                                    // make zerglings
//                                    client.request(actions().of(action().raw(ActionRawUnitCommand.unitCommand()
//                                            .forUnits()
//                                            .useA
//
//                                    )))
                                } else {
                                    // make spawning pool
                                    client.request(actions().of(action().raw(ActionRawUnitCommand.unitCommand()
                                            .forUnits(drones.first())
                                            .useAbility(Abilities.BUILD_SPAWNING_POOL)
                                    )))
                                }
                            }


                            for (larva in larvae) {

                                // If we are at 80% pop cap, make an overlord
                                if (r.observation.playerCommon.minerals - mineralsSpentThisStep >= 100 && r.observation.playerCommon.foodUsed >= r.observation.playerCommon.foodCap * 0.8) {
                                    client.request(actions().of(
                                            action().raw(ActionRawUnitCommand.unitCommand()
                                                    .forUnits(larva)
                                                    .useAbility(Abilities.TRAIN_OVERLORD))
                                    ))
                                    mineralsSpentThisStep += 100
                                }

                                // else make some more drones
                                if (r.observation.playerCommon.minerals - mineralsSpentThisStep >= 50 && r.observation.playerCommon.foodUsed < r.observation.playerCommon.foodCap - 1) {
                                    client.request(actions().of(
                                            action().raw(ActionRawUnitCommand.unitCommand()
                                                    .forUnits(larva)
                                                    .useAbility(Abilities.TRAIN_DRONE))
                                    ))
                                    mineralsSpentThisStep += 50
                                }

                            }
                        }

                        client.request<RequestStep>(nextStep())
                    }

                }

        client.await()
    }
}