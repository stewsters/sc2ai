package com.stewsters

import com.github.ocraft.s2client.api.S2Client.starcraft2Client
import com.github.ocraft.s2client.api.controller.S2Controller.starcraft2Game
import com.github.ocraft.s2client.api.rx.Responses
import com.github.ocraft.s2client.protocol.action.Action.action
import com.github.ocraft.s2client.protocol.action.observer.ActionObserverCameraMove.cameraMove
import com.github.ocraft.s2client.protocol.action.raw.ActionRawUnitCommand
import com.github.ocraft.s2client.protocol.action.spatial.ActionSpatialUnitSelectionPoint.click
import com.github.ocraft.s2client.protocol.action.ui.ActionUiSelectArmy.selectArmy
import com.github.ocraft.s2client.protocol.data.Units
import com.github.ocraft.s2client.protocol.game.BattlenetMap
import com.github.ocraft.s2client.protocol.game.ComputerPlayerSetup.computer
import com.github.ocraft.s2client.protocol.game.Difficulty
import com.github.ocraft.s2client.protocol.game.PlayerSetup.participant
import com.github.ocraft.s2client.protocol.game.Race
import com.github.ocraft.s2client.protocol.request.RequestAction.actions
import com.github.ocraft.s2client.protocol.request.RequestCreateGame.createGame
import com.github.ocraft.s2client.protocol.request.RequestJoinGame.joinGame
import com.github.ocraft.s2client.protocol.request.RequestLeaveGame.leaveGame
import com.github.ocraft.s2client.protocol.response.ResponseCreateGame
import com.github.ocraft.s2client.protocol.response.ResponseJoinGame
import com.github.ocraft.s2client.protocol.response.ResponseLeaveGame
import com.github.ocraft.s2client.protocol.spatial.Point
import com.github.ocraft.s2client.protocol.spatial.PointI
import com.github.ocraft.s2client.protocol.unit.Tag

object SampleBotTwo {

    @JvmStatic
    fun main(args: Array<String>) {
//        val game = starcraft2Game().launch();
//        val client = starcraft2Client().connectTo(game).traced(true).start();
//
//        client.request(createGame()
//                .onBattlenetMap(BattlenetMap.of("Lava Flow"))
//                .withPlayerSetup(participant(), computer(Race.PROTOSS, Difficulty.MEDIUM)))
//
//        client.responseStream()
//                .takeWhile(Responses.isNot(ResponseLeaveGame::class.java))
//                .subscribe { response ->
//                    response.`as`(ResponseCreateGame::class.java).ifPresent({r -> client.request(joinGame().`as`(Race.TERRAN))});
//                    response.`as`(ResponseJoinGame::class.java).ifPresent({ r ->
//                        client.request(actions().of(
//                                action().raw(ActionRawUnitCommand.unitCommand().forUnits(Tag.of(TERRAN_COMMAND_CENTER.)).useAbility(TRAIN_SCV)),
//                                action().raw(cameraMove().to(Point.of(10f, 10f))),
//                                action().featureLayer(click().on(PointI.of(15, 10)).withMode(TOGGLE)),
//                                action().ui(selectArmy().add())
//                        ))
//                        client.request(leaveGame())
//                    })
//                }
//
//        client.await();
    }
}