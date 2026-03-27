package com.sanguiwara.calculator.spec;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class ShotAttemptDistributorTest {

    private static Player player(String name, int aggressiveness) {
        return Player.builder()
                .id(UUID.randomUUID())
                .name(name)
                .birthDate(1990)
                .agressivite(aggressiveness)
                .morale(50)
                .build();
    }

    private static GamePlan planWith(InGamePlayer... players) {
        GamePlan plan = new GamePlan(null, null, null);
        plan.setActivePlayers(List.of(players));
        return plan;
    }

    @Test
    void distributeAttempts_usageHigher_shouldYieldMoreAttempts_whenAggressivenessAndMinutesEqual() {
        InGamePlayer lowUsage = new InGamePlayer(player("lowUsage", 0), null);
        lowUsage.setUsageShoot(1);
        lowUsage.setMinutesPlayed(20);

        InGamePlayer highUsage = new InGamePlayer(player("highUsage", 0), null);
        highUsage.setUsageShoot(30);
        highUsage.setMinutesPlayed(20);

        int attempts = 100;
        ShotAttemptDistributor.distributeAttempts(
                planWith(lowUsage, highUsage),
                attempts,
                InGamePlayer::getUsageShoot,
                InGamePlayer::setThreePointContribution,
                InGamePlayer::getThreePointContribution,
                InGamePlayer::setThreePointWeight,
                InGamePlayer::getThreePointWeight,
                InGamePlayer::addThreePointShot,
                new Random(42L)
        );

        int low = lowUsage.getThreePointAttempt();
        int high = highUsage.getThreePointAttempt();
        log.info("usage test -> lowUsage attempts={}, highUsage attempts={}", low, high);
        assertEquals(attempts, low + high);
        assertTrue(high > low, "Higher usage should produce more shot attempts");
    }

    @Test
    void distributeAttempts_aggressivenessHigher_shouldYieldMoreAttempts_whenUsageAndMinutesEqual() {
        InGamePlayer lowAgg = new InGamePlayer(player("lowAgg", 1), null);
        lowAgg.setUsageShoot(0);
        lowAgg.setMinutesPlayed(20);

        InGamePlayer highAgg = new InGamePlayer(player("highAgg", 99), null);
        highAgg.setUsageShoot(0);
        highAgg.setMinutesPlayed(20);

        int attempts = 100;
        ShotAttemptDistributor.distributeAttempts(
                planWith(lowAgg, highAgg),
                attempts,
                InGamePlayer::getUsageShoot,
                InGamePlayer::setThreePointContribution,
                InGamePlayer::getThreePointContribution,
                InGamePlayer::setThreePointWeight,
                InGamePlayer::getThreePointWeight,
                InGamePlayer::addThreePointShot,
                new Random(1337L)
        );

        int low = lowAgg.getThreePointAttempt();
        int high = highAgg.getThreePointAttempt();
        log.info("aggressiveness test -> lowAgg attempts={}, highAgg attempts={}", low, high);
        assertEquals(attempts, low + high);
        assertTrue(high > low, "Higher aggressiveness should produce more shot attempts");
    }

    @Test
    void distributeAttempts_minutesHigher_shouldYieldMoreAttempts_whenUsageAndAggressivenessEqual() {
        InGamePlayer lowMin = new InGamePlayer(player("lowMin", 1), null);
        lowMin.setUsageShoot(1);
        lowMin.setMinutesPlayed(1);

        InGamePlayer highMin = new InGamePlayer(player("highMin", 1), null);
        highMin.setUsageShoot(1);
        highMin.setMinutesPlayed(40);

        int attempts = 100;
        ShotAttemptDistributor.distributeAttempts(
                planWith(lowMin, highMin),
                attempts,
                InGamePlayer::getUsageShoot,
                InGamePlayer::setThreePointContribution,
                InGamePlayer::getThreePointContribution,
                InGamePlayer::setThreePointWeight,
                InGamePlayer::getThreePointWeight,
                InGamePlayer::addThreePointShot,
                new Random(7L)
        );

        int low = lowMin.getThreePointAttempt();
        int high = highMin.getThreePointAttempt();
        log.info("minutes test -> lowMin attempts={}, highMin attempts={}", low, high);
        assertEquals(attempts, low + high);
        assertTrue(high > low, "Higher minutes played should produce more shot attempts");
    }
}
