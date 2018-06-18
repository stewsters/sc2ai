package com.stewsters;


import com.github.ocraft.s2client.api.S2Client;
import com.github.ocraft.s2client.api.controller.S2Controller;
import com.github.ocraft.s2client.api.rx.Responses;
import com.github.ocraft.s2client.protocol.game.BattlenetMap;
import com.github.ocraft.s2client.protocol.game.Difficulty;
import com.github.ocraft.s2client.protocol.game.GameStatus;
import com.github.ocraft.s2client.protocol.game.MultiplayerOptions;
import com.github.ocraft.s2client.protocol.response.*;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.github.ocraft.s2client.protocol.spatial.PointI;
import com.github.ocraft.s2client.protocol.unit.Tag;
import org.junit.Test;

import static com.github.ocraft.s2client.api.S2Client.starcraft2Client;
import static com.github.ocraft.s2client.api.controller.S2Controller.starcraft2Game;
import static com.github.ocraft.s2client.protocol.Defaults.defaultInterfaces;
import static com.github.ocraft.s2client.protocol.action.Action.action;
import static com.github.ocraft.s2client.protocol.action.Actions.Raw.cameraMove;
import static com.github.ocraft.s2client.protocol.action.Actions.Raw.unitCommand;
import static com.github.ocraft.s2client.protocol.action.Actions.Spatial.click;
import static com.github.ocraft.s2client.protocol.action.Actions.Ui.selectArmy;
import static com.github.ocraft.s2client.protocol.action.spatial.ActionSpatialUnitSelectionPoint.Type.TOGGLE;
import static com.github.ocraft.s2client.protocol.data.Abilities.TRAIN_SCV;
import static com.github.ocraft.s2client.protocol.game.ComputerPlayerSetup.computer;
import static com.github.ocraft.s2client.protocol.game.InterfaceOptions.interfaces;
import static com.github.ocraft.s2client.protocol.game.MultiplayerOptions.multiplayerSetupFor;
import static com.github.ocraft.s2client.protocol.game.PlayerSetup.participant;
import static com.github.ocraft.s2client.protocol.game.Race.*;
import static com.github.ocraft.s2client.protocol.request.Requests.*;

class ExampleTest {

    private static final Long COMMAND_CENTER = 1234L;

    @Test
    void startsTheGame() {
        S2Controller game = starcraft2Game().launch();
        S2Client client = starcraft2Client().connectTo(game).traced(true).start();

        client.request(createGame()
                .onBattlenetMap(BattlenetMap.of("Lava Flow"))
                .withPlayerSetup(participant(), computer(PROTOSS, Difficulty.MEDIUM)));

        client.responseStream()
                .takeWhile(Responses.isNot(ResponseLeaveGame.class))
                .subscribe(response -> {
                    response.as(ResponseCreateGame.class).ifPresent(r -> client.request(joinGame().as(TERRAN)));
                    response.as(ResponseJoinGame.class).ifPresent(r -> {
                        client.request(actions().of(
                                action().raw(unitCommand().forUnits(Tag.of(COMMAND_CENTER)).useAbility(TRAIN_SCV)),
                                action().raw(cameraMove().to(Point.of(10, 10))),
                                action().featureLayer(click().on(PointI.of(15, 10)).withMode(TOGGLE)),
                                action().ui(selectArmy().add())
                        ));
                        client.request(leaveGame());
                    });
                });

        client.await();
    }

//    @Test
//    void processesReplay() {
//        S2Controller game = starcraft2Game().launch();
//        S2Client client = starcraft2Client().connectTo(game).traced(true).start();
//
//        client.request(replayInfo().of(REPLAY_PATH).download());
//
//        client.responseStream()
//                .takeWhile(Responses.isNot(ResponseType.START_REPLAY))
//                .subscribe(response -> response.as(ResponseReplayInfo.class).ifPresent(r -> {
//                    r.getReplayInfo()
//                            .ifPresent(info -> game.relaunchIfNeeded(info.getBaseBuild(), info.getDataVersion()));
//                    client.request(startReplay()
//                            .from(REPLAY_PATH).use(defaultInterfaces()).toObserve(PLAYER_ID).disableFog());
//
//                }));
//
//        client.responseStream()
//                .takeWhile(response -> !game.inState(GameStatus.ENDED))
//                .subscribe(response -> {
//                    response.as(ResponseStartReplay.class).ifPresent(r -> client.request(observation()));
//                    response.as(ResponseObservation.class).ifPresent(r -> {
//                        client.request(nextStep().withCount(GAME_LOOP_COUNT));
//                        client.request(observation());
//                    });
//                });
//
//        client.await();
//    }
//
//    @Test
//    void playsMultiplayerGame() {
//        S2Controller game01 = starcraft2Game().launch();
//        S2Client client01 = starcraft2Client().connectTo(game01).traced(true).start();
//
//        S2Controller game02 = starcraft2Game().launch();
//        S2Client client02 = starcraft2Client().connectTo(game02).traced(true).start();
//
//        client01.request(createGame()
//                .onBattlenetMap(BattlenetMap.of("Lava Flow"))
//                .withPlayerSetup(participant(), participant()).realTime());
//
//        MultiplayerOptions multiplayerOptions = multiplayerSetupFor(S2Controller.lastPort(), PLAYER_COUNT);
//
//        client01.request(joinGame().as(PROTOSS).use(interfaces().raw()).with(multiplayerOptions));
//        client02.request(joinGame().as(ZERG).use(interfaces().raw()).with(multiplayerOptions));
//
//        client01.responseStream()
//                .takeWhile(Responses.isNot(ResponseType.QUIT_GAME))
//                .subscribe(response -> {
//                    response.as(ResponseJoinGame.class).ifPresent(r -> client01.request(leaveGame()));
//                    response.as(ResponseLeaveGame.class).ifPresent(r -> client01.request(quitGame()));
//                });
//
//        client02.responseStream()
//                .takeWhile(Responses.isNot(ResponseType.QUIT_GAME))
//                .subscribe(response -> {
//                    response.as(ResponseJoinGame.class).ifPresent(r -> client02.request(leaveGame()));
//                    response.as(ResponseLeaveGame.class).ifPresent(r -> client02.request(quitGame()));
//                });
//
//        client01.await();
//        client02.await();
//    }

}