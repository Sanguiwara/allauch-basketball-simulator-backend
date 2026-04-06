package com.sanguiwara.factory;

import com.sanguiwara.baserecords.Player;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PlayerGeneratorTest {

    @Test
    void generatePlayer_shouldInitializeCollections() {
        PlayerGenerator gen = new PlayerGenerator(new Random(42L));
        Player p = gen.generatePlayer();

        assertNotNull(p.getTeamsID());
        assertNotNull(p.getBadgeIds());
        assertTrue(p.getTeamsID().isEmpty());
        // Auto-badges are assigned at generation based on player stats thresholds.
        assertFalse(p.getBadgeIds().isEmpty());
        assertNotNull(p.getName());
        assertTrue(p.getName().contains(" "));
    }

    @Test
    void generateRandomPlayer_shouldMatchLegacyRanges() {
        PlayerGenerator gen = new PlayerGenerator(new Random(42L));
        Player p = gen.generateRandomPlayer();

        assertBetweenInclusive(p.getTir3Pts(), 30, 95);
        assertBetweenInclusive(p.getTir2Pts(), 30, 95);
        assertBetweenInclusive(p.getLancerFranc(), 30, 95);
        assertBetweenInclusive(p.getFloater(), 30, 95);
        assertBetweenInclusive(p.getFinitionAuCercle(), 30, 95);
        assertBetweenInclusive(p.getSize(), 30, 95);
        assertBetweenInclusive(p.getEgo(), 0, 99);
    }

    @Test
    void soldier_shouldHaveStrongDefenseAndPhysical() {
        PlayerGenerator gen = new PlayerGenerator(new Random(7L));
        Player p = gen.generatePlayer( PlayerArchetype.SOLDIER);

        assertBetweenInclusive(p.getPhysique(), 82, 99);
        assertBetweenInclusive(p.getSolidite(), 80, 99);
        assertBetweenInclusive(p.getEndurance(), 78, 96);
        assertBetweenInclusive(p.getDefExterieur(), 75, 95);
        assertBetweenInclusive(p.getDefPoste(), 75, 95);
        assertBetweenInclusive(p.getAgressiviteRebond(), 78, 99);
        assertBetweenInclusive(p.getTir3Pts(), 25, 65);
        assertBetweenInclusive(p.getEgo(), 20, 60);
    }

    @Test
    void croqueur_shouldHaveEgo99() {
        PlayerGenerator gen = new PlayerGenerator(new Random(8L));
        Player p = gen.generatePlayer(PlayerArchetype.CROQUEUR);
        assertEquals(99, p.getEgo());
    }

    @Test
    void strategist_shouldHaveElitePlaymakingStats_andOtherStatsInRange() {
        PlayerGenerator gen = new PlayerGenerator(new Random(123L));
        Player p = gen.generatePlayer(PlayerArchetype.STRATEGIST);

        // RegularMan2ManScheme playmaking offensive score inputs
        assertAll(
                () -> assertBetweenInclusive(p.getSpeed(), 75, 99),
                () -> assertBetweenInclusive(p.getSize(), 75, 99),
                () -> assertBetweenInclusive(p.getEndurance(), 75, 99),
                () -> assertBetweenInclusive(p.getPassingSkills(), 75, 99),
                () -> assertBetweenInclusive(p.getBasketballIqOff(), 75, 99),
                () -> assertBetweenInclusive(p.getBallhandling(), 75, 99),
                () -> assertBetweenInclusive(p.getTir3Pts(), 75, 99),
                () -> assertBetweenInclusive(p.getTir2Pts(), 75, 99),
                () -> assertBetweenInclusive(p.getFinitionAuCercle(), 75, 99),
                () -> assertBetweenInclusive(p.getFloater(), 75, 99)
        );

        // "Rest" stays in [1..82] (spot-check a few non-playmaking stats).
        assertAll(
                () -> assertBetweenInclusive(p.getDefExterieur(), 1, 82),
                () -> assertBetweenInclusive(p.getProtectionCercle(), 1, 82),
                () -> assertBetweenInclusive(p.getPhysique(), 1, 82),
                () -> assertBetweenInclusive(p.getIq(), 1, 82),
                () -> assertBetweenInclusive(p.getCoachability(), 1, 82),
                () -> assertBetweenInclusive(p.getEgo(), 1, 82)
        );
    }

    @Test
    void twoPointScorer_shouldHaveEliteTwoPointOffensiveScoreStats_andOtherStatsInRange() {
        PlayerGenerator gen = new PlayerGenerator(new Random(456L));
        Player p = gen.generatePlayer(PlayerArchetype.TWO_POINT_SCORER);

        // TwoPointSpecification#getPlayerScoreForAShot inputs
        assertAll(
                () -> assertBetweenInclusive(p.getSpeed(), 75, 99),
                () -> assertBetweenInclusive(p.getSize(), 75, 99),
                () -> assertBetweenInclusive(p.getEndurance(), 75, 99),
                () -> assertBetweenInclusive(p.getFinitionAuCercle(), 75, 99),
                () -> assertBetweenInclusive(p.getTir2Pts(), 75, 99),
                () -> assertBetweenInclusive(p.getBasketballIqOff(), 75, 99)
        );

        // "Rest" stays in [1..82] (spot-check a few non-two-point-off-score stats).
        assertAll(
                () -> assertBetweenInclusive(p.getTir3Pts(), 1, 82),
                () -> assertBetweenInclusive(p.getFloater(), 1, 82),
                () -> assertBetweenInclusive(p.getWeight(), 1, 82),
                () -> assertBetweenInclusive(p.getDefPoste(), 1, 82),
                () -> assertBetweenInclusive(p.getPassingSkills(), 1, 82),
                () -> assertBetweenInclusive(p.getEgo(), 1, 82)
        );
    }

    @Test
    void allStar_shouldStayInBounds() {
        PlayerGenerator gen = new PlayerGenerator(new Random(123L));
        Player p = gen.generatePlayer( PlayerArchetype.ALL_STAR);

        assertAll(
                () -> assertBetweenInclusive(p.getTir3Pts(), 0, 99),
                () -> assertBetweenInclusive(p.getTir2Pts(), 0, 99),
                () -> assertBetweenInclusive(p.getLancerFranc(), 0, 99),
                () -> assertBetweenInclusive(p.getFloater(), 0, 99),
                () -> assertBetweenInclusive(p.getFinitionAuCercle(), 0, 99),
                () -> assertBetweenInclusive(p.getAgressivite(), 0, 99),
                () -> assertBetweenInclusive(p.getSpeed(), 0, 99),
                () -> assertBetweenInclusive(p.getBallhandling(), 0, 99),
                () -> assertBetweenInclusive(p.getSize(), 0, 99),
                () -> assertBetweenInclusive(p.getWeight(), 0, 99),
                () -> assertBetweenInclusive(p.getDefExterieur(), 0, 99),
                () -> assertBetweenInclusive(p.getDefPoste(), 0, 99),
                () -> assertBetweenInclusive(p.getProtectionCercle(), 0, 99),
                () -> assertBetweenInclusive(p.getTimingRebond(), 0, 99),
                () -> assertBetweenInclusive(p.getAgressiviteRebond(), 0, 99),
                () -> assertBetweenInclusive(p.getSteal(), 0, 99),
                () -> assertBetweenInclusive(p.getTimingBlock(), 0, 99),
                () -> assertBetweenInclusive(p.getPhysique(), 0, 99),
                () -> assertBetweenInclusive(p.getBasketballIqOff(), 0, 99),
                () -> assertBetweenInclusive(p.getBasketballIqDef(), 0, 99),
                () -> assertBetweenInclusive(p.getPassingSkills(), 0, 99),
                () -> assertBetweenInclusive(p.getIq(), 0, 99),
                () -> assertBetweenInclusive(p.getEndurance(), 0, 99),
                () -> assertBetweenInclusive(p.getSolidite(), 0, 99),
                () -> assertBetweenInclusive(p.getPotentielSkill(), 0, 99),
                () -> assertBetweenInclusive(p.getPotentielPhysique(), 0, 99),
                () -> assertBetweenInclusive(p.getCoachability(), 0, 99),
                () -> assertBetweenInclusive(p.getEgo(), 0, 99),
                () -> assertBetweenInclusive(p.getSoftSkills(), 0, 99),
                () -> assertBetweenInclusive(p.getLeadership(), 0, 99),
                () -> assertBetweenInclusive(p.getMorale(), 0, 99)
        );
        assertEquals(99, p.getEgo());
    }

    @Test
    void threePointShooter_shouldHaveEliteThreePointOffensiveScoreStats_andOtherStatsInRange() {
        PlayerGenerator gen = new PlayerGenerator(new Random(999L));
        Player p = gen.generatePlayer(PlayerArchetype.THREE_POINT_SHOOTER);

        // ThreePointSpecification#getPlayerScoreForAShot inputs
        assertAll(
                () -> assertBetweenInclusive(p.getSpeed(), 75, 99),
                () -> assertBetweenInclusive(p.getSize(), 75, 99),
                () -> assertBetweenInclusive(p.getEndurance(), 75, 99),
                () -> assertBetweenInclusive(p.getTir3Pts(), 75, 99),
                () -> assertBetweenInclusive(p.getBasketballIqOff(), 75, 99)
        );

        // "Rest" stays in [1..82] (spot-check a few non-three-point-off-score stats).
        assertAll(
                () -> assertBetweenInclusive(p.getTir2Pts(), 1, 82),
                () -> assertBetweenInclusive(p.getLancerFranc(), 1, 82),
                () -> assertBetweenInclusive(p.getFloater(), 1, 82),
                () -> assertBetweenInclusive(p.getFinitionAuCercle(), 1, 82),
                () -> assertBetweenInclusive(p.getPassingSkills(), 1, 82),
                () -> assertBetweenInclusive(p.getEgo(), 1, 82)
        );
    }

    @Test
    void driveSpecialist_shouldHaveEliteDriveOffensiveScoreStats_andOtherStatsInRange() {
        PlayerGenerator gen = new PlayerGenerator(new Random(321L));
        Player p = gen.generatePlayer(PlayerArchetype.DRIVE_SPECIALIST);

        // DriveSpecification#getPlayerScoreForAShot inputs
        assertAll(
                () -> assertBetweenInclusive(p.getSpeed(), 75, 99),
                () -> assertBetweenInclusive(p.getSize(), 75, 99),
                () -> assertBetweenInclusive(p.getEndurance(), 75, 99),
                () -> assertBetweenInclusive(p.getBallhandling(), 75, 99),
                () -> assertBetweenInclusive(p.getFinitionAuCercle(), 75, 99),
                () -> assertBetweenInclusive(p.getFloater(), 75, 99),
                () -> assertBetweenInclusive(p.getBasketballIqOff(), 75, 99)
        );

        // "Rest" stays in [1..82] (spot-check a few non-drive-off-score stats).
        assertAll(
                () -> assertBetweenInclusive(p.getTir3Pts(), 1, 82),
                () -> assertBetweenInclusive(p.getTir2Pts(), 1, 82),
                () -> assertBetweenInclusive(p.getDefExterieur(), 1, 82),
                () -> assertBetweenInclusive(p.getSteal(), 1, 82),
                () -> assertBetweenInclusive(p.getPassingSkills(), 1, 82),
                () -> assertBetweenInclusive(p.getEgo(), 1, 82)
        );
    }

    @Test
    void youngStar_shouldHaveElitePotentialSkill_andMediocreStats() {
        PlayerGenerator gen = new PlayerGenerator(new Random(1234L));
        Player p = gen.generatePlayer(PlayerArchetype.YOUNG_STAR);

        assertEquals(99, p.getPotentielSkill());
        assertBetweenInclusive(p.getTir3Pts(), 20, 70);
        assertBetweenInclusive(p.getBallhandling(), 20, 70);
        assertBetweenInclusive(p.getPhysique(), 20, 70);
        assertBetweenInclusive(p.getBasketballIqOff(), 20, 70);
    }

    private static void assertBetweenInclusive(int actual, int min, int max) {
        assertTrue(actual >= min && actual <= max, "Expected " + actual + " to be in [" + min + ".." + max + "]");
    }
}
