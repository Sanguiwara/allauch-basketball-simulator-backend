package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.ShotEvent;
import com.sanguiwara.result.ShotResult;
import com.sanguiwara.calculator.spec.ShotSpec;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
public class ShotSimulator<E extends ShotEvent, R extends ShotResult<E>> {

    private final AssistManager assistManager;
    private final Random random;
    private final ShotSpec<E,R> spec;


    public R simulateShots(
            InGamePlayer shooter,
            List<InGamePlayer> potentialPassers,
            double assistedShotProbability,
            double matchupAdvantage
    ) {

        int attempts = spec.sampleAttempts(shooter);

        List<E> events = new ArrayList<>(attempts);
        int madeCount = 0;

        for (int i = 0; i < attempts; i++) {

            InGamePlayer assister = assistManager.getAssister(shooter, potentialPassers, assistedShotProbability);
            boolean isAssistedShot = assister != null;


            double shotPct = spec.computePct(
                    shooter,
                    matchupAdvantage,
                    isAssistedShot
            );
            boolean made = random.nextDouble() < shotPct;


            if (made) {
                madeCount++;
                if (isAssistedShot) {
                    assister.addAssist();
                }
            }

            events.add(spec.create(
                    shooter.getPlayer().id(),
                    i + 1,
                    isAssistedShot,
                    isAssistedShot ? assister.getPlayer().id() : null,
                    shotPct,
                    made,
                    matchupAdvantage
            ));
        }

        return spec.createResult(attempts, madeCount, events);
    }


    public R getTotalShotContribution(
            GamePlan offenseTeamGamePlan,
            GamePlan defenseTeamGamePlan,
            double assistProbability
    ) {
        List<InGamePlayer> offenseTeamActivePlayers = offenseTeamGamePlan.getActivePlayers();
        Map<Player, Player> matchups = defenseTeamGamePlan.getMatchups();
        return offenseTeamActivePlayers.stream()
                .map(offensivePlayer -> {
                    Player defender = matchups.get(offensivePlayer.getPlayer());
                    if (defender != null) {
                        double matchupAdvantage = spec.evaluateMatchupAdvantage(offensivePlayer.getPlayer(), defender);
                        return simulateShots(
                                offensivePlayer,
                                offenseTeamActivePlayers,
                                assistProbability,
                                matchupAdvantage);
                    } else {

                        return simulateShots(
                                offensivePlayer,
                                offenseTeamActivePlayers,
                                assistProbability,
                                0.0);
                    }
                })
                .reduce(spec.empty(), spec::combine);
    }


}
