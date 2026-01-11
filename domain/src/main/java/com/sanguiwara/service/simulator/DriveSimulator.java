package com.sanguiwara.service.simulator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.result.DriveResult;

import java.util.*;

public class DriveSimulator {
    private static final int MIN_ATTEMPTS = 0;
    private static final int MAX_ATTEMPTS = 20;
    private static final double MIN_ADVANTAGE = -50;
    private static final double MAX_ADVANTAGE = 50;
    private static final double ASSIST_BONUS_PCT = 0.15;

    private final Random rng;

    public DriveSimulator(Random rng) {
        this.rng = Objects.requireNonNull(rng);
    }

    public DriveResult simulateDrivesForMatchup(
            List<InGamePlayer> ingamePlayers,
            InGamePlayer attacker,
            Player defender,
            double assistedDrivePercentage
    ) {
        double advantageRaw = computeAdvantageDrive(attacker.getPlayer(), defender);
        double advantage = clamp(advantageRaw, MIN_ADVANTAGE, MAX_ADVANTAGE);

        int attempts = sampleDriveAttempts(
                attacker.getUsageShoot(),
                attacker.getPlayer().agressivite(),
                MIN_ATTEMPTS,
                MAX_ATTEMPTS
        );

        return simulateDriveEvents(
                attacker,
                ingamePlayers,
                attempts,
                assistedDrivePercentage,
                ASSIST_BONUS_PCT,
                advantage
        );
    }

    public DriveResult simulateDriveEvents(
            InGamePlayer attacker,
            List<InGamePlayer> potentialPassers,
            int attempts,
            double assistedDrivePercentage,
            double assistBonusPct,
            double advantageDrive
    ) {
        List<DriveEvent> events = new ArrayList<>(attempts);

        int made = 0;
        int fouls = 0;

        for (int driveNumber = 1; driveNumber <= attempts; driveNumber++) {

            boolean assisted = rng.nextDouble() < assistedDrivePercentage;

            InGamePlayer assister = null;
            if (assisted) {
                assister = pickAssisterWeighted(potentialPassers, attacker);
                if (assister == null) assisted = false;
            }

            double successPct = computeDriveSuccessPct(attacker.getPlayer(), advantageDrive,
                    assisted ? assistBonusPct : 0.0);

            boolean isMade = rng.nextDouble() < successPct;

            boolean foulDrawn = false;

            if (isMade) {
                made++;
                if (assisted) {
                    assister.addAssist();
                }
            }
            if (foulDrawn) fouls++;

            events.add(new DriveEvent(
                    attacker.getPlayer().id(),
                    driveNumber,
                    assisted,
                    assisted ? assister.getPlayer().id() : null,
                    successPct,
                    isMade,
                    foulDrawn,
                    advantageDrive
            ));
        }

        return new DriveResult(attempts, made, fouls, java.util.Collections.unmodifiableList(events));
    }

    public double computeAdvantageDrive(Player off, Player def) {
        double offScore =
                0.20 * off.speed()
                        + 0.10 * off.size()
                        + 0.05 * off.endurance()
                        + 0.20 * off.ballhandling()
                        + 0.50 * off.finitionAuCercle()
                        + 0.20 * off.floater()
                        + 0.05 * off.basketballIqOff();

        double defScore =
                0.12 * def.speed()
                        + 0.30 * def.size()
                        + 0.45 * def.defExterieur()
                        + 0.10 * def.endurance()
                        + 0.10 * def.basketballIqDef()
                        + 0.10 * def.steal()
                        + 0.10 * def.defPoste();

        return offScore - defScore;
    }

    public double computeDriveSuccessPct(Player attacker, double advantageDrive, double assistBonusPct) {
        double base = 0.10 + (attacker.finitionAuCercle() / 100.0) * 0.35
                + (attacker.floater() / 100.0) * 0.20;

        double advPct = (advantageDrive / 50.0) * 0.80;

        return clamp(base + advPct + assistBonusPct, 0.05, 0.95);
    }

    public int sampleDriveAttempts(int usage, int aggressiveness, int min, int max) {
        double usage01 = (usage - 10) / 20.0;
        double aggr01 = aggressiveness / 100.0;

        double intensity = 0.55 * usage01 + 0.45 * aggr01;

        double expected = 4.0 + 16.0 * intensity;
        double std = 1.6 + 1.0 * intensity;

        int sampled = (int) Math.round(expected + rng.nextGaussian() * std);
        return clamp(sampled, min, max);
    }

    private InGamePlayer pickAssisterWeighted(List<InGamePlayer> passers, InGamePlayer attacker) {
        double total = 0.0;
        for (InGamePlayer p : passers) {
            if (p == null || p == attacker) continue;
            total += Math.max(0.0, p.getAssistWeight());
        }
        if (total <= 0.0) return null;

        double r = rng.nextDouble() * total;
        for (InGamePlayer p : passers) {
            if (p == null || p == attacker) continue;
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

    public DriveResult getDriveTotalContribution(List<InGamePlayer> inGamePlayers, GamePlan defenseTeamGamePlan, double assistedShotPercentage) {
        return inGamePlayers.stream()
                .filter(offensivePlayer ->   defenseTeamGamePlan.getMatchups().get(offensivePlayer.getPlayer()) != null)
                .map(offensivePlayer -> this.simulateDrivesForMatchup(inGamePlayers, offensivePlayer, defenseTeamGamePlan.getMatchups().get(offensivePlayer.getPlayer()), assistedShotPercentage))
                .reduce(DriveResult.empty(), DriveResult::combine);
    }
}
