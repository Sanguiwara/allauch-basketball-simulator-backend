package com.sanguiwara.progression;

import com.sanguiwara.baserecords.Player;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerProgressionDeltaTest {

    private static Player basePlayer(Set<Long> badgeIds) {
        int v = 50;
        return Player.builder()
                .id(UUID.randomUUID())
                .name("P")
                .birthDate(1990)
                .injured(false)
                .badgeIds(badgeIds)
                .tir3Pts(v)
                .tir2Pts(v)
                .lancerFranc(v)
                .floater(v)
                .finitionAuCercle(v)
                .speed(v)
                .ballhandling(v)
                .size(v)
                .weight(v)
                .agressivite(v)
                .defExterieur(v)
                .defPoste(v)
                .protectionCercle(v)
                .timingRebond(v)
                .agressiviteRebond(v)
                .steal(v)
                .timingBlock(v)
                .physique(v)
                .basketballIqOff(v)
                .basketballIqDef(v)
                .passingSkills(v)
                .iq(v)
                .endurance(v)
                .solidite(v)
                .potentielSkill(v)
                .potentielPhysique(v)
                .coachability(v)
                .ego(v)
                .softSkills(v)
                .leadership(v)
                .morale(v)
                .build();
    }

    @Test
    void between_whenBadgesUnchanged_returnsNullBadgeDeltas() {
        Player before = basePlayer(Set.of(1L, 2L));
        Player after = basePlayer(Set.of(1L, 2L));

        PlayerProgressionDelta delta = PlayerProgressionDelta.between(before, after);
        assertEquals(Set.of(), delta.badgesAdded());
        assertEquals(Set.of(), delta.badgesRemoved());
    }

    @Test
    void between_whenBadgeAdded_returnsBadgesAddedOnly() {
        Player before = basePlayer(Set.of(1L));
        Player after = basePlayer(Set.of(1L, 2L));

        PlayerProgressionDelta delta = PlayerProgressionDelta.between(before, after);
        assertEquals(Set.of(2L), delta.badgesAdded());
        assertEquals(Set.of(), delta.badgesRemoved());
    }

    @Test
    void between_whenBadgeRemoved_returnsBadgesRemovedOnly() {
        Player before = basePlayer(Set.of(1L, 2L));
        Player after = basePlayer(Set.of(2L));

        PlayerProgressionDelta delta = PlayerProgressionDelta.between(before, after);
        assertEquals(Set.of(), delta.badgesAdded());
        assertEquals(Set.of(1L), delta.badgesRemoved());
    }
}
