package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.calculator.spec.ShotSpec;
import com.sanguiwara.defense.DefenseSchemeResolver;
import com.sanguiwara.defense.DefensiveScheme;
import com.sanguiwara.gameevent.ShotEvent;
import com.sanguiwara.result.ShotResult;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class ShotSimulator<E extends ShotEvent, R extends ShotResult<E>> {
    private final Random random;
    private final ShotSpec<E, R> spec;
    private final DefenseSchemeResolver defensiveSchemeResolver;


    public R getTotalShotContribution(
            GamePlan offenseTeamGamePlan,
            GamePlan defenseTeamGamePlan,
            double assistProbability,
            double blockProbability
    ) {
        spec.distributeShotAttempts(offenseTeamGamePlan);
        List<InGamePlayer> offenseTeamActivePlayers = offenseTeamGamePlan.getActivePlayers();
        return offenseTeamActivePlayers.stream().map(offensivePlayer -> {
            DefensiveScheme defensiveScheme = defensiveSchemeResolver.resolve(defenseTeamGamePlan.getDefenseType());
            double advantageForAPlayer = defensiveScheme.calculateAdvantageForAPlayer(offensivePlayer, defenseTeamGamePlan, spec);
            return simulateShots(
                    defenseTeamGamePlan,
                    offensivePlayer,
                    offenseTeamActivePlayers,
                    assistProbability,
                    advantageForAPlayer,
                    blockProbability);
        }).reduce(spec.empty(), spec::combine);


    }


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
                //int teamMorale = averageTeamMorale(shooter, passersWithoutShooter);
                //shotPct = applyMoraleBonus(shotPct, teamMorale);
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

    private static double applyMoraleBonus(double baseShotPct, int morale) {
        double moraleBonus = (morale / 99.0) * 0.40 - 0.20;
        return Math.clamp(baseShotPct + moraleBonus, 0.0, 0.90);
    }

    /**
     * Team morale is approximated by the average of the shooter's morale and the morale of potential passers.
     * We exclude the shooter from {@code potentialPassersWithoutShooter} to avoid double-counting.
     */
    private static int averageTeamMorale(InGamePlayer shooter, List<InGamePlayer> potentialPassersWithoutShooter) {
        long sum = shooter.getPlayer().getMorale();
        int count = 1;
        for (InGamePlayer p : potentialPassersWithoutShooter) {
            sum += p.getPlayer().getMorale();
            count++;
        }
        return (int) Math.round(sum / (double) count);
    }




    public InGamePlayer pickAssister(List<InGamePlayer> potentialPassers, double assistedShotPercentage) {
        boolean assisted = random.nextDouble() < assistedShotPercentage;
        InGamePlayer assister = null;
        if (assisted) {
            double total = 0.0;
            for (InGamePlayer p : potentialPassers) {
                total += p.getAssistWeight();
            }

            double r = random.nextDouble() * total;
            for (var potentialPasser : potentialPassers) {
                r -= potentialPasser.getAssistWeight();
                if (r <= 0.0) {
                    assister = potentialPasser;
                    break;
                }
            }
        }
        return assister;
    }


    private InGamePlayer pickBlocker(List<InGamePlayer> potentialBlockers) {
        double total = 0.0;
        for (InGamePlayer p : potentialBlockers) {
            total += p.getBlockWeight();
        }
        InGamePlayer playerToReturn = null;

        double r = random.nextDouble() * total;
        for (InGamePlayer p : potentialBlockers) {
            r -= p.getBlockWeight();
            if (r <= 0.0) {
                playerToReturn = p;
                break;
            }
        }
        return playerToReturn;

    }


}
