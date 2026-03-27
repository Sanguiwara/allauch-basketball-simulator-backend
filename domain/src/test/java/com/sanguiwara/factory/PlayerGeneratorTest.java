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
    void threePointShooter_shouldHaveEliteThreePoint() {
        PlayerGenerator gen = new PlayerGenerator(new Random(999L));
        Player p = gen.generatePlayer(PlayerArchetype.THREE_POINT_SHOOTER);

        assertBetweenInclusive(p.getTir3Pts(), 85, 99);
        assertBetweenInclusive(p.getLancerFranc(), 78, 99);
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
