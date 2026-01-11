package com.sanguiwara.service.simulator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.TwoPointShootingResult;

import java.util.*;


public class TwoPointSimulator {

    private static final int MIN_ATTEMPTS = 0;
    private static final int MAX_ATTEMPTS = 20;
    private static final double MIN_ADVANTAGE = -50;
    private static final double MAX_ADVANTAGE = 50;
    private static final double ASSIST_BONUS_PCT = 0.15;

    private final Random rng;

    public TwoPointSimulator(Random rng) {
        this.rng = Objects.requireNonNull(rng);
    }

    public TwoPointShootingResult simulate2ptForMatchup(
            List<InGamePlayer> offensePlan,
            InGamePlayer shooter,
            Player defender,
            double assistedShotPercentage
    ) {
        double advantageRaw = computeAdvantage2pt(shooter.getPlayer(), defender);
        double advantage = clamp(advantageRaw, MIN_ADVANTAGE, MAX_ADVANTAGE);

        int attempts = sampleTwoPointAttempts(
                shooter.getUsageShoot(),
                shooter.getPlayer().agressivite(),
                MIN_ATTEMPTS,
                MAX_ATTEMPTS
        );

        return simulateShots(
                shooter,
                offensePlan,
                attempts,
                assistedShotPercentage,
                ASSIST_BONUS_PCT,
                advantage
        );
    }

    public TwoPointShootingResult simulateShots(
            InGamePlayer shooter,
            List<InGamePlayer> potentialPassers,
            int attempts,
            double assistedShotPercentage,
            double assistBonusPct,
            double advantage2pt
    ) {
        List<TwoPointShotEvent> events = new ArrayList<>(attempts);
        int madeCount = 0;

        for (int shotNumber = 1; shotNumber <= attempts; shotNumber++) {

            boolean assisted = rng.nextDouble() < assistedShotPercentage;
            InGamePlayer assister = null;
            if (assisted) {
                assister = pickAssisterWeighted(potentialPassers, shooter);
            }

            double shotPct = computeTwoPointPct(shooter.getPlayer(), advantage2pt,
                    assisted ? assistBonusPct : 0.0);

            boolean made = rng.nextDouble() < shotPct;

            shooter.recordTwoPointShot(made);

            if (made) {
                madeCount++;
                if (assisted) {
                    assister.addAssist();
                }
            }

            events.add(new TwoPointShotEvent(
                    shooter.getPlayer().id(),
                    shotNumber,
                    assisted,
                    assisted ? assister.getPlayer().id() : null,
                    shotPct,
                    made,
                    advantage2pt
            ));
        }

        return new TwoPointShootingResult(attempts, madeCount, java.util.Collections.unmodifiableList(events));
    }

    public double computeTwoPointPct(Player shooter, double advantage2pt, double assistBonusPct) {
        double base;
        base = 0.30 + (shooter.finitionAuCercle() / 100.0) * 0.40
                + (shooter.size() / 100.0) * 0.05;

        double advPct = (advantage2pt / 50.0) * 0.18;

        return clamp(base + advPct + assistBonusPct, 0.10, 0.85);
    }

    public double computeAdvantage2pt(Player off, Player def) {
        double offScore =
                0.05 * off.speed()
                        + 0.25 * off.size()
                        + 0.15 * off.endurance()
                        + 0.10 * off.ballhandling()
                        + 0.35 * off.finitionAuCercle()
                        + 0.10 * off.basketballIqOff();

        double defInterior = 0.6 * def.defPoste() + 0.4 * def.size();
        double defScore =
                0.05 * def.speed()
                        + 0.25 * def.size()
                        + 0.25 * defInterior
                        + 0.15 * def.endurance()
                        + 0.15 * def.basketballIqDef()
                        + 0.05 * def.steal();
        return offScore - defScore;
    }

    public int sampleTwoPointAttempts(int usage, int aggressiveness, int min, int max) {
        double usage01 = (usage - 10) / 20.0;
        double aggr01 = aggressiveness / 100.0;

        double intensity = 0.65 * usage01 + 0.35 * aggr01;

        double base = 2.0;
        double mult = 8.0;

        double expected = base + mult * intensity;
        double std = 1.4 + 0.8 * intensity;

        int sampled = (int) Math.round(expected + rng.nextGaussian() * std);
        return clamp(sampled, min, max);
    }

    private InGamePlayer pickAssisterWeighted(List<InGamePlayer> passers, InGamePlayer shooter) {
        double total = 0.0;
        for (InGamePlayer p : passers) {
            if (p == null || p == shooter) continue;
            total += Math.max(0.0, p.getAssistWeight());
        }
        if (total <= 0.0) return null;

        double r = rng.nextDouble() * total;
        for (InGamePlayer p : passers) {
            if (p == null || p == shooter) continue;
            r -= Math.max(0.0, p.getAssistWeight());
            if (r <= 0.0) return p;
        }
        return null;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public TwoPointShootingResult get2ptsTotalContribution(GamePlan offenseTeamGamePlan, GamePlan defenseTeamGamePlan, double assistedShotPercentage) {
        return offenseTeamGamePlan.getActivePlayers().stream()
                .filter(offensivePlayer ->   defenseTeamGamePlan.getMatchups().get(offensivePlayer.getPlayer()) != null)
                .map(offensivePlayer -> this.simulate2ptForMatchup(offenseTeamGamePlan.getActivePlayers(), offensivePlayer, defenseTeamGamePlan.getMatchups().get(offensivePlayer.getPlayer()), assistedShotPercentage))
                .reduce(TwoPointShootingResult.empty(), TwoPointShootingResult::combine);
    }
}
