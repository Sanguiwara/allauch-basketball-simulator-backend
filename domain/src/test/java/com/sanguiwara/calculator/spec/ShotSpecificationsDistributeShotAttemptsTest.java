package com.sanguiwara.calculator.spec;

import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration-style tests: validate that each ShotSpec distributes attempts in a way that reflects usage,
 * when aggressiveness and minutes played are equal across players.
 *
 * <p>We use a deterministic Random so the test is stable (no flaky statistical assertions).</p>
 */
@Slf4j
class ShotSpecificationsDistributeShotAttemptsTest {

    private enum SpecKind {
        THREE_POINT,
        TWO_POINT,
        DRIVE
    }

    private static Stream<Arguments> specAndAttempts() {
        return Stream.of(
                Arguments.of(SpecKind.THREE_POINT, 25),
                Arguments.of(SpecKind.THREE_POINT, 50),
                Arguments.of(SpecKind.THREE_POINT, 100),
                Arguments.of(SpecKind.TWO_POINT, 25),
                Arguments.of(SpecKind.TWO_POINT, 50),
                Arguments.of(SpecKind.TWO_POINT, 100),
                Arguments.of(SpecKind.DRIVE, 25),
                Arguments.of(SpecKind.DRIVE, 50),
                Arguments.of(SpecKind.DRIVE, 100)
        );
    }

    @ParameterizedTest(name = "{0} distributeShotAttempts with {1} attempts")
    @MethodSource("specAndAttempts")
    void distributeShotAttempts_usage30PlayersShouldGetMoreAttemptsThanUsage10Players(SpecKind kind, int attempts) {
        // 5 players (basketball lineup): 3 with usage=30, 2 with usage=10.
        // Same aggressiveness and minutes for everyone.
        // With aggressiveness=0 and minutes=40, intensity ratio is exactly 3:1 between usage 30 and usage 10.
        List<InGamePlayer> players = newPlayers(5, /*aggressiveness=*/0, /*minutesPlayed=*/40);
        List<InGamePlayer> highUsage = players.subList(0, 3);
        List<InGamePlayer> lowUsage = players.subList(3, players.size());

        for (InGamePlayer p : highUsage) setUsage(kind, p, 30);
        for (InGamePlayer p : lowUsage) setUsage(kind, p, 10);

        GamePlan plan = new GamePlan(null, null, null);
        plan.setActivePlayers(players);
        plan.setTotalShotNumber(attempts);
        configureShares(kind, plan);

        BadgeEngine badgeEngine = new BadgeEngine();
        Random random = new StratifiedRandom(attempts);

        distribute(kind, plan, random, badgeEngine);

        log.info("=== {} distributeShotAttempts (attempts={}) ===", kind, attempts);
        for (InGamePlayer p : players) {
            log.info("player={} usage={} attempts={} weight={}",
                    p.getPlayer().getName(),
                    usage(kind, p),
                    attempts(kind, p),
                    weight(kind, p)
            );
        }

        int totalAttempts = players.stream().mapToInt(p -> attempts(kind, p)).sum();
        assertEquals(attempts, totalAttempts, "Total attempts must match");

        int minHigh = highUsage.stream().mapToInt(p -> attempts(kind, p)).min().orElseThrow();
        int maxLow = lowUsage.stream().mapToInt(p -> attempts(kind, p)).max().orElseThrow();
        assertTrue(minHigh > maxLow,
                "With equal aggressiveness/minutes, usage=30 players should always get more attempts than usage=10 players");
    }

    private static void distribute(SpecKind kind, GamePlan plan, Random random, BadgeEngine badgeEngine) {
        switch (kind) {
            case THREE_POINT -> new ThreePointSpecification(random, badgeEngine).distributeShotAttempts(plan);
            case TWO_POINT -> new TwoPointSpecification(random, badgeEngine).distributeShotAttempts(plan);
            case DRIVE -> new DriveSpecification(random, badgeEngine).distributeShotAttempts(plan);
        }
    }

    private static void configureShares(SpecKind kind, GamePlan plan) {
        // Shares don't need to sum to 1; we force the tested spec to own all attempts.
        switch (kind) {
            case THREE_POINT -> {
                plan.setThreePointAttemptShare(1.0);
                plan.setMidRangeAttemptShare(0.0);
                plan.setDriveAttemptShare(0.0);
            }
            case TWO_POINT -> {
                plan.setThreePointAttemptShare(0.0);
                plan.setMidRangeAttemptShare(1.0);
                plan.setDriveAttemptShare(0.0);
            }
            case DRIVE -> {
                plan.setThreePointAttemptShare(0.0);
                plan.setMidRangeAttemptShare(0.0);
                plan.setDriveAttemptShare(1.0);
            }
        }
    }

    private static void setUsage(SpecKind kind, InGamePlayer p, int usage) {
        switch (kind) {
            case THREE_POINT -> p.setUsageShoot(usage);
            case TWO_POINT -> p.setUsagePost(usage);
            case DRIVE -> p.setUsageDrive(usage);
        }
    }

    private static int attempts(SpecKind kind, InGamePlayer p) {
        return switch (kind) {
            case THREE_POINT -> p.getThreePointAttempt();
            case TWO_POINT -> p.getTwoPointAttempts();
            case DRIVE -> p.getDriveAttempts();
        };
    }

    private static int usage(SpecKind kind, InGamePlayer p) {
        return switch (kind) {
            case THREE_POINT -> p.getUsageShoot();
            case TWO_POINT -> p.getUsagePost();
            case DRIVE -> p.getUsageDrive();
        };
    }

    private static double weight(SpecKind kind, InGamePlayer p) {
        return switch (kind) {
            case THREE_POINT -> p.getThreePointWeight();
            case TWO_POINT -> p.getTwoPointWeight();
            case DRIVE -> p.getDriveWeight();
        };
    }

    private static List<InGamePlayer> newPlayers(int count, int aggressiveness, int minutesPlayed) {
        List<InGamePlayer> players = new ArrayList<>(count);
        UUID gamePlanId = UUID.randomUUID();
        for (int i = 0; i < count; i++) {
            Player player = Player.builder()
                    .id(UUID.randomUUID())
                    .name("P" + i)
                    .birthDate(1990)
                    .agressivite(aggressiveness)
                    .morale(50)
                    .build();
            InGamePlayer inGamePlayer = new InGamePlayer(player, gamePlanId);
            inGamePlayer.setMinutesPlayed(minutesPlayed);
            players.add(inGamePlayer);
        }
        return players;
    }

    /**
     * Deterministic sequence of doubles in [0,1): (0.5/n, 1.5/n, ..., (n-0.5)/n).
     * This acts as a "stratified" sampling of the CDF and avoids flaky tests.
     */
    private static final class StratifiedRandom extends Random {
        private final int n;
        private int i;

        private StratifiedRandom(int n) {
            if (n <= 0) throw new IllegalArgumentException("n must be > 0");
            this.n = n;
            this.i = 0;
        }

        @Override
        public double nextDouble() {
            double v = (i + 0.5) / n;
            i++;
            if (i >= n) i = 0;
            return v;
        }
    }
}
