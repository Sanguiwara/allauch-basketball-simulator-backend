package com.sanguiwara.service.simulator;

import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.result.ThreePointShootingResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class ThreePointSimulator {

    public static ThreePointShootingResult simulateThreePointShots(
            InGamePlayer shooter,
            List<InGamePlayer> potentialPassers,
            double assistedShotProbability,
            double assistBonusPct,
            double advantage3pt
    ) {
        int threePointAttempts = sampleThreePointAttempts(shooter.getUsageShoot(), shooter.getPlayer().agressivite());
        Random rng = new Random();
        List<ThreePointShotEvent> events = new ArrayList<>(threePointAttempts);
        int madeCount = 0;

        for (int i = 0; i < threePointAttempts; i++) {
            boolean assisted = rng.nextDouble() < assistedShotProbability;
            InGamePlayer assister = null;
            if (assisted) {
                assister = pickAssisterWeighted(potentialPassers, shooter, rng);
                if (assister == null) {
                    assisted = false;
                }
            }

            double shotPct = computeThreePointPct(
                    shooter.getPlayer().tir3Pts(),
                    advantage3pt,
                    assisted ? assistBonusPct : 0.0
            );

            boolean made = rng.nextDouble() < shotPct;

            shooter.recordThreePointShot(made);
            if (made) madeCount++;

            UUID assisterId = null;
            if (made && assisted) {
                assister.addAssist();
                assisterId = assister.getPlayer().id();
            }

            events.add(new ThreePointShotEvent(
                    shooter.getPlayer().id(),
                    i + 1,
                    assisted,
                    assisterId,
                    shotPct,
                    made
            ));
        }

        return new ThreePointShootingResult(threePointAttempts, madeCount, events);
    }

    public static double computeThreePointPct(int tir3PtsRating, double advantage3pt, double assistBonusPct) {
        double basePct = (tir3PtsRating / 100.0) * (0.35);
        double advantagePct = (advantage3pt / 50.0) * 0.30;
        return basePct + advantagePct + assistBonusPct;
    }

    public static InGamePlayer pickAssisterWeighted(List<InGamePlayer> passers, InGamePlayer shooter, Random rng) {
        double total = 0.0;
        for (InGamePlayer p : passers) {
            if (p == shooter) continue;
            total += p.getAssistWeight();
        }

        double r = rng.nextDouble() * total;
        for (InGamePlayer p : passers) {
            if (p == shooter) continue;
            r -= p.getAssistWeight();
            if (r <= 0.0) return p;
        }
        return null;
    }

    static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int sampleThreePointAttempts(
            int usage,
            int aggressiveness
    ) {
        Random random = new Random();
        double normalizedUsage = (usage - 10) / 20.0;
        double normalizedAggressiveness = aggressiveness / 100.0;

        double threePointIntensity =
                0.70 * normalizedUsage +
                        0.30 * normalizedAggressiveness;

        double expectedThreePointAttempts =
                1.8 + 12.5 * threePointIntensity;

        double standardDeviation =
                1.2 + 0.8 * threePointIntensity;

        int sampledThreePointAttempts =
                (int) Math.round(
                        expectedThreePointAttempts +
                                random.nextGaussian() * standardDeviation
                );

        return clamp(sampledThreePointAttempts, 0, 20);
    }

    public static ThreePointShootingResult get3ptsFromPlayer(GamePlan homeGamePlan, InGamePlayer inGameOff, Player def, double assistedShotPercentage) {
        Player off = inGameOff.getPlayer();

        double offScore =
                0.05 * off.speed() +
                        0.05 * off.size()
                        + 0.10 * off.endurance() +
                        0.80 * off.tir3Pts() +
                        0.15 * off.basketballIqOff();

        double defScore =
                0.10 * def.speed() +
                        0.10 * def.size() +
                        0.55 * def.defExterieur()
                        + 0.05 * def.endurance() +
                        0.10 * def.basketballIqDef();

        double adv = offScore - defScore;

        double advantage = clamp(adv, -50, 50);

        return ThreePointSimulator.simulateThreePointShots(inGameOff, homeGamePlan.getActivePlayers(), assistedShotPercentage, 0.15, advantage);
    }

    public static ThreePointShootingResult getTotal3ptsContribution(GamePlan home, GamePlan visitor, double playmakingContribution) {
        return home.getActivePlayers()
                .stream()
                .filter(offensivePlayer ->   visitor.getMatchups().get(offensivePlayer.getPlayer()) != null)
                .map(offensivePlayer -> ThreePointSimulator.get3ptsFromPlayer(home, offensivePlayer, visitor.getMatchups().get(offensivePlayer.getPlayer()), playmakingContribution))
                .reduce(ThreePointShootingResult.empty(), ThreePointShootingResult::combine);
    }
}
