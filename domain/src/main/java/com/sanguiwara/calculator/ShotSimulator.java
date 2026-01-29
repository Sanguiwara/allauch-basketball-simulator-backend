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
    private final Random random;
    private final ShotSpec<E,R> spec;


    private R simulateShots(
            GamePlan defensiveTeamGamePlan,
            InGamePlayer shooter,
            List<InGamePlayer> potentialPassers,
            double assistedShotProbability,
            double matchupAdvantage,
            double blockProbability
    ) {

        int attempts = spec.getAttempts(shooter);

        List<E> events = new ArrayList<>(attempts);
        int madeCount = 0;

        for (int i = 0; i < attempts; i++) {


            boolean blocked = random.nextDouble() < blockProbability * spec.getBlockProbabilityCoefficient();

            if (blocked) {
                InGamePlayer blocker = pickBlocker(defensiveTeamGamePlan.getActivePlayers());
                blocker.addBlock();
                events.add(spec.create(
                        shooter,
                        i + 1,
                        false,
                        null,
                        0.0,
                        false,
                        matchupAdvantage,
                        true
                ));
            } else {
                List<InGamePlayer> passersWithoutShooter =
                        potentialPassers.stream()
                                .filter(p -> p != shooter) // ou !p.equals(shooter)
                                .toList(); // Java 16+


                InGamePlayer assister = pickAssister(passersWithoutShooter, assistedShotProbability);
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
                        shooter,
                        i + 1,
                        isAssistedShot,
                        isAssistedShot ? assister.getPlayer().getId() : null,
                        shotPct,
                        made,
                        matchupAdvantage,
                        false
                ));
            }
        }

        return spec.createResult(attempts, madeCount, events);
    }


    public R getTotalShotContribution(
            GamePlan offenseTeamGamePlan,
            GamePlan defenseTeamGamePlan,
            double assistProbability,
            double blockProbability
    ) {
        spec.distributeShotAttempts(offenseTeamGamePlan);
        List<InGamePlayer> offenseTeamActivePlayers = offenseTeamGamePlan.getActivePlayers();
        Map<Player, Player> matchups = defenseTeamGamePlan.getMatchups();


        return offenseTeamActivePlayers.stream()
                .map(offensivePlayer -> {
                    Player defender = matchups.get(offensivePlayer.getPlayer());
                    if (defender != null) {
                        double matchupAdvantage = spec.evaluateMatchupAdvantage(offensivePlayer.getPlayer(), defender);
                        return simulateShots(
                                defenseTeamGamePlan,
                                offensivePlayer,
                                offenseTeamActivePlayers,
                                assistProbability,
                                matchupAdvantage,
                                blockProbability);
                    } else {

                        return simulateShots(
                                defenseTeamGamePlan,
                                offensivePlayer,
                                offenseTeamActivePlayers,
                                assistProbability,
                                0.0,
                                blockProbability);
                    }
                })
                .reduce(spec.empty(), spec::combine);
    }


    public InGamePlayer pickAssister( List<InGamePlayer> potentialPassers, double assistedShotPercentage) {
        boolean assisted = random.nextDouble() < assistedShotPercentage;
        InGamePlayer assister = null;
        if (assisted) {
            InGamePlayer result = null;
            double total = 0.0;
            for (InGamePlayer p : potentialPassers) {
                total += Math.max(0.0, p.getAssistWeight());
            }

            double r = random.nextDouble() * total;
            for (InGamePlayer p : potentialPassers) {
                r -= Math.max(0.0, p.getAssistWeight());
                if (r <= 0.0) {
                    result = p;
                    break;
                }
            }
            assister = result;
        }
        return assister;
    }



    private InGamePlayer pickBlocker( List<InGamePlayer> potentialBlockers) {
        double total = 0.0;
        for (InGamePlayer p : potentialBlockers) {
            total +=  p.getBlockWeight();
        }
        InGamePlayer playerToReturn = null;

        double r = random.nextDouble() * total;
        for (InGamePlayer p : potentialBlockers) {
            r -=  p.getBlockWeight();
            if (r <= 0.0) {
                playerToReturn = p;
                break;
            }
        }
        return playerToReturn;

    }






}
