package com.sanguiwara.badges;

import com.sanguiwara.baserecords.Player;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AutoSkillBadgesTest {

    @Test
    void precision3pts_shouldAssignCorrectRank_andRemovePreviousOnChange() {
        Player p = basePlayer();
        Set<Long> badgeIds = p.getBadgeIds();

        p.setTir3Pts(29);
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_FER_ID));
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_BRONZE_ID));
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_OR_ID));
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_PLATINE_ID));

        p.setTir3Pts(30);
        assertTrue(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_FER_ID));

        p.setTir3Pts(50);
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_FER_ID));
        assertTrue(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_BRONZE_ID));

        p.setTir3Pts(70);
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_BRONZE_ID));
        assertTrue(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_OR_ID));

        p.setTir3Pts(90);
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_OR_ID));
        assertTrue(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_PLATINE_ID));

        // Decrease: should downgrade.
        p.setTir3Pts(69);
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_PLATINE_ID));
        assertTrue(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_BRONZE_ID));

        p.setTir3Pts(0);
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_FER_ID));
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_BRONZE_ID));
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_OR_ID));
        assertFalse(badgeIds.contains(AutoSkillBadges.PRECISION_3PTS_PLATINE_ID));
    }

    @Test
    void syncAll_shouldAssignBadgesFromCurrentStats() {
        Player p = basePlayer();
        p.setBadgeIds(new HashSet<>());

        // Stats chosen to hit different ranks.
        p.setTir3Pts(90); // platine
        p.setTir2Pts(70); // or
        p.setFinitionAuCercle(50); // bronze (drive mapping)
        p.setTimingRebond(30); // fer (rebound mapping)
        p.setSteal(29); // none
        p.setTimingBlock(70); // or (contre mapping)
        p.setBasketballIqOff(30); // fer
        p.setDefExterieur(50); // bronze

        // Clear and resync to validate syncAll().
        p.getBadgeIds().clear();
        AutoSkillBadges.syncAll(p);

        assertTrue(p.getBadgeIds().contains(AutoSkillBadges.PRECISION_3PTS_PLATINE_ID));
        assertTrue(p.getBadgeIds().contains(AutoSkillBadges.PRECISION_2PTS_OR_ID));
        assertTrue(p.getBadgeIds().contains(AutoSkillBadges.DRIVE_BRONZE_ID));
        assertTrue(p.getBadgeIds().contains(AutoSkillBadges.REBOND_FER_ID));
        assertFalse(p.getBadgeIds().contains(AutoSkillBadges.INTERCEPTION_FER_ID));
        assertTrue(p.getBadgeIds().contains(AutoSkillBadges.CONTRE_OR_ID));
        assertTrue(p.getBadgeIds().contains(AutoSkillBadges.QI_BASKET_OFF_FER_ID));
        assertTrue(p.getBadgeIds().contains(AutoSkillBadges.DEF_EXTER_BRONZE_ID));
    }

    private static Player basePlayer() {
        return Player.builder()
                .teamsID(new HashSet<>())
                .clubID(null)
                .badgeIds(new HashSet<>())
                .id(UUID.randomUUID())
                .name("Test Player")
                .birthDate(1990)
                .injured(false)
                .tir3Pts(0)
                .tir2Pts(0)
                .lancerFranc(0)
                .floater(0)
                .finitionAuCercle(0)
                .speed(0)
                .ballhandling(0)
                .size(0)
                .weight(0)
                .agressivite(0)
                .defExterieur(0)
                .defPoste(0)
                .protectionCercle(0)
                .timingRebond(0)
                .agressiviteRebond(0)
                .steal(0)
                .timingBlock(0)
                .physique(0)
                .basketballIqOff(0)
                .basketballIqDef(0)
                .passingSkills(0)
                .iq(0)
                .endurance(0)
                .solidite(0)
                .potentielSkill(0)
                .potentielPhysique(0)
                .coachability(0)
                .ego(0)
                .softSkills(0)
                .leadership(0)
                .morale(0)
                .build();
    }
}

